package com.tsm4j.core;

import java.time.LocalDateTime;

// state machine runtime information
public interface Context {
    LocalDateTime getStartTime();
    StateMachine.Id getStateMachineId();
}
