package com.tsm4j.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
class NamedTransition {
    @EqualsAndHashCode.Include
    private final String name;
    private final Consumer<Context> transition;

    void accept(ContextImpl context) {
        this.transition.accept(context);
    }
}
