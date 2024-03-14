package com.tsm4j.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Set;

@Getter
class StateMachineTransition<T> {

    private final String id;
    private final TransitionWithContext<T> transition;
    private final T data;
    private final ArrayList<State<?>> path;

    StateMachineTransition(String id, TransitionWithContext<T> transition, T data, ArrayList<State<?>> path) {
        this.id = id;
        this.transition = transition;
        this.data = data;
        this.path = path;
    }

    Set<State<?>> getRequiredStates() {
        return this.transition.requiredStates();
    }

    <I, O> NextState<?> apply(ExecutionContextImpl<I, O> executionContext) {
        return this.transition.apply(this.data, executionContext);
    }
}
