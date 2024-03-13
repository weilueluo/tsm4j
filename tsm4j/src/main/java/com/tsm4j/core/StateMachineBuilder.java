package com.tsm4j.core;


import java.util.Set;

public interface StateMachineBuilder<I, O> {

    static <I, O> StateMachineBuilder<I, O> create(String name) {
        return new StateMachineBuilderImpl<>(name);
    }

    <T> State<T> addState(String name);

    <T> State<T> addState(String name, Set<State<?>> requiredStates);

    State<O> addOutputState(String name);

    State<O> addOutputState(String name, Set<State<?>> requiredStates);

    State<I> addInputState(String name);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandlerWithContext<E> exceptionHandler);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler);

    <T> void addTransition(State<T> state, Transition<T> transition);

    <T> void addTransition(State<T> state, TransitionWithContext<T> transition);

    StateMachine<I, O> build();
}
