package com.tsm4j.core;

import com.tsm4j.core.statetypes.AbstractStateType;
import com.tsm4j.core.statetypes.StateType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StateLeaf implements State<Void> {

    public static final StateLeaf INSTANCE = new StateLeaf();

    private static final String NAME = "LEAF_STATE";

    @Override
    public State.Id getId() {
        return Id.INSTANCE;
    }

    @Override
    public NextState<Void> of(Void data) {
        return NextStateLeaf.INSTANCE;
    }

    @Override
    public int compareTo(State<?> o) {
        if (o == null) {
            return -1;  // push null to end (for no reason)
        }
        return getId().compareTo(o.getId());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Id implements State.Id {

        private static final Id INSTANCE = new Id();

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public AbstractStateType getType() {
            return StateType.LEAF;
        }

        @Override
        public int getOrder() {
            return Order.LOWEST_PRECEDENCE;
        }

        @Override
        public int compareTo(State.Id o) {
            if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1;  // push null to end (for no reason)
            }
            return 1;  // push leaf to end, but before null (for no reason)
        }
    }
}
