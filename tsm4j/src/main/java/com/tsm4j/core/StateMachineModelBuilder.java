package com.tsm4j.core;


public interface StateMachineModelBuilder<I, O> {

    static <I, O> StateMachineModelBuilder<I, O> create(String name) {
        return new StateMachineModelBuilderImpl<>(name);
    }

    <T> State<T> addState(String name, State<?>... states);

    State<O> addOutputState(String name, State<?>... states);

    State<I> addInputState(String name, State<?>... states);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandlerWithContext<E> exceptionHandler);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler);

    <T> void addTransition(State<T> state, Transition<T> transition);

    <T> void addTransition(State<T> state, TransitionWithContext<T> transition);

    StateMachine<I, O> build();
}
