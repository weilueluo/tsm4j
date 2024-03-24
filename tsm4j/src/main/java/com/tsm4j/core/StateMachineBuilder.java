package com.tsm4j.core;


import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface StateMachineBuilder {
    static StateMachineBuilder newInstance() {
        return new StateMachineBuilderImpl();
    }

    <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, BiConsumer<Context, RE> exceptionHandler);

    <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, Consumer<RE> exceptionHandler);

    <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, Runnable exceptionHandler);

    <T> StateMachineBuilder addTransition(State<T> state, Runnable transition);

    <T> StateMachineBuilder addTransition(State<T> state, Consumer<Context> transition);

    StateMachineBuilder addTransition(Set<State<?>> states, Runnable transition);

    StateMachineBuilder addTransition(Set<State<?>> states, Consumer<Context> transition);

    StateMachine build();
}
