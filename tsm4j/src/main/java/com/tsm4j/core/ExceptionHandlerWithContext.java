package com.tsm4j.core;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExceptionHandler<E extends RuntimeException> extends BiFunction<E, ContextImpl, NextStateImpl<?>> {
}
