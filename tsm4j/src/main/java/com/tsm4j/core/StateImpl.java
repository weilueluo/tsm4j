package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
class StateImpl<T> implements State<T> {

    private static int STATE_COUNTER = 0;
    private final String name;

    StateImpl() {
        this(getNextName());
    }

    private static String getNextName() {
        STATE_COUNTER++;
        return "state-" + STATE_COUNTER;
    }
}
