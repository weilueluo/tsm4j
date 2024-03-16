package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class StateMachineTransition<T> {

    @EqualsAndHashCode.Include
    private final String id;
    private final Transition<T> transition;
    private final T data;
    private final ArrayList<State<?>> path;

    StateMachineTransition(String id, Transition<T> transition, T data, ArrayList<State<?>> path) {
        this.id = id;
        this.transition = transition;
        this.data = data;
        this.path = path;
    }

    Set<State<?>> getRequiredStates() {
        return this.transition.requiredStates();
    }

    <I, O> NextState<?> apply(ContextImpl<I, O> executionContext) {
        return this.transition.apply(this.data, executionContext);
    }
}
