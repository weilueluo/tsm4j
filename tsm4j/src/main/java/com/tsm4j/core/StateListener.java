package com.tsm4j.core;

@FunctionalInterface
public interface StateListener<S extends Enum<S>> {

    void accept(StateMachineContext<S> context);
}
