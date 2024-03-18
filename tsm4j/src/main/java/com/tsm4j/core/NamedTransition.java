package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NamedTransition<E extends Enum<E>> {
    @EqualsAndHashCode.Include
    private final String name;
    private final Transition<E> transition;
}
