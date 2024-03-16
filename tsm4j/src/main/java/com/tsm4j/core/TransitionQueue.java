package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.Set;

public class TransitionQueue {

    private final DependencyValueMap<StateMachineTransition<?>, State<?>, StateMachineTransition<?>> dependencyMap;
    private final LinkedList<StateMachineTransition<?>> availableQueue;

    TransitionQueue() {
        this.availableQueue = new LinkedList<>();
        this.dependencyMap = new DependencyValueMap<>();
    }

    StateMachineTransition<?> pop() {
        return availableQueue.pop();
    }

    boolean isEmpty() {
        return this.availableQueue.isEmpty();
    }

    void consume(StateMachinePath<?> path) {
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
