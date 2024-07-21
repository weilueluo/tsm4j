package com.tsm4j.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class StateMachineContextImpl<S extends Enum<S>> implements StateMachineContext<S> {

    private final Map<S, Integer> stateCountMap;

    private final Set<S> allStates;

    private final LinkedList<S> queuedStates;

    private S lastState;

    StateMachineContextImpl(Set<S> allStates) {
        this.stateCountMap = new HashMap<>();
        this.allStates = allStates;
        this.queuedStates= new LinkedList<>();
    }

    @Override
    public void queue(S state) {
        this.queuedStates.add(state);
    }

    void consumeQueuedStates(Consumer<S> stateConsumer) {
        while (!this.queuedStates.isEmpty()) {
            stateConsumer.accept(this.queuedStates.pop());
        }
    }

    @Override
    public int getReachedCount(S state) {
        return this.stateCountMap.getOrDefault(state, 0);
    }

    @Override
    public boolean reached(S state) {
        return getReachedCount(state) != 0;
    }

    @Override
    public Set<S> getAllStates() {
        return this.allStates;
    }

    @Override
    public S getLatestState() {
        return this.lastState;
    }


    void onStateReached(S state) {
        this.stateCountMap.compute(state, (s, count) -> count == null ? 1 : count + 1);
        this.lastState = state;
    }
}
