package com.tsm4j.core.statetypes;

public abstract class AbstractStateType implements StateType {
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

