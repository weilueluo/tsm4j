package com.tsm4j.core.statetype;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Output extends AbstractStateType {
    public static final Output INSTANCE = new Output();

    @Override
    public boolean isOutput() {
        return true;
    }
}
