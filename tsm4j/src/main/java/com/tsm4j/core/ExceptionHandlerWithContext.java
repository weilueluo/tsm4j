package com.tsm4j.core;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExceptionHandlerWithContext<E extends RuntimeException> extends BiFunction<E, Context, NextState<?>> {
}
