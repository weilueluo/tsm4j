package com.tsm4j.core;

import com.tsm4j.core.statetypes.AbstractStateType;

public interface State<T> extends Comparable<State<?>> {

    Id getId();

    NextState<T> of(T data);

    void addTransition(Transition<T> transition);

    void addTransition(TransitionWithContext<T> transition);

    void addTransition(Transition<T> transition, int order);

    void addTransition(TransitionWithContext<T> transition, int order);

    interface Id extends Comparable<Id> {
        String getName();

        AbstractStateType getType();

        int getOrder();
    }
}
