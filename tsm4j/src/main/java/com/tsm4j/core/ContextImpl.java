package com.tsm4j.core;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Getter
public class ContextImpl<E extends Enum<E>> implements Context<E> {

    private final Map<NamedTransition<E>, Set<E>> transitionMap;
    private final E initState;


    ContextImpl(Map<NamedTransition<E>, Set<E>> transitionMap, E initState) {
        this.transitionMap = transitionMap;
        this.initState = initState;
    }

    @Override
    public void queue(E state) {

    }
}
