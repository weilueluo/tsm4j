package com.tsm4j.core;

public interface State<T> {
    String getName();

    /*
     * Returns the next state to be queued to run
     * */
    NextState<T> of(T data);

    /*
     * Similar to {@link State#of(T data)}, but use null as input.
     * */
    NextState<T> of();

    boolean isOutput();

    boolean isLeaf();
}
