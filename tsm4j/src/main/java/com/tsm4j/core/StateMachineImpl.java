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

@RequiredArgsConstructor(access = AccessLevel.PACKAGE, staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
public class StateMachineImpl<I, O> implements StateMachine<I, O> {

    @EqualsAndHashCode.Include
    private final StateMachine.Id id;
    private final Map<State<?>, List<TransitionWithContext<?>>> stateToTransitionsMap;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;

    @Override
    public StateMachine.Id getId() {
        return id;
    }

    @SuppressWarnings("rawtypes,unchecked")  // applying transition output is guaranteed to be type safe, enforced by state machine builder
    public StateMachineResult<O> run(NextState<I> initState) {
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
            final State<?> currState = runningState.next.getState();

            log.trace("[StateMachine={}] Current state: {}", id, currState);
            if (!stateToTransitionsMap.containsKey(currState)) {  // sanity check
                throw new IllegalStateException(String.format("Next state not defined in this state machine, state=%s", currState));
            }

            final List<TransitionWithContext<?>> currTransitions = stateToTransitionsMap.get(currState);

            if (currState.getId().getType().isLeaf()) {
                log.trace("[StateMachine={}] Leaf: {}", id, currState);
                resultBuilder.leafPath(runningState.complete());
                continue;

            } else if (currState.getId().getType().isOutput()) {
                log.trace("[StateMachine={}] Output: {}", id, currState);
                resultBuilder.outputPath(runningState.complete());

            } else if (currTransitions.isEmpty()) {
                log.trace("[StateMachine={}] Implicit Leaf: {}", id, currState);
                resultBuilder.implicitLeafPath(runningState.complete());
                continue;
            }

            for (TransitionWithContext transition : currTransitions) {
                try {
                    NextState<?> nextState = (NextState<?>) transition.apply(runningState.next.getData(), context);
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
        Optional<ExceptionHandlerWithContext<E>> handlerToUse = getClosestExceptionHandler(e.getClass());
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                NextState<?> nextState = handlerToUse.get().apply(e, context);
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
    private <E extends RuntimeException> Optional<ExceptionHandlerWithContext<E>> getClosestExceptionHandler(Class<?> clazz) {
        if (clazz == null) {
            return Optional.empty();
        } else if (exceptionHandlerMap.containsKey(clazz)) {
            return Optional.ofNullable((ExceptionHandlerWithContext<E>) exceptionHandlerMap.get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass());
        }
    }


    @RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
    private static class RunningState<T> implements Comparable<RunningState<?>> {
        private final NextState<T> next;
        private final LinkedList<NextState<?>> history;

        static <R> RunningState<R> initial(NextState<R> nextState) {
            return new RunningState<>(nextState, new LinkedList<>());
        }

        <R> RunningState<R> next(NextState<R> followingState) {
            return of(followingState, complete());
        }

        LinkedList<NextState<?>> complete() {
            final LinkedList<NextState<?>> allHistory = new LinkedList<>(this.history);
            allHistory.add(next);
            return allHistory;
        }

        @Override
        public int compareTo(RunningState<?> o) {
            return this.next.getState().compareTo(o.next.getState());
        }
    }


    @RequiredArgsConstructor
    static class Id implements StateMachine.Id {
        private final String name;
        @Override
        public String getName() {
            return name;
        }
    }
}
