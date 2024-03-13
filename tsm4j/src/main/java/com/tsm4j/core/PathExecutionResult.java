package com.tsm4j.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PathExecutionResult<I, O> {
    private final List<StateMachinePath<?, I, O>> nextPaths;
    private final boolean executed;
}
