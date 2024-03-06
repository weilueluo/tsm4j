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

    @Override
    public void addTransition(Transition<Void> transition, int order) {
        throw new UnsupportedOperationException("leaf state has no transition");
    }

    @Override
    public void addTransition(TransitionWithContext<Void> transition, int order) {
        throw new UnsupportedOperationException("leaf state has no transition");
    }

    @Override
    public int compareTo(State<?> o) {
        if (o == null) {
            return -1;  // push null to end (for no reason)
        }
        return getId().compareTo(o.getId());
    }

    private static final class Id extends StateImpl.Id {

        private static final Id INSTANCE = new Id(NAME, StateType.LEAF, Order.LOWEST_PRECEDENCE);


        public Id(String name, AbstractStateType type, int order) {
            super(name, type, order);
        }
    }
}
