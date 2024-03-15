package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.Set;

public class TransitionQueue<I, O> {

    private final DependencyValueMap<String, State<?>, StateMachineTransition<?>> dependencyMap;
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
        this.dependencyMap.addDependencies(transition.getId(), transition.getRequiredStates());
        if (this.dependencyMap.isFree(transition.getId())) {
            // already satisfied
            this.availableQueue.add(transition);
        } else {
            // else dependencies not satisfied
            // put on waiting list
            this.dependencyMap.addValue(transition.getId(), transition);
        }
    }
}
