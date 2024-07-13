package com.tsm4j.core;

import java.util.Set;

public interface StateMachineContext<S extends Enum<S>> {

    void queue(S state);

    int getCount(S state);

    boolean reached(S state);

    Set<S> getAllStates();

    S getLatestState();
}
