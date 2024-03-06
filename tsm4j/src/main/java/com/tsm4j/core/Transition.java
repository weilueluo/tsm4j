package com.tsm4j.core;

@FunctionalInterface
public interface Transition<T> extends TransitionWithContext<T> {

    @Override
    default NextState<?> apply(T t, Context context) {
        return apply(t);
    }

    NextState<?> apply(T t);
}
