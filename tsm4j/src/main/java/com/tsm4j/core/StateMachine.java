package com.tsm4j.core;

public interface StateMachine<O> {
    <T> Execution<T, O> send(NextState<T> initState);
}
