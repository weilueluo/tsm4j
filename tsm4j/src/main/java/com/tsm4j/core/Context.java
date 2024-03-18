package com.tsm4j.core;

public interface Context<E> {
    void queue(E state);
}
