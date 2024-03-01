package com.tsm4j;

import java.util.function.BiFunction;

@FunctionalInterface
public interface TransitionWithContext<T> extends BiFunction<T, Context, NextStateImpl<?>> {
}
