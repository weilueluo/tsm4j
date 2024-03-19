package com.tsm4j.core;

import java.util.function.Consumer;

@FunctionalInterface
public interface Transition<E extends Enum<E>> extends Consumer<Context<E>> {
}
