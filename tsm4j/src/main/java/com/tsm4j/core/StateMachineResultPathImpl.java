package com.tsm4j.core;

import java.util.LinkedList;
import java.util.List;

class StateMachineResultPathImpl<O> implements StateMachineResultPath<O> {

    private final List<NextState<?>> path;
    private final NextStateImpl<O> outputState;


    StateMachineResultPathImpl(List<NextState<?>> path, NextStateImpl<O> outputState) {
        this.path = path;
        this.outputState = outputState;
    }

    StateMachineResultPathImpl(StateMachineResultPathImpl<?> other, NextStateImpl<O> outputState) {
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
    public NextStateImpl<O> getOutputState() {
        return outputState;
    }
}
