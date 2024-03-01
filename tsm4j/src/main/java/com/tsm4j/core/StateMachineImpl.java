package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE, staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
public class StateMachine<I, O> {

    @EqualsAndHashCode.Include
    private final StateMachineId id;

    private final Set<StateImpl<?>> states;
    private final Map<Class<?>, ExceptionHandler<? extends RuntimeException>> exceptionHandlerMap;

    public StateMachineResult<O> run(NextStateImpl<I> initState) {
        log.debug("[StateMachine={}] Starting", id);

        // setup
        ContextImpl context = new ContextImpl(id);
        StateMachineResult.StateMachineResultBuilder<O> resultBuilder = StateMachineResult.builder();

        // run state machine
        PriorityQueue<RunningState<?>> remainingStates = new PriorityQueue<>(); // descending queue
        remainingStates.add(RunningState.initial(initState));

        log.trace("[StateMachine={}] Running graph", id);
        while (!remainingStates.isEmpty()) {

            final RunningState<?> runningState = remainingStates.poll();
            final StateImpl<?> currState = runningState.next.getState();
            final List<Supplier<NextStateImpl<?>>> nextStateSuppliers = runningState.next.getOrderedNextStateSuppliers(context);

            log.trace("[StateMachine={}] Current state: {}", id, currState);
            if (!states.contains(currState)) {  // sanity check
                throw new IllegalStateException(String.format("Next state not defined in this state machine, state=%s", currState));
            }

            if (currState.getId().getType().isLeaf()) {
                log.trace("[StateMachine={}] Leaf: {}", id, currState);
                resultBuilder.leafPath(runningState.complete());
                continue;

            } else if (currState.getId().getType().isOutput()) {
                log.trace("[StateMachine={}] Output: {}", id, currState);
                resultBuilder.outputPath(runningState.complete());

            } else if (nextStateSuppliers.isEmpty()) {
                log.trace("[StateMachine={}] Implicit Leaf: {}", id, currState);
                resultBuilder.implicitLeafPath(runningState.complete());
                continue;
            }

            for (Supplier<NextStateImpl<?>> supplier : nextStateSuppliers) {
                try {
                    NextStateImpl<?> nextState = supplier.get();
                    remainingStates.add(runningState.next(nextState));
                } catch (RuntimeException e) {  // transition breaking from normal flow
                    handleException(e, runningState, remainingStates, context);
                }
            }
        }

        StateMachineResult<O> result = resultBuilder.build();

        log.debug("[StateMachine={}] Finished", id);
        return result;
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <E extends RuntimeException> void handleException(E e, RunningState<?> runningState, PriorityQueue<RunningState<?>> remainingStates, ContextImpl context) {
        Optional<ExceptionHandler<E>> handlerToUse = getClosestExceptionHandler(e.getClass());
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                NextStateImpl<?> nextState = handlerToUse.get().apply(e, context);
                remainingStates.add(runningState.next(nextState));
            } catch (RuntimeException nestedException) {
                if (nestedException == e) { // avoid infinite recursion
                    throw e;
                }
                handleException(nestedException, runningState, remainingStates, context);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends RuntimeException> Optional<ExceptionHandler<E>> getClosestExceptionHandler(Class<?> clazz) {
        if (clazz == null) {
            return Optional.empty();
        } else if (exceptionHandlerMap.containsKey(clazz)) {
            return Optional.ofNullable((ExceptionHandler<E>) exceptionHandlerMap.get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass());
        }
    }


    @RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
    private static class RunningState<T> implements Comparable<RunningState<?>> {
        private final NextStateImpl<T> next;
        private final LinkedList<NextStateImpl<?>> history;

        static <R> RunningState<R> initial(NextStateImpl<R> nextState) {
            return new RunningState<>(nextState, new LinkedList<>());
        }

        <R> RunningState<R> next(NextStateImpl<R> followingState) {
            return of(followingState, complete());
        }

        LinkedList<NextStateImpl<?>> complete() {
            final LinkedList<NextStateImpl<?>> allHistory = new LinkedList<>(this.history);
            allHistory.add(next);
            return allHistory;
        }

        @Override
        public int compareTo(RunningState<?> o) {
            return this.next.getState().compareTo(o.next.getState());
        }
    }

}
