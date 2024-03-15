package com.tsm4j.core;

import java.util.HashSet;
import java.util.Set;

@FunctionalInterface
public interface Transition<T> extends TransitionWithContext<T> {

    static <R> Transition<R> from(Transition<R> transition, Set<State<?>> requiredStates) {
        final Set<State<?>> combined = new HashSet<>();
        combined.addAll(transition.requiredStates());
        combined.addAll(requiredStates);
        return new Transition<R>() {
            @Override
            public Set<State<?>> requiredStates() {
                return combined;
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
