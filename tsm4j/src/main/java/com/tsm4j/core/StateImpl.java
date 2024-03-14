package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
class StateImpl<T> implements State<T> {

    @ToString.Include
    @EqualsAndHashCode.Include
    private final String name;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final boolean isInput;
    @ToString.Include
    @EqualsAndHashCode.Include
    private final boolean isOutput;
    private final Set<State<?>> requiredStates;
    private final Map<String, TransitionWithContext<T>> transitionsMap;  // id to transition map

    StateImpl(
            String name,
            boolean isInput,
            boolean isOutput,
            Set<State<?>> requiredStates
    ) {
        this.name = name;
        this.isOutput = isOutput;
        this.isInput = isInput;
        this.requiredStates = requiredStates;
        this.transitionsMap = new HashMap<>();
    }

    public boolean isLeaf() {
        return this.transitionsMap.isEmpty();
    }

    public boolean isInput() {
        return this.isInput;
    }

    public boolean isOutput() {
        return this.isOutput;
    }

    public NextState<T> of(T data) {
        return NextStateImpl.of(this, data);
    }

    void addTransition(Transition<T> transition) {
        this.addTransition((TransitionWithContext<T>) transition);
    }

    void addTransition(TransitionWithContext<T> transition) {
        this.transitionsMap.put(name + "transition-" + transitionsMap.size(), transition);
    }
}
