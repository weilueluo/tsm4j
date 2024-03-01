package com.tsm4j.core;

import com.tsm4j.core.ExceptionHandler;
import com.tsm4j.core.ExceptionHandlerWithContext;
import com.tsm4j.core.LeafState;
import com.tsm4j.core.Order;
import com.tsm4j.core.State;
import com.tsm4j.core.StateImpl;
import com.tsm4j.core.StateMachine;
import com.tsm4j.core.StateMachineBuilder;
import com.tsm4j.core.StateMachineImpl;
import com.tsm4j.core.Transition;
import com.tsm4j.core.TransitionWithContext;
import com.tsm4j.core.statetypes.StateType;
import com.tsm4j.core.statetypes.AbstractStateType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineBuilderImpl<I, O> implements StateMachineBuilder<I, O> {

    private final StateMachine.Id stateMachineId;
    private final Map<State<?>, List<TransitionWithContext<?>>> stateToTransitionsMap = new HashMap<State<?>, List<TransitionWithContext<?>>>(){{
        put(LeafState.INSTANCE, new ArrayList<>());  // leaf state should be present in all state machine by default, because state are checked to be presents in current state machine
    }};
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap = new HashMap<>();

    @Override
    public <T> State<T> newTransitionState(String name) {
        return this.newTransitionState(name, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public <T> State<T> newTransitionState(String name, int order) {
        return this.newState(name, StateType.TRANSITION, order);
    }

    public State<O> newOutputState(String name, int order) {
        return this.newState(name, StateType.OUTPUT, order);
    }

    public State<O> newOutputState(String name) {
        return this.newOutputState(name, Order.DEFAULT_PRECEDENCE);
    }

    private <T> State<T> newState(String name, AbstractStateType type, int order) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        State<T> state = StateImpl.of(new StateImpl.Id(name, type, order));
        if (!this.stateToTransitionsMap.containsKey(state)) {
            this.stateToTransitionsMap.put(state, new ArrayList<>());
            return state;
        } else {
            throw new IllegalArgumentException(String.format("State already exists, state=%s", state.getId()));
        }
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
    public <E extends RuntimeException> void addExceptionHandler(Class<E> clazz, ExceptionHandler<E> exceptionHandler) {
        addExceptionHandler(clazz, (ExceptionHandlerWithContext<E>) exceptionHandler);
    }


    @Override
    public <T> void addTransition(State<T> state, TransitionWithContext<T> transition, int order) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);
        if (!this.stateToTransitionsMap.containsKey(state)) {
            throw new IllegalArgumentException(String.format("State is not defined in this state machine, state=%s", state.getId()));
        }
        this.stateToTransitionsMap.get(state).add(transition);
    }

    @Override
    public <T> void addTransition(State<T> state, Transition<T> transition, int order) {
        this.addTransition(state, (TransitionWithContext<T>) transition, order);
    }

    @Override
    public <T> void addTransition(State<T> state, TransitionWithContext<T> transition) {
        this.addTransition(state, transition, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public <T> void addTransition(State<T> state, Transition<T> transition) {
        this.addTransition(state, (TransitionWithContext<T>) transition, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public StateMachine<I, O> build() {
        return StateMachineImpl.of(stateMachineId, stateToTransitionsMap, exceptionHandlerMap);
    }
}
