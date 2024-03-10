package com.tsm4j.core;

public interface StateMachine<I, O> {

    StateMachineId getId();

    StateMachineResult<O> send(NextState<I> initState);

}
