package com.tsm4j;

import com.tsm4j.statetypes.StateType;
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
    private final Set<State<?>> states = new HashSet<>(Collections.singletonList(NextState.leaf().getState()));
    private final Map<Class<?>, ExceptionHandler<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    public static <I, O> StateMachineBuilder<I, O> create(String name) {
        return new StateMachineBuilder<>(StateMachineId.of(name));
    }

    public <T> State<T> newTransitionState(String name) {
        return this.newTransitionState(name, State.DEFAULT_PRECEDENCE);
    }

    public <T> State<T> newTransitionState(String name, int order) {
        return this.newState(name, StateTypes.TRANSITION, order);
    }

    public State<O> newOutputState(String name, int order) {
        return this.newState(name, StateTypes.OUTPUT, order);
    }

    public State<O> newOutputState(String name) {
        return this.newOutputState(name, State.DEFAULT_PRECEDENCE);
    }

    private <T> State<T> newState(String name, StateType type, int order) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        State<T> state = State.of(StateId.of(name, type, order));
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

    public final <T> void addTransition(State<T> state, Transition<T> transition, int order) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        if (!this.states.contains(state)) {
            throw new IllegalArgumentException(String.format("State is not defined in this state machine, state=%s", state.getId()));
        }
        state.addTransition(transition, order);
    }

    public final <T> void addTransition(State<T> state, Transition<T> transition) {
        this.addTransition(state, transition, Transition.DEFAULT_PRECEDENCE);
    }

    public StateMachine<I, O> build() {
        return StateMachine.of(stateMachineId, states, exceptionHandlerMap);
    }
}
