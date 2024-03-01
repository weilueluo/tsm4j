package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateMachineIdImpl implements StateMachineId {
    private final String name;

    @Override
    public String getName() {
        return name;
    }
}