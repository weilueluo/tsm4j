package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
class NamedTransition<E extends Enum<E>> {
    @EqualsAndHashCode.Include
    private final String name;
    private final Transition<E> transition;

    void accept(ContextImpl<E> context) {
        this.transition.accept(context);
    }
}
