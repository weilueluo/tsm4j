package com.tsm4j.core;

import java.util.Set;

@FunctionalInterface
public interface Transition<T> extends TransitionWithContext<T> {

    static <R> Transition<R> from(Transition<R> transition, Set<State<?>> requiredStates) {
        return new Transition<R>() {

            @Override
            public Set<State<?>> requiredStates() {
                return requiredStates;
            }

            @Override
            public NextState<?> apply(R r) {
                return transition.apply(r);
            }
        };
    }

    @Override
    default NextState<?> apply(T t, ExecutionContext context) {
        return apply(t);
    }

    NextState<?> apply(T t);
}
