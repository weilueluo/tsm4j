package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    public static int HIGHEST_PRECEDENCE = Integer.MAX_VALUE;
    public static int LOWEST_PRECEDENCE = Integer.MIN_VALUE;
    public static int DEFAULT_PRECEDENCE = 0;
}
