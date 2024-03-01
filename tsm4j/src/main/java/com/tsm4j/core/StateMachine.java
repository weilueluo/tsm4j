package com.tsm4j.core;

public interface StateMachine<I, O> {

    Id getId();

    StateMachineResult<O> run(NextState<I> initState);

    interface Id {
        String getName();
    }
}
