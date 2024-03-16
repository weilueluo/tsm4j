package com.tsm4j.core;

@FunctionalInterface
public interface VoidTransition<T> extends Transition<T> {

    @Override
    default NextState<?> apply(T data, Context context) {
        return this.apply();
    }

    NextState<?> apply();
}
