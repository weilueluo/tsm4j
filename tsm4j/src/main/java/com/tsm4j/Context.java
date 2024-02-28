package com.tsm4j;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

// state machine context information
// add any metadata here
@Getter
@RequiredArgsConstructor
public class Context {

    private final LocalDateTime startTime = LocalDateTime.now();

    @Getter
    private final StateMachineId stateMachineId;

}
