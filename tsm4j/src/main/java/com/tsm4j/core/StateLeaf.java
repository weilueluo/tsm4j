package com.tsm4j.core;


class StateLeaf extends StateImpl<Void> {

    public static final StateLeaf INSTANCE = new StateLeaf();

    private static final String NAME = "LEAF_STATE";

    StateLeaf() {
        super(NAME, false, false, new Requirements(new State[]{}));
    }

    @Override
    public NextState<Void> of(Void data) {
        return NextStateLeaf.INSTANCE;
    }

    @Override
    public void addTransition(Transition<Void> transition) {
        throw new UnsupportedOperationException("leaf state has no transition");
    }

    @Override
    public void addTransition(TransitionWithContext<Void> transition) {
        throw new UnsupportedOperationException("leaf state has no transition");
    }

}
