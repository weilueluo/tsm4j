package com.tsm4j.core;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Context<E> {
    void send(E state);

    boolean hasReached(E state);

    Set<E> getReached();

    <T> void put(T data);

    <T> Optional<T> get(Class<T> clazz);

    <T> T getOrError(Class<T> clazz);

    <T> T getOrDefault(Class<T> clazz, Supplier<T> defaultSupplier);
}
