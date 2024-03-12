package com.tsm4j.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/*
 * Represents a path in the state machine
 * Also responsible for recording state level activities in the executionContext
 * */
@Getter
class StateMachinePath<T, I, O> {

    private final ArrayList<State<?>> path;
    private final StateImpl<T> state;
    private final T data;
    private boolean executed;

    StateMachinePath(@NonNull ArrayList<State<?>> prevStates, @NonNull StateImpl<T> newState, T data, ExecutionContextImpl<I, O> executionContext) {
        this.state = newState;
        this.data = data;
        this.path = new ArrayList<>(prevStates);
        this.path.add(newState);
        executionContext.getCurrentExecution().recordReached(newState);
    }

    StateMachinePath(@NonNull ArrayList<State<?>> oldPath, @NonNull NextStateImpl<T> nextState, ExecutionContextImpl<I, O> executionContext) {
        this(oldPath, nextState.getState(), nextState.getData(), executionContext);
    }

    boolean isOutput() {
        return this.state.isOutput();
    }

    boolean isLeaf() {
        return this.state.isLeaf();
    }

    ExecutionResult<I, O> tryExecute(ExecutionContextImpl<I, O> executionContext) {
        boolean runHooks = false;
        if (!this.executed) {  // if passed the execute check before, then allow it for subsequent runs
            if (this.canExecute(executionContext)) {
                runHooks = true;  // run hooks only if first time execute
                this.executed = true;
            } else {
                // cant execute yet, return 'this' as the next path to run
                return new ExecutionResult<>(Collections.singletonList(this), false);
            }
        }

        // pre hooks
        if (runHooks) {
            this.preHook(executionContext);
        }

        // execute
        LinkedList<StateMachinePath<?, I, O>> nextRegions = new LinkedList<>();
        for (TransitionWithContext<T> transition : this.state.getTransitions()) {
            try {
                NextStateImpl<?> nextState = (NextStateImpl<?>) transition.apply(data, executionContext);
                nextRegions.add(new StateMachinePath<>(path, nextState, executionContext));
            } catch (RuntimeException e) {
                nextRegions.add(handleException(e, executionContext));
            }
        }

        // post hook
        if (runHooks) {
            this.postHook(executionContext);
        }
        executionContext.getCurrentExecution().recordExecuted(this.state);
        return new ExecutionResult<>(nextRegions, true);
    }

    private boolean canExecute(ExecutionContextImpl<I, O> executionContext) {
        return this.state.getRequirements().isSatisfied(executionContext);
    }

    private void preHook(ExecutionContextImpl<I, O> executionContext) {
        // TODO
    }

    private void postHook(ExecutionContextImpl<I, O> executionContext) {
        // TODO
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <E extends RuntimeException> StateMachinePath<?, I, O> handleException(E e, ExecutionContextImpl<I, O> executionContext) {
        Optional<ExceptionHandlerWithContext<E>> handlerToUse = getClosestExceptionHandler(e.getClass(), executionContext);
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                NextStateImpl<?> nextState = (NextStateImpl<?>) handlerToUse.get().apply(e, executionContext);  // this cast is safe... as long as this is the only implementing class
                return new StateMachinePath<>(path, nextState, executionContext);
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

    @Getter
    @RequiredArgsConstructor
    static class ExecutionResult<I, O> {
        private final List<StateMachinePath<?, I, O>> nextPaths;
        private final boolean executed;
    }
}
