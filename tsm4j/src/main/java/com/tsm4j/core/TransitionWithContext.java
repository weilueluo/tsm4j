package com.tsm4j.core;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

@FunctionalInterface
public interface TransitionWithContext<T> extends BiFunction<T, ExecutionContext, NextState<?>> {

    static <R> TransitionWithContext<R> from(TransitionWithContext<R> transition, Set<State<?>> requiredStates) {
        return new TransitionWithContext<R>() {

            @Override
            public Set<State<?>> requiredStates() {
                return requiredStates;
            }

            @Override
            public NextState<?> apply(R r, ExecutionContext context) {
                return transition.apply(r, context);
            }
        };
    }

    default Set<State<?>> requiredStates() {
        return Collections.emptySet();
    }
}
