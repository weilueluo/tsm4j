package com.tsm4j.core;

class NextStateLeaf extends NextStateImpl<Void> {

    static final NextStateLeaf INSTANCE = new NextStateLeaf(StateLeaf.INSTANCE, null);

    protected NextStateLeaf(StateImpl<Void> state, Void data) {
        super(state, data);
    }
}
