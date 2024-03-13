package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
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
    private final List<TransitionWithContext<T>> transitions;

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
        this.transitions = new ArrayList<>();
    }

    public boolean isLeaf() {
        return this.transitions.isEmpty();
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
        this.transitions.add(transition);
    }

    void addTransition(TransitionWithContext<T> transition) {
        this.transitions.add(transition);
    }
}
