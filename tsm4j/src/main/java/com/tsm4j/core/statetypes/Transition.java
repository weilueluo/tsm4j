package com.tsm4j.core.statetypes;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
// represents a typical state that performs some operations
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transition extends AbstractStateType {
    public static final Transition INSTANCE = new Transition();

    @Override
    public boolean isTransition() {
        return true;
    }
}
