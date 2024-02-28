package com.tsm4j;


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
public class NextState<T> {

    private final State<T> state;
    private final T data;

    // NextState can only be constructed from here
    static <T> NextState<T> of(State<T> state, T data) {
        return new NextState<>(state, data);
    }

    public static NextState<Void> leaf() {
        return of(State.of(StateId.of("VOID", StateTypes.LEAF, State.DEFAULT_PRECEDENCE)), null);
    }

    public List<Supplier<NextState<?>>> getOrderedNextStateSuppliers(Context context) {
        return state.getOrderedNextStateSuppliers(data, context);
    }
}
