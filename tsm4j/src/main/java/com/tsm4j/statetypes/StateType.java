package com.tsm4j.statetypes;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class StateType {
    public boolean isTransition() {
        return false;
    }

    public boolean isOutput() {
        return false;
    }

    public boolean isLeaf() {
        return false;
    }
}

