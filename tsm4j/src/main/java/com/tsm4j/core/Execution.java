package com.tsm4j.core;

import com.tsm4j.core.exception.StateNotReachedException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class Execution<I, O> {
    private final List<List<State<?>>> paths;
    private final List<O> outputs;
    private final State<I> inputState;
    private final I input;
    private final Map<State<?>, Object> stateDataMap;

    Execution(StateMachinePath<I, I, O> initPath) {
        this.paths = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.inputState = initPath.getState();
        this.input = initPath.getData();
        this.stateDataMap = new HashMap<>();

        this.notifyNewPath(initPath);
    }

    void notifyNewPath(@NonNull StateMachinePath<?, I, O> path) {
        if (path.isLeaf()) {
            // we got a complete path
            this.paths.add(path.getPath());
        }
        if (path.isOutput()) {
            // we got an output
            this.outputs.add((O) path.getData());
        }
        // record latest data for this path
        this.stateDataMap.put(path.getState(), path.getData());
    }

    public <T> Optional<T> get(State<T> state) {
        return Optional.ofNullable((T) this.stateDataMap.get(state));
    }

    public <T> T getOrError(State<T> state) {
        if (!this.stateDataMap.containsKey(state)) {
            throw new StateNotReachedException(state.toString());
        }
        return (T) this.stateDataMap.get(state);
    }

    public <T> T getOrDefault(State<T> state, Supplier<T> defaultSupplier) {
        return get(state).orElseGet(defaultSupplier);
    }

    public boolean isReached(State<?> state) {
        return this.stateDataMap.containsKey(state);
    }
}
