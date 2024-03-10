package com.tsm4j.core;


public interface StateMachineBuilder<I, O> {

    static <I, O> StateMachineBuilder<I, O> create(String name) {
        return new StateMachineBuilderImpl<>(new StateMachineIdImpl(name));
    }

    <T> State<T> newTransitionState(String name);

    State<O> newOutputState(String name);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandlerWithContext<E> exceptionHandler);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler);

    <T> void addTransition(State<T> state, Transition<T> transition);

    <T> void addTransition(State<T> state, TransitionWithContext<T> transition);

    StateMachine<I, O> build();
}
