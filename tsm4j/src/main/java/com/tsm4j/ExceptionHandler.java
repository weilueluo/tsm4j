package com.tsm4j;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExceptionHandler<E extends RuntimeException> extends BiFunction<E, Context, NextState<?>> {
}
