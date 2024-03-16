package com.tsm4j.core;


@FunctionalInterface
public interface VoidExceptionHandler<E extends RuntimeException> extends ContextExceptionHandler<E> {

    @Override
    default NextState<?> apply(E e, Context context) {
        return apply();
    }

    NextState<?> apply();
}
