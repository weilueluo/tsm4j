package com.tsm4j.core;

import java.time.LocalDateTime;
import java.util.Set;

// state machine runtime information
public interface ExecutionContext {
    Set<State<?>> getStates();

    LocalDateTime getStartTime();

    String getName();
}
