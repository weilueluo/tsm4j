package com.tsm4j.core;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Context {
    void send(State<Void> state);

    <T> void send(State<T> state, T data);

    boolean hasReached(State<?> state);

    Set<State<?>> getReached();

    <T> Optional<T> get(State<T> state);

    <T> T getOrError(State<T> state);

    <T> T getOrDefault(State<T> state, Supplier<T> defaultSupplier);
}
