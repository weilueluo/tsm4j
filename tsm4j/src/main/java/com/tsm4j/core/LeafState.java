package com.tsm4j.core;

import com.tsm4j.core.statetypes.StateType;
import com.tsm4j.core.statetypes.AbstractStateType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeafState implements State<Void> {

    public static final LeafState INSTANCE = new LeafState();

    private static final String NAME = "LEAF_STATE";

    @Override
    public State.Id getId() {
        return Id.INSTANCE;
    }

    @Override
    public NextState<Void> of(Void data) {
        return LeafNextState.INSTANCE;
    }

    @Override
    public int compareTo(State<?> o) {
        if (o == null) {
            return -1;  // push null to end
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
                return -1;  // push null to last
            }
            return 1;  // push it to second last
        }
    }
}
