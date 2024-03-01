package com.tsm4j.core;

import com.tsm4j.core.statetypes.StateTypeImpl;
import com.tsm4j.core.statetypes.StateTypes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StateMachineBuilder<I, O> {

    private final StateMachineId stateMachineId;
    private final Set<StateImpl<?>> states = new HashSet<>(Collections.singletonList(NextStateImpl.leaf().getState()));
    private final Map<Class<?>, ExceptionHandler<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    public static <I, O> StateMachineBuilder<I, O> create(String name) {
        return new StateMachineBuilder<>(StateMachineId.of(name));
    }

    public <T> StateImpl<T> newTransitionState(String name) {
        return this.newTransitionState(name, Order.DEFAULT_PRECEDENCE);
    }

    public <T> StateImpl<T> newTransitionState(String name, int order) {
        return this.newState(name, StateTypes.TRANSITION, order);
    }

    public StateImpl<O> newOutputState(String name, int order) {
        return this.newState(name, StateTypes.OUTPUT, order);
    }

    public StateImpl<O> newOutputState(String name) {
        return this.newOutputState(name, Order.DEFAULT_PRECEDENCE);
    }

    private <T> StateImpl<T> newState(String name, StateTypeImpl type, int order) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        StateImpl<T> state = StateImpl.of(StateId.of(name, type, order));
        if (this.states.add(state)) {
            return state;
        } else {
            throw new IllegalArgumentException(String.format("State already exists, state=%s", state.getId()));
        }
    }

    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(exceptionHandler);
        if (this.exceptionHandlerMap.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("Exception handler of given class is already defined, class=%s", clazz));
        }
        this.exceptionHandlerMap.put(clazz, exceptionHandler);
    }

    public final <T> void addTransition(StateImpl<T> state, TransitionWithContext<T> transition, int order) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        if (!this.states.contains(state)) {
            throw new IllegalArgumentException(String.format("State is not defined in this state machine, state=%s", state.getId()));
        }
        state.addTransition(transition, order);
    }

    public <T> void addTransition(StateImpl<T> state, Transition<T> transition, int order) {
       this.addTransition(state, (TransitionWithContext<T>) transition, order);
    }

    public <T> void addTransition(StateImpl<T> state, TransitionWithContext<T> transition) {
        this.addTransition(state, transition, Order.DEFAULT_PRECEDENCE);
    }

    public <T> void addTransition(StateImpl<T> state, Transition<T> transition) {
        this.addTransition(state, (TransitionWithContext<T>) transition, Order.DEFAULT_PRECEDENCE);
    }

    public StateMachine<I, O> build() {
        return StateMachine.of(stateMachineId, states, exceptionHandlerMap);
    }
}
