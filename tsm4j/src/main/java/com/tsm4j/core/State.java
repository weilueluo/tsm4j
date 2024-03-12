package com.tsm4j.core;

public interface State<T> {
    String getName();

    NextState<T> of(T data);

    boolean isInput();

    boolean isOutput();

    boolean isLeaf();
}
