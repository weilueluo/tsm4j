package com.tsm4j.core;

import com.tsm4j.core.map.DependencyMap;

import javax.swing.plaf.nimbus.State;
import java.util.LinkedList;
import java.util.Set;

class PathQueue<E extends Enum<E>> {

    private final DependencyMap<E, E> dependencyMap;
    private final LinkedList<Path<?>> queue;

    PathQueue(Set<State<?>> states) {
        this.dependencyMap = new DependencyMap<>();
        this.queue = new LinkedList<>();

        states.forEach(state -> this.dependencyMap.addDependencies(state, ((StateImpl<?>) state).getRequiredStates()));
    }

    boolean isEmpty() {
        return this.queue.isEmpty();
    }

    Path<?> pop() {
        return this.queue.pop();
    }

    void add(Path<?> path) {
        final State<?> reachedState = path.getState();

        // we have reached state on this path, try release state that depend on this state
        final Set<Path<?>> freedPaths = this.dependencyMap.removeDependency(reachedState);
        this.queue.addAll(freedPaths);

        // try to put on waiting list, if cannot it means it is already satisfied
        // if this state is already satisfied, then we can add it directly
        if (!this.dependencyMap.addValue(reachedState, path)) {
            this.queue.add(path);
        }
    }
}
