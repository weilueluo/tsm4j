package com.tsm4j.core;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StateMachineImpl<S extends Enum<S>> implements StateMachine<S> {

    private final DependencyQueue<ObjectContainer<S>, S> stateQueue;
    private final DependencyQueue<ObjectContainer<List<StateListener<S>>>, S> listenerQueue;
    private final StateMachineContextImpl<S> context;

    /*
    * Below keeps inputs for toBuilder method only
    * */
    final Map<S, List<Set<S>>> _stateDepsMap;
    final Map<Set<S>, List<StateListener<S>>> _listenerMap;
    final Set<S> _allStates;

    StateMachineImpl(Map<S, List<Set<S>>> stateDepsMap, Map<Set<S>, List<StateListener<S>>> listenerMap, Set<S> allStates) {
        this.stateQueue = createStateQueue(stateDepsMap);
        this.listenerQueue = new DependencyQueue<>(reverseMapping(listenerMap));
        this.context = new StateMachineContextImpl<>(allStates);

        // create an immutable copy for converting to builder
        this._stateDepsMap = new HashMap<>(stateDepsMap);
        this._listenerMap = new HashMap<>(listenerMap);
        this._allStates = new HashSet<>(allStates);
    }

    private static <S extends Enum<S>> DependencyQueue<ObjectContainer<S>, S> createStateQueue(Map<S, List<Set<S>>> stateDepsMap) {
        Map<ObjectContainer<S>, Set<S>> newStateDeps = new HashMap<>();
        // each state can be reached from multiple different set of dependencies
        // but DependencyMap is for modelling a key mapping to a single set of dependency only
        // so we create different keyContainers with the same underlying key (but different hash)
        // to stimulate the behaviour of one key having multiple set of dependencies
        for (Map.Entry<S, List<Set<S>>> depEntry : stateDepsMap.entrySet()) {
            S dependent = depEntry.getKey();
            for (Set<S> deps : depEntry.getValue()) {
                ObjectContainer<S> keyContainer = new ObjectContainer<>(dependent);
                newStateDeps.put(keyContainer, deps);
            }
        }
        return new DependencyQueue<>(newStateDeps);
    }

    private static <K, V> Map<ObjectContainer<List<V>>, K> reverseMapping(Map<K, List<V>> kvMap) {
        Map<K, ObjectContainer<List<V>>> listKeyMap = new HashMap<>();
        kvMap.forEach((k, v) -> listKeyMap.put(k, new ObjectContainer<>(v)));

        Map<ObjectContainer<List<V>>, K> reverseMap = new HashMap<>();
        listKeyMap.forEach((k, v) -> reverseMap.put(v, k));
        return reverseMap;
    }

    public StateMachineContext<S> send(List<S> states) {
        this.queue(states);
        return this.process();
    }

    @Override
    public StateMachineContext<S> send(S state) {
        return send(Collections.singletonList(state));
    }

    @Override
    public void queue(List<S> states) {
        states.forEach(this::onStateReached);
    }

    @Override
    public StateMachineContext<S> process() {
        // while there is more state reached
        while (!this.stateQueue.isEmpty()) {
            // get the next reached state
            S state = this.stateQueue.pop().get();
            // notify state reached
            this.onStateReached(state);
        }
        return this.context;
    }

    @Override
    public Set<S> getAllStates() {
        return this._allStates;
    }

    @Override
    public StateMachineBuilder<S> toBuilder() {
        return StateMachineBuilderImpl.from(this);
    }

    private void onStateReached(S state) {
        // mark state satisfied
        this.stateQueue.satisfy(state);
        // notify context
        this.context.onStateReached(state);
        // call listeners
        listenerQueue.satisfy(state);
        while (!listenerQueue.isEmpty()) {
            listenerQueue.pop().get().forEach(listener -> listener.accept(this.context));
        }
        // consume any state queued by the user
        this.context.consumeQueuedStates(this.stateQueue::satisfy);
    }
}
