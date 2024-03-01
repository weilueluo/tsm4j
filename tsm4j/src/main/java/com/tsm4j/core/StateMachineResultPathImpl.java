package com.tsm4j.core;

import java.util.LinkedList;
import java.util.List;

class StateMachineResultPathImpl<O> implements StateMachineResultPath<O> {

    private final List<NextState<?>> path;
    private final NextState<O> outputState;


    StateMachineResultPathImpl(List<NextState<?>> path, NextState<O> outputState) {
        this.path = path;
        this.outputState = outputState;
    }

    StateMachineResultPathImpl(StateMachineResultPathImpl<?> other, NextState<O> outputState) {
        this.path = new LinkedList<>(other.path);
        this.path.add(outputState);
        this.outputState = outputState;
    }

    @Override
    public List<NextState<?>> get() {
        return this.path;
    }

    @Override
    public O getOutput() {
        return outputState.getData();
    }

    @Override
    public NextState<O> getOutputState() {
        return outputState;
    }
}
