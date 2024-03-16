package com.tsm4j.core;


import java.util.Set;

public interface StateMachineBuilder<O> {
    static <T> StateMachineBuilder<T> newInstance() {
        return new StateMachineBuilderImpl<>();
    }

    <T> State<T> addState();

    <T> State<T> addState(String name);

    State<O> addOutputState();

    State<O> addOutputState(String name);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ContextExceptionHandler<E> exceptionHandler);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler);

    <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, VoidExceptionHandler<E> exceptionHandler);

    <T> void addTransition(State<T> state, ContextTransition<T> transition);

    <T> void addTransition(State<T> state, ContextTransition<T> transition, Set<State<?>> requiredStates);

    <T> void addTransition(State<T> state, VoidTransition<T> transition);

    <T> void addTransition(State<T> state, VoidTransition<T> transition, Set<State<?>> requiredStates);

    <T> void addTransition(State<T> state, InputTransition<T> transition);

    <T> void addTransition(State<T> state, InputTransition<T> transition, Set<State<?>> requiredStates);

    <T> void addTransition(State<T> state, Transition<T> transition);

    <T> void addTransition(State<T> state, Transition<T> transition, Set<State<?>> requiredStates);

    StateMachine<O> build();
}
