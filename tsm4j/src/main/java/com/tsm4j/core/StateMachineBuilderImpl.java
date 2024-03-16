package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineBuilderImpl<O> implements StateMachineBuilder<O> {

    private final Set<State<?>> states = new HashSet<>();
    private final Set<State<O>> outputStates = new HashSet<>();
    private final Map<Class<?>, ContextExceptionHandler<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    /// ADD STATE

    @Override
    public <T> State<T> addState(String name) {
        return this.addState(name, false, Collections.emptySet());
    }

    @Override
    public <T> State<T> addState() {
        return this.addState("unnamed-" + states.size());
    }

    @Override
    public State<O> addOutputState(String name) {
        final State<O> state = this.addState(name, true, Collections.emptySet());
        this.outputStates.add(state);
        return state;
    }

    @Override
    public State<O> addOutputState() {
        return this.addOutputState("unnamed-output-" + outputStates.size());
    }

    private <T> State<T> addState(String name, boolean isOutput, Set<State<?>> requiredStates) {
        Objects.requireNonNull(name);
        final State<T> state = new StateImpl<>(name, isOutput, requiredStates);
        if (this.states.contains(state)) {
            throw new IllegalArgumentException("State already exists, state=" + state);
        }
        this.states.add(state);
        return state;
    }

    /// ADD TRANSITIONS

    @Override
    public <T> void addTransition(State<T> state, InputTransition<T> transition) {
        this.addTransition(state, (Transition<T>) transition);
    }

    @Override
    public <T> void addTransition(State<T> state, InputTransition<T> transition, Set<State<?>> requiredStates) {
        this.addTransition(state, (Transition<T>) transition, requiredStates);
    }

    @Override
    public <T> void addTransition(State<T> state, Transition<T> transition) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        ((StateImpl<T>) state).addTransition(transition);
    }

    @Override
    public <T> void addTransition(State<T> state, Transition<T> transition, Set<State<?>> requiredStates) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        ((StateImpl<T>) state).addTransition(Transition.of(transition, requiredStates));
    }

    @Override
    public <T> void addTransition(State<T> state, ContextTransition<T> transition) {
        this.addTransition(state, ((Transition<T>) transition));
    }

    @Override
    public <T> void addTransition(State<T> state, ContextTransition<T> transition, Set<State<?>> requiredStates) {
        this.addTransition(state, (Transition<T>) transition, requiredStates);
    }

    @Override
    public <T> void addTransition(State<T> state, VoidTransition<T> transition) {
        this.addTransition(state, ((Transition<T>) transition));
    }

    @Override
    public <T> void addTransition(State<T> state, VoidTransition<T> transition, Set<State<?>> requiredStates) {
        this.addTransition(state, (Transition<T>) transition, requiredStates);
    }

    /// ADD EXCEPTION HANDLER

    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler) {
        addExceptionHandler(clazz, (ContextExceptionHandler<E>) exceptionHandler);
    }

    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, VoidExceptionHandler<E> exceptionHandler) {
        addExceptionHandler(clazz, (ContextExceptionHandler<E>) exceptionHandler);
    }

    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ContextExceptionHandler<E> exceptionHandler) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(exceptionHandler);
        if (this.exceptionHandlerMap.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("Exception handler of given class is already defined, class=%s", clazz));
        }
        this.exceptionHandlerMap.put(clazz, exceptionHandler);
    }

    @Override
    public StateMachine<O> build() {
        return new StateMachineImpl<>(states, outputStates, exceptionHandlerMap);
    }
}
