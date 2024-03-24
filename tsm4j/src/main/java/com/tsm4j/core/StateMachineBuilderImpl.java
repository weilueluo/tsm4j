package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineBuilderImpl implements StateMachineBuilder {

    private final Map<NamedTransition, Set<State<?>>> transitionMap;
    private final Map<Class<? extends RuntimeException>, BiConsumer<Context, ? extends RuntimeException>> exceptionHandlerMap;

    public StateMachineBuilderImpl() {
        this.exceptionHandlerMap = new HashMap<>();
        this.transitionMap = new HashMap<>();
    }


    @Override
    public <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, BiConsumer<Context, RE> exceptionHandler) {
        this.exceptionHandlerMap.put(clazz, exceptionHandler);
        return this;
    }

    @Override
    public <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, Consumer<RE> exceptionHandler) {
        this.addExceptionHandler(clazz, (c, re) -> exceptionHandler.accept(re));
        return this;
    }

    @Override
    public <RE extends RuntimeException> StateMachineBuilder addExceptionHandler(Class<RE> clazz, Runnable exceptionHandler) {
        this.addExceptionHandler(clazz, (c, re) -> exceptionHandler.run());
        return this;
    }

    @Override
    public <T> StateMachineBuilder addTransition(State<T> state, Runnable transition) {
        this.addTransition(state, (c) -> transition.run());
        return this;
    }

    @Override
    public <T> StateMachineBuilder addTransition(State<T> state, Consumer<Context> transition) {
        this.addTransition(Collections.singleton(state), transition);
        return this;
    }

    @Override
    public StateMachineBuilder addTransition(Set<State<?>> states, Runnable transition) {
        return this.addTransition(states, (c) -> transition.run());
    }

    @Override
    public StateMachineBuilder addTransition(Set<State<?>> states, Consumer<Context> transition) {
        if (states.isEmpty()) {
            throw new IllegalArgumentException("States cannot be empty");
        }
        NamedTransition namedTransition = new NamedTransition(getNextTransitionName(), transition);
        this.transitionMap.put(namedTransition, states);
        return this;
    }

    @Override
    public StateMachine build() {
        return new StateMachineImpl(transitionMap, exceptionHandlerMap);
    }

    private String getNextTransitionName() {
        return "transition-" + this.transitionMap.size() + 1;
    }
}
