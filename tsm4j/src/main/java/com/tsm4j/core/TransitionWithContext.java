package com.tsm4j.core;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;

@FunctionalInterface
public interface TransitionWithContext<T> extends BiFunction<T, ExecutionContext, NextState<?>> {
    default Set<State<?>> requiredStates() {
        return Collections.emptySet();
    }
}
