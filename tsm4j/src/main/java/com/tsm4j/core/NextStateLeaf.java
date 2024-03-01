package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NextStateLeaf implements NextState<Void> {

    static final NextStateLeaf INSTANCE = new NextStateLeaf();

    @Override
    public State<Void> getState() {
        return StateLeaf.INSTANCE;
    }

    @Override
    public Void getData() {
        return null;
    }
}
