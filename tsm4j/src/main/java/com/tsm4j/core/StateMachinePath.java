package com.tsm4j.core;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Represents a path in the state machine
 * */
@Getter
class StateMachinePath<T> {

    private final T data;
    private final ArrayList<State<?>> path;
    private final StateImpl<T> state;

    StateMachinePath(@NonNull ArrayList<State<?>> prevStates, @NonNull StateImpl<T> newState, T data) {
        this.state = newState;
        this.data = data;
        this.path = new ArrayList<>(prevStates);
        this.path.add(newState);
    }

    StateMachinePath(@NonNull ArrayList<State<?>> oldPath, @NonNull NextStateImpl<T> nextState) {
        this(oldPath, nextState.getState(), nextState.getData());
    }

    boolean isOutput() {
        return this.state.isOutput();
    }

    boolean isLeaf() {
        return this.state.isLeaf();
    }


    List<StateMachineTransition<T>> getTransitions() {
        List<StateMachineTransition<T>> transitions = new ArrayList<>();
        for (Map.Entry<String, TransitionWithContext<T>> entry : state.getTransitionsMap().entrySet()) {
            transitions.add(new StateMachineTransition<>(entry.getKey(), entry.getValue(), this.data, this.path));
        }
        return transitions;
    }
}
