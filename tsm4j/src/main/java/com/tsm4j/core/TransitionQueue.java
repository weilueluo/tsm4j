package com.tsm4j.core;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class TransitionQueue<E extends Enum<E>> {

    private final DependencyMap<NamedTransition<E>, E> dependencyMap;
    private final LinkedList<NamedTransition<E>> availableQueue;

    TransitionQueue(Map<NamedTransition<E>, Set<E>> transitionMap) {
        this.availableQueue = new LinkedList<>();
        this.dependencyMap = new DependencyMap<>(transitionMap);
    }

    NamedTransition<E> pop() {
        return this.availableQueue.pop();
    }

    boolean isEmpty() {
        return this.availableQueue.isEmpty();
    }

    void add(E state) {
        Set<NamedTransition<E>> freeTransitions = this.dependencyMap.satisfy(state);
        this.availableQueue.addAll(freeTransitions);
    }
}
