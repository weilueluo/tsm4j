package com.tsm4j.core;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
class ExecutionContextImpl<I, O> implements ExecutionContext {
    private final LocalDateTime startTime = LocalDateTime.now();
    private final String name;
    private final Set<State<?>> states;
    private final Execution<I, O> execution;
    private final PathQueue<I, O> pathQueue;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;

    public ExecutionContextImpl(
            String name,
            Set<State<?>> states,
            Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap,
            StateMachinePath<I, I, O> initPath) {
        this.name = name;
        this.states = states;
        this.exceptionHandlerMap = exceptionHandlerMap;
        this.execution = new Execution<>(initPath.getState(), initPath.getData());
        this.pathQueue = new PathQueue<>(states);
        this.pathQueue.add(initPath);
    }

    public void recordOutput(O output) {
        this.execution.recordOutput(output);
    }

    public void recordPath(List<State<?>> path) {
        this.execution.recordPath(path);
    }
}
