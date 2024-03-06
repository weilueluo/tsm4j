package com.tsm4j.core.statetype;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Leaf extends AbstractStateType {
    public static final Leaf INSTANCE = new Leaf();

    @Override
    public boolean isLeaf() {
        return true;
    }
}
