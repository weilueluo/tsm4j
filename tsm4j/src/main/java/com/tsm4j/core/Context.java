package com.tsm4j.core;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

// state machine runtime information
public interface Context {
    LocalDateTime getStartTime();

    StateMachineId getId();

    <T> Optional<T> get(String name, Class<T> clazz);

    <T> Optional<T> get(Class<T> clazz);

    <T> T getOrError(String name, Class<T> clazz);

    <T> T getOrError(Class<T> clazz);

    <T> T getOrDefault(String name, Class<T> clazz, Supplier<T> defaultSupplier);

    <T> T getOrDefault(Class<T> clazz, Supplier<T> defaultSupplier);

    <T> void set(String name, Class<? extends T> clazz, T obj);

    <T> void set(Class<? extends T> clazz, T obj);

    <T> void set(String name, T obj);

    void set(Object obj);
}
