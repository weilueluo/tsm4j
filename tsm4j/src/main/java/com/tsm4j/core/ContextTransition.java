package com.tsm4j.core;

@FunctionalInterface
public interface ContextTransition<T> extends Transition<T> {

    @Override
    default NextState<?> apply(T t, Context context) {
        return this.apply(context);
    }

    NextState<?> apply(Context context);
}
