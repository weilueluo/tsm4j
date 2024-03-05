package com.tsm4j.core;

import com.tsm4j.core.statetypes.AbstractStateType;
import com.tsm4j.core.statetypes.StateType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineBuilderImpl<I, O> implements StateMachineBuilder<I, O> {

    private final StateMachineId stateMachineId;

    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    @Override
    public <T> State<T> newTransitionState(String name) {
        return this.newTransitionState(name, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public <T> State<T> newTransitionState(String name, int order) {
        return this.newState(name, StateType.TRANSITION, order);
    }

    public State<O> newOutputState(String name, int order) {
        return this.newState(name, StateType.OUTPUT, order);
    }

    public State<O> newOutputState(String name) {
        return this.newOutputState(name, Order.DEFAULT_PRECEDENCE);
    }

    private <T> State<T> newState(String name, AbstractStateType type, int order) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        return new StateImpl<>(new StateImpl.Id(name, type, order));
    }

    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandlerWithContext<E> exceptionHandler) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(exceptionHandler);
        if (this.exceptionHandlerMap.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("Exception handler of given class is already defined, class=%s", clazz));
        }
        this.exceptionHandlerMap.put(clazz, exceptionHandler);
    }


    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler) {
        addExceptionHandler(clazz, (ExceptionHandlerWithContext<E>) exceptionHandler);
    }

    @Override
    public StateMachine<I, O> build() {
        return new StateMachineImpl<>(stateMachineId, exceptionHandlerMap);
    }
}
