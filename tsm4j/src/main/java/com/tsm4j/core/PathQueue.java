package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class PathQueue<I, O> {

    private final DependencyValueMap<State<?>, State<?>, StateMachinePath<?, I, O>> dependencyMap;
    private final LinkedList<StateMachinePath<?, I, O>> freeQueue;

    PathQueue(Set<State<?>> states) {
        this.dependencyMap = new DependencyValueMap<>();
        this.freeQueue = new LinkedList<>();

        states.forEach(state -> this.dependencyMap.addDependencies(state, ((StateImpl<?>) state).getRequiredStates()));
    }

    boolean isEmpty() {
        return this.freeQueue.isEmpty();
    }

    StateMachinePath<?, I, O> pop() {
        return this.freeQueue.pop();
    }

    void addAll(List<StateMachinePath<?, I, O>> paths) {
        paths.forEach(this::add);
    }

    void add(StateMachinePath<?, I, O> path) {
        final State<?> reachedState = path.getState();

        // we have reached state on this path, try release state that depend on this state
        final Set<StateMachinePath<?, I, O>> freedPaths = this.dependencyMap.removeDependency(reachedState);
        this.freeQueue.addAll(freedPaths);

        // if this state is already satisfied, then we can add it directly
        if (this.dependencyMap.isFree(reachedState)) {
            this.freeQueue.add(path);
        } else {
            // this path is not ready yet, there are states it depends on not reached.
            // put on waiting list
            this.dependencyMap.addValue(reachedState, path);
        }
    }

}
