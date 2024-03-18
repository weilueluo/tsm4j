package com.tsm4j.core;

public interface StateMachine<E> {
    Context<E> send(E initState);
}
