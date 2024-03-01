package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeafNextState implements NextState<Void> {

    static final LeafNextState INSTANCE = new LeafNextState();

    @Override
    public State<Void> getState() {
        return LeafState.INSTANCE;
    }

    @Override
    public Void getData() {
        return null;
    }
}
