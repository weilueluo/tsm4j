package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class PathQueue<I, O> {

    private final DependencyValueMap<State<?>, State<?>, StateMachinePath<?, I, O>> stateDependencyMap;
    private final LinkedList<StateMachinePath<?, I, O>> freePathQueue;

    PathQueue(Set<State<?>> states) {
        this.stateDependencyMap = new DependencyValueMap<>();
        this.freePathQueue = new LinkedList<>();

        states.forEach(state -> this.stateDependencyMap.addDependencies(state, ((StateImpl<?>) state).getRequiredStates()));
    }

    boolean isEmpty() {
        return this.freePathQueue.isEmpty();
    }

    StateMachinePath<?, I, O> pop() {
        return this.freePathQueue.pop();
    }

    void addAll(List<StateMachinePath<?, I, O>> paths) {
        paths.forEach(this::add);
    }

    void add(StateMachinePath<?, I, O> path) {
        final State<?> reachedState = path.getState();

        // we have reached state on this path, try release state that depend on this state
        final Set<StateMachinePath<?, I, O>> freedPaths = this.stateDependencyMap.removeDependency(reachedState);
        this.freePathQueue.addAll(freedPaths);

        // if this state is already satisfied, then we can add it directly
        if (this.stateDependencyMap.isFree(reachedState)) {
            this.freePathQueue.add(path);
        } else {
            // this path is not ready yet, there are states it depends on not reached.
            this.stateDependencyMap.addValue(reachedState, path);
        }
    }

}
