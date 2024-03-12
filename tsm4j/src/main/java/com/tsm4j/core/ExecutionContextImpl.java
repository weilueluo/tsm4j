package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
class ExecutionContextImpl<I, O> implements ExecutionContext {
    private final LocalDateTime startTime = LocalDateTime.now();

    private final String name;
    private final Set<State<?>> states;
    private final Set<State<I>> inputStates;
    private final Set<State<O>> outputStates;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;

    private final List<Execution<I, O>> executions;
    private Execution<I, O> currentExecution;


    Execution<I, O> startNewExecution(I input) {
        this.currentExecution = new Execution<>(input);
        return this.currentExecution;
    }

    Execution<I, O> endCurrentExecution() {
        this.executions.add(this.currentExecution);
        Execution<I, O> execution = this.currentExecution;
        this.currentExecution = null;
        return execution;
    }
}
