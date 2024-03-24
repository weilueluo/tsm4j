package com.tsm4j.core;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class TransitionQueue {

    private final DependencyMap<NamedTransition, State<?>> dependencyMap;
    private final LinkedList<NamedTransition> availableQueue;

    TransitionQueue(Map<NamedTransition, Set<State<?>>> transitionMap) {
        this.availableQueue = new LinkedList<>();
        this.dependencyMap = new DependencyMap<>(transitionMap);
    }

    NamedTransition pop() {
        return this.availableQueue.pop();
    }

    boolean isEmpty() {
        return this.availableQueue.isEmpty();
    }

    void add(State<?> state) {
        Set<NamedTransition> freeTransitions = this.dependencyMap.satisfy(state);
        this.availableQueue.addAll(freeTransitions);
    }
}
