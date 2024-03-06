package com.tsm4j.core;

public interface StateMachine<I, O> {

    StateMachineId getId();

    StateMachineResult<O> run(NextState<I> initState);

}
