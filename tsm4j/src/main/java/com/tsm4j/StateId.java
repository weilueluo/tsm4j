package com.tsm4j;

import com.tsm4j.statetypes.StateType;
import lombok.Value;

@Value(staticConstructor = "of")
class StateId implements Comparable<StateId> {

    static final int HIGHEST_PRECEDENCE = Integer.MAX_VALUE;
    static final int LOWEST_PRECEDENCE = Integer.MIN_VALUE;
    static final int DEFAULT_PRECEDENCE = 0;

    String name;
    StateType type; // same name but different type is valid
    int order;

    @Override
    public int compareTo(StateId o) {
        return o.order - this.order;
    }
}
