package com.tsm4j.core;

import com.tsm4j.core.map.DependencyMap;

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
        this.dependencyMap.satisfy(state);

        Set<StateMachineTransition<?>> freedTransitions = dependencyMap.removeDependency(path.getState()); // we reached path containing this state, so remove it as dependency
        this.availableQueue.addAll(freedTransitions);
        path.getTransitions().forEach(this::add);
    }

    private void add(StateMachineTransition<?> transition) {
        this.dependencyMap.addDependencies(transition, transition.getRequiredStates());
        if (!this.dependencyMap.addValue(transition, transition)) {
            // already satisfied
            this.availableQueue.add(transition);
        }
    }
}
