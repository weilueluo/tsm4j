package com.tsm4j.core;

import com.tsm4j.core.queue.DependencyMap;
import lombok.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PathQueue<I, O> {

    private final Set<State<?>> validStates;
    private final DependencyMap<State<?>, State<?>> stateDependencyMap;
    private final Map<State<?>, Set<StateMachinePath<?, I, O>>> pendingPathsMap;
    private final LinkedList<StateMachinePath<?, I, O>> freePathQueue;

    PathQueue(Set<State<?>> validStates) {
        this.validStates = new HashSet<>(validStates);
        this.validStates.add(NextState.leaf().getState());
        this.stateDependencyMap = new DependencyMap<>();
        this.pendingPathsMap = new HashMap<>();
        this.freePathQueue = new LinkedList<>();

        this.validStates.forEach(this::require);
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
        if (!validStates.contains(path.getState())) {
            // probably a transition returned a state from another state machine
            throw new IllegalArgumentException("Cannot add path containing an invalid state: " + path.getState());
        }
        // we have reached this path, so remove this state as dependency
        release(path.getState());

        // if this state has no dependency, then we can add it directly
        if (isReleased(path.getState())) {
            freePathQueue.add(path);
        } else {
            // this path is not ready yet, some required states not met.
            // add to pending map
            if (pendingPathsMap.containsKey(path.getState())) {
                pendingPathsMap.get(path.getState()).add(path);
            } else {
                Set<StateMachinePath<?, I, O>> paths = new HashSet<>();
                paths.add(path);
                pendingPathsMap.put(path.getState(), paths);
            }
        }
    }

    private void require(@NonNull State<?> state) {
        this.stateDependencyMap.addDependencies(state, ((StateImpl<?>) state).getRequiredStates());
    }

    private void release(@NonNull StateImpl<?> requiredState) {
        Set<State<?>> freedStates = this.stateDependencyMap.removeDependency(requiredState);
        freedStates.forEach(state -> {
            Set<StateMachinePath<?, I, O>> released = pendingPathsMap.remove(state);
            if (released != null) {
                this.freePathQueue.addAll(released);
            }
        });
    }

    private boolean isReleased(StateImpl<?> state) {
        return this.stateDependencyMap.isFree(state);
    }
}
