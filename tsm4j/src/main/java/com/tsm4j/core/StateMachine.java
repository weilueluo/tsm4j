package com.tsm4j.core;

public interface StateMachine<I, O> {

    String getName();

    Execution<I, O> send(NextState<I> initState);
}
