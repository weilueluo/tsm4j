package com.tsm4j.core;

import java.util.List;
import java.util.Set;

public interface StateMachine<S extends Enum<S>> {
    StateMachineContext<S> send(List<S> states);

    StateMachineContext<S> send(S state);

    void queue(List<S> states);

    StateMachineContext<S> process();

    Set<S> getAllStates();

    StateMachineBuilder<S> toBuilder();

    boolean reached(S state);

    int getReachedCount(S state);
}
