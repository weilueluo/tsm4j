package com.tsm4j.core;

import java.util.List;

public interface StateMachineResult<O> {

    List<StateMachineResultPath<O>> getOutputPaths();

    List<StateMachineResultPath<?>> getLeafPaths();

    List<O> getOutputs();
}
