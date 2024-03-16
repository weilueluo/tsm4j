package com.tsm4j.core;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


@Getter
class ContextImpl<I, O> implements Context {

    private final LocalDateTime startTime = LocalDateTime.now();
    private final Set<State<?>> states;
    private final ExecutionImpl<I, O> execution;
    private final PathQueue pathQueue;
    private final TransitionQueue transitionQueue;
    private final Map<Class<?>, ContextExceptionHandler<? extends RuntimeException>> exceptionHandlerMap;

    public ContextImpl(
            Set<State<?>> states,
            Map<Class<?>, ContextExceptionHandler<? extends RuntimeException>> exceptionHandlerMap,
            StateMachinePath<I> initPath) {
        this.states = states;
        this.exceptionHandlerMap = exceptionHandlerMap;
        this.execution = new ExecutionImpl<>(initPath);
        this.transitionQueue = new TransitionQueue();
        this.pathQueue = new PathQueue(states);
        this.pathQueue.add(initPath);
    }

    void notify(StateMachinePath<?> path) {
        this.execution.onNewPath(path);
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
