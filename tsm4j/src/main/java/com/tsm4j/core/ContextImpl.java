package com.tsm4j.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
class ContextImpl implements Context {
    private final LocalDateTime startTime = LocalDateTime.now();
    private final StateMachineId id;
}
