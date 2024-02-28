package com.tsm4j;

import lombok.Value;

@Value(staticConstructor = "of")
public class StateMachineId {
    String name;
}
