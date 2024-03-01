package com.tsm4j.core;


public interface NextState<T> {

    static NextState<Void> leaf() {
        return NextStateLeaf.INSTANCE;
    }

    State<T> getState();

    T getData();
}
