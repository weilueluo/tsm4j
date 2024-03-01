package com.tsm4j.core.statetypes;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Output extends StateType {
    public static final Output INSTANCE = new Output();

    @Override
    public boolean isOutput() {
        return true;
    }
}
