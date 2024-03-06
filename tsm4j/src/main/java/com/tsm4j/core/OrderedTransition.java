package com.tsm4j.core;

import lombok.Value;

@Value
class OrderedTransition<T> implements Comparable<OrderedTransition<?>> {
    TransitionWithContext<T> transition;
    int order;

    @Override
    public int compareTo(OrderedTransition<?> o) {
        return o.order - this.order;
    }
}
