package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnumStateMachineBuilderImpl<E extends Enum<E>> implements EnumStateMachineBuilder<E> {

    private final EnumSet<E> states;
    private final Map<NamedTransition<E>, Set<E>> transitionMap;
    private final Map<Class<? extends RuntimeException>, ContextExceptionHandler<? extends RuntimeException, E>> exceptionHandlerMap;

    public EnumStateMachineBuilderImpl(@NonNull Class<E> clazz) {
        this.states = EnumSet.noneOf(clazz);
        this.exceptionHandlerMap = new HashMap<>();
        this.transitionMap = new HashMap<>();
    }

    /// ADD STATE

    public EnumStateMachineBuilder<E> addState(@NonNull E state) {
        this.states.add(state);
        return this;
    }

    @Override
    public EnumStateMachineBuilder<E> addStates(@NonNull Class<E> clazz) {
        this.states.addAll(EnumSet.allOf(clazz));
        return this;
    }

    /// ADD EXCEPTION HANDLER

    @Override
    public <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(@NonNull Class<RE> clazz, @NonNull ContextExceptionHandler<RE, E> exceptionHandler) {
        this.exceptionHandlerMap.put(clazz, exceptionHandler);
        return this;
    }

    @Override
    public <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(Class<RE> clazz, ExceptionHandler<RE> exceptionHandler) {
        return this.addExceptionHandler(clazz, (re, e) -> exceptionHandler.accept(re));
    }

    @Override
    public <RE extends RuntimeException> EnumStateMachineBuilder<E> addExceptionHandler(Class<RE> clazz, VoidExceptionHandler exceptionHandler) {
        return this.addExceptionHandler(clazz, (re, e) -> exceptionHandler.run());
    }

    /// ADD TRANSITION

    @Override
    public EnumStateMachineBuilder<E> addTransition(E state, Transition<E> transition) {
        final NamedTransition<E> namedTransition = new NamedTransition<>(getNextTransitionName(), transition);
        this.transitionMap.put(namedTransition, Collections.singleton(state));
        return this;
    }

    @Override
    public EnumStateMachineBuilder<E> addTransition(E state, VoidTransition transition) {
        return this.addTransition(state, (c) -> transition.run());
    }


    /// ADD CONDITIONS

    @Override
    public EnumStateMachineBuilder<E> addTransition(Set<E> states, VoidTransition transition) {
        return this.addTransition(states, (c) -> transition.run());
    }

    @Override
    public EnumStateMachineBuilder<E> addTransition(@NonNull Set<E> states, @NonNull Transition<E> transition) {
        final NamedTransition<E> namedTransition = new NamedTransition<>(getNextTransitionName(), transition);
        this.transitionMap.put(namedTransition, new HashSet<>(states));
        return this;
    }

    @Override
    public StateMachine<E> build() {
        return new EnumStateMachineImpl<>(states, transitionMap, exceptionHandlerMap);
    }

    private String getNextTransitionName() {
        return "transition-" + this.transitionMap.size() + 1;
    }
}
