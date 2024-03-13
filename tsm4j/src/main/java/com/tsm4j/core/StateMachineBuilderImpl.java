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
class StateMachineBuilderImpl<I, O> implements StateMachineBuilder<I, O> {

    private final String name;
    private final Set<State<?>> states = new HashSet<>();
    private final Set<State<I>> inputStates = new HashSet<>();
    private final Set<State<O>> outputStates = new HashSet<>();
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    @Override
    public <T> State<T> addState(String name) {
        return addState(name, Collections.emptySet());
    }

    @Override
    public <T> State<T> addState(String name, Set<State<?>> requiredStates) {
        return this.addState(name, false, false, requiredStates);
    }

    @Override
    public State<I> addInputState(String name) {
        final State<I> state = this.addState(name, true, false, Collections.emptySet());
        this.inputStates.add(state);
        return state;
    }

    @Override
    public State<O> addOutputState(String name) {
        return addOutputState(name, Collections.emptySet());
    }

    @Override
    public State<O> addOutputState(String name, Set<State<?>> requiredStates) {
        final State<O> state = this.addState(name, false, true, requiredStates);
        this.outputStates.add(state);
        return state;
    }

    private <T> State<T> addState(String name, boolean isInput, boolean isOutput, Set<State<?>> requiredStates) {
        Objects.requireNonNull(name);
        final State<T> state = new StateImpl<>(name, isInput, isOutput, requiredStates);
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

//    public static class StateBuilder<T> {
//        private final String name;
//        private List<State<?>> requiredTransitionedStates = new ArrayList<>();
//        private List<State<?>> requiredReachedStates = new ArrayList<>();
//
//        public StateBuilder(String name) {
//            this.name = name;
//        }
//
//        public StateBuilder<T> addRequiredReachedStates(List<State<?>> states) {
//            this.requiredReachedStates.addAll(states);
//            return this;
//        }
//
//        public StateBuilder<T> addRequiredTransitionedStates(List<State<?>> states) {
//            this.requiredTransitionedStates.addAll(states);
//            return this;
//        }
//
//        public State<?> build() {
//
//        }
//    }
}
