package com.tsm4j.core;

import java.util.function.BiFunction;

@FunctionalInterface
public interface TransitionWithContext<T> extends BiFunction<T, Context, NextState<?>> {
}
