package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;

public interface State<T> extends Comparable<State<?>> {

    Id getId();

    NextState<T> of(T data);

    interface Id extends Comparable<Id> {
        String getName();

        AbstractStateType getType();

        int getOrder();
    }
}
