package com.tsm4j.core;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


@Getter
class ExecutionContextImpl<I, O> implements ExecutionContext {
    private final LocalDateTime startTime = LocalDateTime.now();
    private final String name;
    private final Set<State<?>> states;
    private final Execution<I, O> execution;
    private final PathQueue<I, O> pathQueue;
    private final TransitionQueue<I, O> transitionQueue;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;

    public ExecutionContextImpl(
            String name,
            Set<State<?>> states,
            Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap,
            StateMachinePath<I, I, O> initPath) {
        this.name = name;
        this.states = states;
        this.exceptionHandlerMap = exceptionHandlerMap;
        this.execution = new Execution<>(initPath);
        this.transitionQueue = new TransitionQueue<>();
        this.pathQueue = new PathQueue<>(states);
        this.pathQueue.add(initPath);
    }

    void notifyNewPath(StateMachinePath<?, I, O> path) {
        this.execution.notifyNewPath(path);
    }

    @Override
    public <T> Optional<T> get(State<T> state) {
        return this.execution.get(state);
    }

    @Override
    public <T> T getOrError(State<T> state) {
        return this.execution.getOrError(state);
    }

    @Override
    public <T> T getOrDefault(State<T> state, Supplier<T> defaultSupplier) {
        return this.execution.getOrDefault(state, defaultSupplier);
    }

    @Override
    public boolean isReached(State<?> state) {
        return this.execution.isReached(state);
    }
}
