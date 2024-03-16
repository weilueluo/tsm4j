package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.Set;

class PathQueue {

    private final DependencyValueMap<State<?>, State<?>, StateMachinePath<?>> dependencyMap;
    private final LinkedList<StateMachinePath<?>> freeQueue;

    PathQueue(Set<State<?>> states) {
        this.dependencyMap = new DependencyValueMap<>();
        this.freeQueue = new LinkedList<>();

        states.forEach(state -> this.dependencyMap.addDependencies(state, ((StateImpl<?>) state).getRequiredStates()));
    }

    boolean isEmpty() {
        return this.freeQueue.isEmpty();
    }

    StateMachinePath<?> pop() {
        return this.freeQueue.pop();
    }

    void add(StateMachinePath<?> path) {
        final State<?> reachedState = path.getState();

        // we have reached state on this path, try release state that depend on this state
        final Set<StateMachinePath<?>> freedPaths = this.dependencyMap.removeDependency(reachedState);
        this.freeQueue.addAll(freedPaths);

        // try to put on waiting list, if cannot it means it is already satisfied
        // if this state is already satisfied, then we can add it directly
        if (!this.dependencyMap.addValue(reachedState, path)) {
            this.freeQueue.add(path);
        }
    }
}
