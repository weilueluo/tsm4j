package com.tsm4j.core;

import java.util.Set;

public interface StateMachineBuilder<S extends Enum<S>> {

    StateMachineBuilder<S> addTransition(S fromState, S toState);

    StateMachineBuilder<S> addTransition(Set<S> requiredStates, S toStates);

    StateMachineBuilder<S> removeTransition(S fromState, S toState);

    StateMachineBuilder<S> removeTransition(Set<S> requiredStates, S toStates);

    StateMachineBuilder<S> addListener(S requiredState, StateListener<S> listener);

    StateMachineBuilder<S> addListener(Set<S> requiredStates, StateListener<S> listener);

    StateMachineBuilder<S> addListener(StateListener<S> listener);

    StateMachineBuilder<S> removeListener(Set<S> requiredStates, StateListener<S> listener);

    StateMachineBuilder<S> removeAllListeners(Set<S> requiredStates);

    StateMachine<S> build();

    static <S extends Enum<S>> StateMachineBuilder<S> from(Class<S> clazz) {
        return StateMachineBuilderImpl.statesFrom(clazz);
    }

    static <S extends Enum<S>> StateMachineBuilder<S> from(StateMachine<S> stateMachine) {
        return StateMachineBuilderImpl.from(stateMachine);
    }
}
