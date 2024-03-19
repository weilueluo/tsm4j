package com.tsm4j.core;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ContextExceptionHandler<RE extends RuntimeException, E extends Enum<E>> extends BiConsumer<RE, Context<E>> {
}