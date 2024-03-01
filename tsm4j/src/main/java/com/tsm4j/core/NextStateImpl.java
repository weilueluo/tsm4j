package com.tsm4j.core;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Supplier;

/*
 * NextState is just state with the input data
 * */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
class NextStateImpl<T> implements NextState<T> {

    private final State<T> state;
    private final T data;

    // NextState can only be constructed from here
    static <T> NextState<T> of(StateImpl<T> state, T data) {
        return new NextStateImpl<>(state, data);
    }
}
