package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;

public interface State<T> {

    Id getId();

    NextState<T> of(T data);

    interface Id {
        String getName();

        AbstractStateType getType();
    }
}
