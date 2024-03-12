package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineModelBuilderImpl<I, O> implements StateMachineModelBuilder<I, O> {

    private final String name;
    private final Set<State<?>> states = new HashSet<>();
    private final Set<State<I>> inputStates = new HashSet<>();
    private final Set<State<O>> outputStates = new HashSet<>();
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    @Override
    public <T> State<T> addState(String name, State<?>... states) {
        return this.addState(name, false, false, states);
    }

    @Override
    public State<I> addInputState(String name, State<?>... states) {
        final State<I> state = this.addState(name, true, false, states);
        this.inputStates.add(state);
        return state;
    }

    @Override
    public State<O> addOutputState(String name, State<?>... states) {
        final State<O> state = this.addState(name, false, true, states);
        this.outputStates.add(state);
        return state;
    }

    private <T> State<T> addState(String name, boolean isInput, boolean isOutput, State<?>... states) {
        Objects.requireNonNull(name);
        final State<T> state = new StateImpl<>(name, isInput, isOutput, new Requirements(states));
        if (this.states.contains(state)) {
            throw new IllegalArgumentException("State already exists, state=" + state);
        }
        this.states.add(state);
        return state;
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

    @Override
    public <T> void addTransition(State<T> state, Transition<T> transition) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        ((StateImpl<T>) state).addTransition(transition);  // this cast is safe because we have only one implementing class
    }

    @Override
    public <T> void addTransition(State<T> state, TransitionWithContext<T> transition) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        ((StateImpl<T>) state).addTransition(transition);  // this cast is safe because we have only one implementing class
    }

    @Override
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler) {
        Objects.requireNonNull(exceptionHandler);
        addExceptionHandler(clazz, (ExceptionHandlerWithContext<E>) exceptionHandler);
    }

    @Override
    public StateMachine<I, O> build() {
        return new StateMachineImpl<>(name, states, inputStates, outputStates, exceptionHandlerMap);
    }
}
