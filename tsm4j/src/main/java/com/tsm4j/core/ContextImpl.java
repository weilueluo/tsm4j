package com.tsm4j.core.impl;

import com.tsm4j.core.StateMachineId;
import com.tsm4j.core.context.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ContextImpl implements Context {
    private final LocalDateTime startTime = LocalDateTime.now();
    private final StateMachineId stateMachineId;
}
