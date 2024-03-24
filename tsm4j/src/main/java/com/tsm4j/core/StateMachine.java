package com.tsm4j.core;

import java.util.Set;

public interface StateMachine {
    Context send(State<Void> state);

    <T> Context send(State<T> state, T data);

    Context send(Set<State<Void>> states);
}
