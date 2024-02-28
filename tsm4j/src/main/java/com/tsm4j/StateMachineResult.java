package com.tsm4j;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StateMachineResult<O> {

    @Singular
    private final List<LinkedList<NextState<?>>> outputPaths;
    @Singular
    private final List<LinkedList<NextState<?>>> leafPaths;
    @Singular
    private final List<LinkedList<NextState<?>>> implicitLeafPaths;

    @SuppressWarnings("unchecked")  // ensured by state machine builder, result state T always has data T
    public List<O> getOutputs() {
        return this.outputPaths.stream()
                .map(path -> (O) path.getLast().getData())
                .collect(Collectors.toList());
    }
}
