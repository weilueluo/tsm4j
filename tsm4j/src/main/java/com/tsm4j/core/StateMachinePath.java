package com.tsm4j.core;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/*
 * Represents a path in the state machine
 * Also responsible for recording state level activities in the executionContext
 * */
@Getter
class StateMachinePath<T, I, O> {

    private final T data;
    private final ArrayList<State<?>> path;
    private final StateImpl<T> state;

    StateMachinePath(@NonNull ArrayList<State<?>> prevStates, @NonNull StateImpl<T> newState, T data) {
        this.state = newState;
        this.data = data;
        this.path = new ArrayList<>(prevStates);
        this.path.add(newState);
    }

    StateMachinePath(@NonNull ArrayList<State<?>> oldPath, @NonNull NextStateImpl<T> nextState) {
        this(oldPath, nextState.getState(), nextState.getData());
    }

    boolean isOutput() {
        return this.state.isOutput();
    }

    boolean isLeaf() {
        return this.state.isLeaf();
    }

    List<StateMachinePath<?, I, O>> next(ExecutionContextImpl<I, O> context) {

        LinkedList<StateMachinePath<?, I, O>> nextPaths = new LinkedList<>();
        for (TransitionWithContext<T> transition : this.state.getTransitions()) {
            try {
                NextStateImpl<?> nextState = (NextStateImpl<?>) transition.apply(data, context);
                nextPaths.add(new StateMachinePath<>(path, nextState));
            } catch (RuntimeException e) {
                nextPaths.add(handleException(e, context));
            }
        }

        return nextPaths;
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <E extends RuntimeException> StateMachinePath<?, I, O> handleException(E e, ExecutionContextImpl<I, O> executionContext) {
        Optional<ExceptionHandlerWithContext<E>> handlerToUse = getClosestExceptionHandler(e.getClass(), executionContext);
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                NextStateImpl<?> nextState = (NextStateImpl<?>) handlerToUse.get().apply(e, executionContext);  // this cast is safe... as long as this is the only implementing class
                return new StateMachinePath<>(path, nextState);
            } catch (RuntimeException nestedException) {
                if (nestedException == e) { // avoid infinite recursion
                    throw e;
                }
                return handleException(nestedException, executionContext);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends RuntimeException> Optional<ExceptionHandlerWithContext<E>> getClosestExceptionHandler(Class<?> clazz, ExecutionContextImpl<I, O> context) {
        if (clazz == null) {
            return Optional.empty();
        } else if (context.getExceptionHandlerMap().containsKey(clazz)) {
            return Optional.ofNullable((ExceptionHandlerWithContext<E>) context.getExceptionHandlerMap().get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass(), context);
        }
    }
}
