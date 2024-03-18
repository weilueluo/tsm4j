package com.tsm4j.core;


import java.util.Set;

public interface EnumStateMachineBuilder<E extends Enum<E>> {
    static <E extends Enum<E>> EnumStateMachineBuilder<E> newInstance(Class<E> clazz) {
        return new EnumStateMachineBuilderImpl<>(clazz);
    }

    EnumStateMachineBuilder<E> addState(E state);
    EnumStateMachineBuilder<E> addStates(Class<E> clazz);
    <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(Class<RE> clazz, ContextExceptionHandler<RE, E> exceptionHandler);
    <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(Class<RE> clazz, ExceptionHandler<RE> exceptionHandler);
    <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(Class<RE> clazz, VoidExceptionHandler exceptionHandler);
    EnumStateMachineBuilder<E> addTransition(E state, VoidTransition transition);
    EnumStateMachineBuilder<E> addTransition(E state, Transition<E> transition);
    EnumStateMachineBuilder<E> addTransition(Set<E> states, VoidTransition transition);
    EnumStateMachineBuilder<E> addTransition(Set<E> states, Transition<E> transition);

    StateMachine<E> build();
}
