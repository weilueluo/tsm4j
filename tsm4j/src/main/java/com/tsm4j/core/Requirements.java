package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Requirements {

    private final State<?>[] requiredStates;

    boolean isSatisfied(ExecutionContextImpl<?, ?> executionContext) {
        for (State<?> state : requiredStates) {
            if (executionContext.getCurrentExecution().getStateExecutedCount(state) == 0) {
                return false;
            }
        }
        return true;
    }
}
