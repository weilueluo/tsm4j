package com.tsm4j.core;

import com.tsm4j.core.map.DependencyValueMap;

import java.util.LinkedList;
import java.util.Set;

public class TransitionQueue<I, O> {

    private final DependencyValueMap<String, State<?>, StateMachineTransition<?>> transitionDependencyMap;
    private final LinkedList<StateMachineTransition<?>> freeTransitionQueue;

    TransitionQueue() {
        this.freeTransitionQueue = new LinkedList<>();
        this.transitionDependencyMap = new DependencyValueMap<>();
    }

    StateMachineTransition<?> pop() {
        return freeTransitionQueue.pop();
    }

    boolean isEmpty() {
        return this.freeTransitionQueue.isEmpty();
    }

    void consume(StateMachinePath<?, I, O> path) {
        Set<StateMachineTransition<?>> freedTransitions = transitionDependencyMap.removeDependency(path.getState()); // we reached path containing this state, so remove it as dependency
        this.freeTransitionQueue.addAll(freedTransitions);
        path.getTransitions().forEach(this::add);
    }

    private void add(StateMachineTransition<?> transition) {
        if (!this.transitionDependencyMap.addDependencies(transition.getId(), transition.getRequiredStates())) {
            // dependencies are already configured before for this transition
            if (this.transitionDependencyMap.isFree(transition.getId())) {
                // already satisfied
                this.freeTransitionQueue.add(transition);
            }
        }
        // else dependencies not satisfied
        // put on waiting list
        this.transitionDependencyMap.addValue(transition.getId(), transition);
    }
}
