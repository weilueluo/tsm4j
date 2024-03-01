package com.tsm4j.core;

import com.tsm4j.core.Context;
import com.tsm4j.core.StateMachine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
class ContextImpl implements Context {
    private final LocalDateTime startTime = LocalDateTime.now();
    private final StateMachine.Id stateMachineId;
}
