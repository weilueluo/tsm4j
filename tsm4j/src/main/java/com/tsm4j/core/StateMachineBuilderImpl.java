package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;
import com.tsm4j.core.statetype.StateType;
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
        return this.newState(name, StateType.TRANSITION);
    }

    public State<O> newOutputState(String name) {
        return this.newState(name, StateType.OUTPUT);
    }

    private <T> State<T> newState(String name, AbstractStateType type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        return new StateImpl<>(new StateImpl.Id(name, type));
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

    public <T> void addTransition(State<T> state, Transition<T> transition) {
        ((StateImpl<T>) state).addTransition(transition);  // this cast is safe because we have only one implementing class
    }

    public <T> void addTransition(State<T> state, TransitionWithContext<T> transition) {
        ((StateImpl<T>) state).addTransition(transition);  // this cast is safe because we have only one implementing class
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
