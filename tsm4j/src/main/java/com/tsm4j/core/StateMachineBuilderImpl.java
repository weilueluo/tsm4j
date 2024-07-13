package com.tsm4j.core;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StateMachineBuilderImpl<S extends Enum<S>> implements StateMachineBuilder<S> {

    private final Set<S> allStates;

    private final Map<S, List<Set<S>>> stateDepsMap;  // state to its dependencies
    private final Map<Set<S>, List<StateListener<S>>> stateListenersMap;  // dependencies to their triggers

    StateMachineBuilderImpl(Set<S> allStates) {
        this(allStates, new HashMap<>(), new HashMap<>());
    }

    StateMachineBuilderImpl(Set<S> allStates, Map<S, List<Set<S>>> stateDepsMap, Map<Set<S>, List<StateListener<S>>> stateListenersMap) {
        this.allStates = allStates;
        this.stateDepsMap = stateDepsMap;
        this.stateListenersMap = stateListenersMap;
    }

    static <S extends Enum<S>> StateMachineBuilderImpl<S> statesFrom(Class<S> clazz) {
        Objects.requireNonNull(clazz);
        return new StateMachineBuilderImpl<>(EnumSet.allOf(clazz));
    }

    static <S extends Enum<S>> StateMachineBuilderImpl<S> from(StateMachine<S> stateMachine) {
        Objects.requireNonNull(stateMachine);
        StateMachineImpl<S> stateMachineImpl = (StateMachineImpl<S>) stateMachine;
        return new StateMachineBuilderImpl<>(stateMachineImpl._allStates, stateMachineImpl._stateDepsMap, stateMachineImpl._listenerMap);
    }

    /*
     * transitions related operations
     * */

    @Override
    public StateMachineBuilder<S> addTransition(S fromState, S toState) {
        return this.addTransition(Collections.singleton(fromState), toState);
    }

    @Override
    public StateMachineBuilder<S> addTransition(Set<S> requiredStates, S toState) {
        Objects.requireNonNull(requiredStates);
        Objects.requireNonNull(toState);
        if (!requiredStates.isEmpty()) {
            this.stateDepsMap.putIfAbsent(toState, new LinkedList<>());
            this.stateDepsMap.get(toState).add(Collections.unmodifiableSet(new HashSet<>(requiredStates)));
        }
        return this;
    }

    @Override
    public StateMachineBuilder<S> removeTransition(S fromState, S toState) {
        return this.removeTransition(Collections.singleton(fromState), toState);
    }

    @Override
    public StateMachineBuilder<S> removeTransition(Set<S> requiredStates, S toState) {
        if (this.stateDepsMap.containsKey(toState)) {
            this.stateDepsMap.get(toState).remove(requiredStates);
        }
        return this;
    }

    /*
     * listeners related operations
     * */

    @Override
    public StateMachineBuilder<S> addListener(S requiredState, StateListener<S> listener) {
        return this.addListener(Collections.singleton(requiredState), listener);
    }

    @Override
    public StateMachineBuilder<S> addListener(Set<S> requiredStates, StateListener<S> listener) {
        Set<S> immutableSets = Collections.unmodifiableSet(new HashSet<>(requiredStates));
        this.stateListenersMap.putIfAbsent(immutableSets, new LinkedList<>());
        this.stateListenersMap.get(immutableSets).add(listener);
        return this;
    }

    @Override
    public StateMachineBuilder<S> addListener(StateListener<S> listener) {
        this.allStates.forEach(state -> this.addListener(state, listener));
        return this;
    }

    @Override
    public StateMachineBuilder<S> removeListener(Set<S> requiredStates, StateListener<S> listener) {
        Objects.requireNonNull(requiredStates);
        if (this.stateListenersMap.containsKey(requiredStates)) {
            this.stateListenersMap.get(requiredStates).remove(listener);
        }
        return this;
    }

    @Override
    public StateMachineBuilder<S> removeAllListeners(Set<S> requiredStates) {
        Objects.requireNonNull(requiredStates);
        this.stateListenersMap.remove(requiredStates);
        return this;
    }

    @Override
    public StateMachine<S> build() {
        return new StateMachineImpl<>(stateDepsMap, stateListenersMap, allStates);
    }
}
