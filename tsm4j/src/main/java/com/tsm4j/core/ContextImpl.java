package com.tsm4j.core;

import com.tsm4j.core.exception.DataNotFoundException;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

class ContextImpl<E extends Enum<E>> implements Context<E> {

    private final Map<Class<?>, Object> dataStore;
    private final LinkedList<E> pendingQueue;
    private final TransitionQueue<E> readyQueue;
    private final Set<E> reachedStates;

    ContextImpl(Map<NamedTransition<E>, Set<E>> transitionMap) {
        this.dataStore = new HashMap<>();
        this.pendingQueue = new LinkedList<>();
        this.readyQueue = new TransitionQueue<>(transitionMap);
        this.reachedStates = new HashSet<>();
    }

    @Override
    public void send(E state) {
        this.reachedStates.add(state);
        this.pendingQueue.add(state);
    }

    boolean isEmpty() {
        updateReadyQueue();
        return readyQueue.isEmpty();
    }

    NamedTransition<E> pop() {
        updateReadyQueue();
        return readyQueue.pop();
    }

    void updateReadyQueue() {
        while (!pendingQueue.isEmpty()) {
            readyQueue.add(pendingQueue.pop());
        }
    }

    @Override
    public boolean hasReached(E state) {
        return this.reachedStates.contains(state);
    }

    @Override
    public Set<E> getReached() {
        return Collections.unmodifiableSet(this.reachedStates);
    }

    @Override
    public <T> void put(@NonNull T data) {
        this.dataStore.put(data.getClass(), data);
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable((T) this.dataStore.get(clazz));
    }

    @Override
    public <T> T getOrError(Class<T> clazz) {
        return get(clazz).orElseThrow(() -> new DataNotFoundException(clazz.getSimpleName()));
    }

    @Override
    public <T> T getOrDefault(Class<T> clazz, Supplier<T> defaultSupplier) {
        return this.get(clazz).orElseGet(defaultSupplier);
    }
}
