package com.tsm4j.core;


import java.util.function.Consumer;

@FunctionalInterface
public interface ExceptionHandler<RE extends RuntimeException> extends Consumer<RE> {
}
