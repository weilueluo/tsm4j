package com.tsm4j.statetypes;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Leaf extends StateType {
    public static final Leaf INSTANCE = new Leaf();

    @Override
    public boolean isLeaf() {
        return true;
    }
}
