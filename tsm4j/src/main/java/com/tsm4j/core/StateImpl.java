package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString(exclude = "transitions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
class StateImpl<T> implements State<T> {

    @EqualsAndHashCode.Include
    private final String name;
    private final List<TransitionWithContext<T>> transitions = new ArrayList<>();
    @EqualsAndHashCode.Include
    private final boolean isInput;
    @EqualsAndHashCode.Include
    private final boolean isOutput;
    private final Requirements requirements;

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
