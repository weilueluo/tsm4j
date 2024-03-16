package com.tsm4j.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Transition<T> extends BiFunction<T, Context, NextState<?>> {
    static <R> Transition<R> of(Transition<R> transition, Set<State<?>> requiredStates) {
        final Set<State<?>> combined = new HashSet<>();
        combined.addAll(transition.requiredStates());
        combined.addAll(requiredStates);
        return new Transition<R>() {

            @Override
            public Set<State<?>> requiredStates() {
                return combined;
            }

            @Override
            public NextState<?> apply(R r, Context context) {
                return transition.apply(r, context);
            }
        };
    }

    default Set<State<?>> requiredStates() {
        return Collections.emptySet();
    }
}
