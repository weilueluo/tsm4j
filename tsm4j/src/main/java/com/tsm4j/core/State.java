package com.tsm4j.core;

public interface State<T> {

    static <T> State<T> create() {
        return new StateImpl<>();
    }

    static <T> State<T> create(String name) {
        return new StateImpl<>(name);
    }

    String getName();

}
