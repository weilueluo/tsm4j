package com.tsm4j.core;

import java.util.UUID;

@FunctionalInterface
public interface Transition<T> extends TransitionWithContext<T> {

    String id = UUID.randomUUID().toString();

    @Override
    default NextState<?> apply(T t, ExecutionContext context) {
        return apply(t);
    }

    NextState<?> apply(T t);
}
