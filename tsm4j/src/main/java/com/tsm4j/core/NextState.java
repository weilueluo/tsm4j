package com.tsm4j.core;

/*
 * Represents the next state to be queued
 * */
public interface NextState<T> {

    /*
     * Returns a state representing the end of this path
     * */
    static NextState<Void> leaf() {
        return NextStateLeaf.INSTANCE;
    }

    State<T> getState();

    T getData();
}
