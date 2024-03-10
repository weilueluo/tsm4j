package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;
import com.tsm4j.core.statetype.StateType;
import lombok.NonNull;

class StateLeaf extends StateImpl<Void> {

    public static final StateLeaf INSTANCE = new StateLeaf(Id.INSTANCE);

    private static final String NAME = "LEAF_STATE";

    StateLeaf(StateImpl.@NonNull Id id) {
        super(id);
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

    private static final class Id extends StateImpl.Id {

        private static final Id INSTANCE = new Id(NAME, StateType.LEAF);


        public Id(String name, AbstractStateType type) {
            super(name, type);
        }
    }
}
