package com.tsm4j.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Execution<I, O> {
    private final Map<State<?>, Integer> stateExecutionCountMap = new HashMap<>();
    private final Map<State<?>, Integer> stateReachedCountMap = new HashMap<>();
    @Getter
    private final List<List<State<?>>> paths = new ArrayList<>();
    @Getter
    private final List<O> outputs = new ArrayList<>();
    @Getter
    private final I input;

    public int getStateExecutedCount(@NonNull State<?> state) {
        return stateExecutionCountMap.getOrDefault(state, 0);
    }

    public int getStateReachedCount(@NonNull State<?> state) {
        return stateReachedCountMap.getOrDefault(state, 0);
    }

    void recordExecuted(@NonNull State<?> state) {
        stateExecutionCountMap.merge(state, 1, Integer::sum);
    }

    void recordReached(@NonNull State<?> state) {
        stateReachedCountMap.merge(state, 1, Integer::sum);
    }

    void recordPath(@NonNull List<State<?>> path) {
        paths.add(path);
    }

    void recordOutput(O output) {
        outputs.add(output);
    }
}
