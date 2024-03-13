package com.tsm4j.core;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PathQueue<I, O> {

    private final Set<State<?>> validStates;
    private final Map<State<?>, Set<State<?>>> rs2sMap;
    private final Map<State<?>, Set<State<?>>> s2rsMap;
    private final Map<State<?>, Set<StateMachinePath<?, I, O>>> pendingPathsMap;
    private final LinkedList<StateMachinePath<?, I, O>> releasedQueue;

    PathQueue(Set<State<?>> validStates) {
        this.validStates = new HashSet<>(validStates);
        this.validStates.add(NextState.leaf().getState());
        this.rs2sMap = new HashMap<>();
        this.s2rsMap = new HashMap<>();
        this.pendingPathsMap = new HashMap<>();
        this.releasedQueue = new LinkedList<>();

        this.validStates.forEach(this::require);
    }

    boolean isEmpty() {
        return this.releasedQueue.isEmpty();
    }

    StateMachinePath<?, I, O> pop() {
        return this.releasedQueue.pop();
    }

    void addAll(List<StateMachinePath<?, I, O>> paths) {
        paths.forEach(this::add);
    }

    void add(StateMachinePath<?, I, O> path) {
        if (!validStates.contains(path.getState())) {
            // probably a transition returned a state from another state machine
            throw new IllegalArgumentException("Cannot add path containing an invalid state: " + path.getState());
        }
        // we have reached this path, so release this state
        notRequired(path.getState());

        if (isReleased(path.getState())) {
            // this path is ready to be consumed
            releasedQueue.add(path);
            return;
        }

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

    private void require(@NonNull State<?> state) {
        Set<State<?>> requiredStates = s2rsMap.get(state);
        Set<State<?>> newRequiredStates = ((StateImpl<?>) state).getRequiredStates();

        // handle s2rsMap
        if (requiredStates != null) {
            requiredStates.addAll(newRequiredStates);
        } else {
            s2rsMap.put(state, new HashSet<>(((StateImpl<?>) state).getRequiredStates()));
        }

        // handle rs2sMap
        newRequiredStates.forEach(rs -> {
            Set<State<?>> states = rs2sMap.get(rs);
            if (states != null) {
                states.add(state);
            } else {
                states = new HashSet<>();
                states.add(state);
                rs2sMap.put(rs, states);
            }
        });
    }

    private void notRequired(@NonNull StateImpl<?> requiredState) {
        List<StateMachinePath<?, I, O>> releasedPaths = new ArrayList<>();
        Set<State<?>> states = rs2sMap.remove(requiredState);
        if (states != null) {
            // iterate through all states that depend on this required state
            for (State<?> state : states) {
                Set<State<?>> requiredStates = s2rsMap.get(state);
                if (requiredStates != null) {
                    // remove this required state from all states that depends on it
                    requiredStates.remove(requiredState);
                    if (requiredStates.isEmpty()) {
                        // if this required state is the last required state of this state, add all pending path of this state to the queue
                        s2rsMap.remove(state);
                        Set<StateMachinePath<?, I, O>> released = pendingPathsMap.remove(state);
                        if (released != null) {
                            releasedPaths.addAll(released);
                        }
                    }
                }
            }
        }
        this.releasedQueue.addAll(releasedPaths);
    }

    private boolean isReleased(StateImpl<?> state) {
        Set<State<?>> requiredStates = s2rsMap.get(state);
        if (requiredStates == null) {
            return true;
        } else if (requiredStates.isEmpty()) {
            s2rsMap.remove(state);
            return true;
        } else {
            return false;
        }
    }
}
