package com.tsm4j.core;

import com.tsm4j.core.exception.DataNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

class ContextImpl implements Context {

    private final Map<State<?>, Object> dataStore;
    private final LinkedList<State<?>> pendingQueue;
    private final TransitionQueue readyQueue;

    ContextImpl(Map<NamedTransition, Set<State<?>>> transitionMap) {
        this.dataStore = new HashMap<>();
        this.pendingQueue = new LinkedList<>();
        this.readyQueue = new TransitionQueue(transitionMap);
    }

    @Override
    public void send(State<Void> state) {
        this.send(state, null);
    }

    @Override
    public <T> void send(State<T> state, T data) {
        this.pendingQueue.add(state);
        this.dataStore.put(state, data);
    }

    boolean isEmpty() {
        updateReadyQueue();
        return readyQueue.isEmpty();
    }

    NamedTransition pop() {
        updateReadyQueue();
        return readyQueue.pop();
    }

    void updateReadyQueue() {
        while (!pendingQueue.isEmpty()) {
            readyQueue.add(pendingQueue.pop());
        }
    }

    @Override
    public boolean hasReached(State<?> state) {
        return this.dataStore.containsKey(state);
    }

    @Override
    public Set<State<?>> getReached() {
        return Collections.unmodifiableSet(this.dataStore.keySet());
    }

    @Override
    public <T> Optional<T> get(State<T> state) {
        return Optional.ofNullable((T) this.dataStore.get(state));
    }

    @Override
    public <T> T getOrError(State<T> state) {
        return get(state).orElseThrow(() -> new DataNotFoundException(state.getName()));
    }

    @Override
    public <T> T getOrDefault(State<T> state, Supplier<T> defaultSupplier) {
        return this.get(state).orElseGet(defaultSupplier);
    }
}
