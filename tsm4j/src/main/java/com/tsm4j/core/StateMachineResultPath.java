package com.tsm4j.core;

import java.util.List;

public interface StateMachineResultPath<O> {
    List<NextState<?>> get();

    O getOutput();

    NextState<O> getOutputState();
}
