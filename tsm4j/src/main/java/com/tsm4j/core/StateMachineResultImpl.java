package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class StateMachineResultImpl<O> implements StateMachineResult<O> {

    @Singular
    private final List<StateMachineResultPath<O>> outputPaths;
    @Singular
    private final List<StateMachineResultPath<?>> leafPaths;

    public List<O> getOutputs() {
        return this.outputPaths
                .stream()
                .map(StateMachineResultPath::getOutput)
                .collect(Collectors.toList());
    }
}
