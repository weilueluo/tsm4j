package com.tsm4j.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Execution<I, O> {
    private final List<List<State<?>>> paths = new ArrayList<>();
    private final List<O> outputs = new ArrayList<>();
    private final State<I> inputState;
    private final I input;

    void recordPath(@NonNull List<State<?>> path) {
        paths.add(path);
    }

    void recordOutput(O output) {
        outputs.add(output);
    }
}
