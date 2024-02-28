package com.tsm4j;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Transition<T> extends BiFunction<T, Context, NextState<?>> {
    int HIGHEST_PRECEDENCE = Integer.MAX_VALUE;
    int LOWEST_PRECEDENCE = Integer.MIN_VALUE;
    int DEFAULT_PRECEDENCE = 0;
}
