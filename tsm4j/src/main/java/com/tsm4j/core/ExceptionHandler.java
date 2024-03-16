package com.tsm4j.core;


@FunctionalInterface
public interface ExceptionHandler<E extends RuntimeException> extends ContextExceptionHandler<E> {

    @Override
    default NextState<?> apply(E e, Context context) {
        return apply(e);
    }

    NextState<?> apply(E e);
}
