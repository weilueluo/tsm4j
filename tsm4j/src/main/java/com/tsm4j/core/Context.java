package com.tsm4j.core;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/*
 * Context stores state machine runtime information
 * */
public interface Context {
    /*
     * Returns the states of the state machine
     * */
    Set<State<?>> getStates();

    /*
     * Returns the start time of the state machine
     * */
    LocalDateTime getStartTime();

    /*
     * Return the latest data of the given state, wrapped in optional.
     * An empty optional means the data associated with this state is null, or the state is not yet reached.
     * */
    <T> Optional<T> get(State<T> state);

    /*
     *  Returns the latest data of the given state, error if the state is not yet reached.
     *  This method can still return null if the data reached this state is null.
     * */
    <T> T getOrError(State<T> state);

    /*
     *  Returns the latest data of the given state, or use the default supplier if the state is not yet reached, or the data associated with this state is null.
     * */
    <T> T getOrDefault(State<T> state, Supplier<T> defaultSupplier);

    /*
     * Returns whether given state is already reached at the point of time of calling this method.
     * */
    boolean isReached(State<?> state);
}