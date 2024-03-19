package com.tsm4j.core;

import java.util.Set;

public interface StateMachine<E extends Enum<E>> {
    Context<E> send(E state);

    Context<E> send(Set<E> states);
}
