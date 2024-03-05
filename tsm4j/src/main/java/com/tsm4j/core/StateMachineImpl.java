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
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
class StateMachineImpl<I, O> implements StateMachine<I, O> {

    @EqualsAndHashCode.Include
    private final StateMachineId id;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;

    @Override
    public StateMachineId getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    // applying transition output is guaranteed to be type safe, enforced by state machine builder
    public StateMachineResult<O> run(NextState<I> initState) {
        log.trace("[StateMachine={}] Started", id);

        // setup
        ContextImpl context = new ContextImpl(id);
        StateMachineResultImpl.StateMachineResultImplBuilder<O> resultBuilder = StateMachineResultImpl.builder();

        // run
        PriorityQueue<RunningPath<?>> remainingStates = new PriorityQueue<>();
        remainingStates.add(RunningPath.initial(initState));

        log.trace("[StateMachine={}] Running graph", id);
        while (!remainingStates.isEmpty()) {

            final RunningPath<?> runningPath = remainingStates.poll();
            final State<?> currState = runningPath.path.getOutputState().getState();

            log.trace("[StateMachine={}] Current state: {}", id, currState);

            final List<Supplier<NextStateImpl<?>>> currSuppliers = runningPath.path.getOutputState().applyTransitions(context);

            if (currState.getId().getType().isLeaf()) {
                log.trace("[StateMachine={}] Leaf: {}", id, currState);
                resultBuilder.leafPath(runningPath.complete());
                continue;

            } else if (currState.getId().getType().isOutput()) {
                log.trace("[StateMachine={}] Output: {}", id, currState);
                resultBuilder.outputPath((StateMachineResultPath<O>) runningPath.complete());  // this cast is safe, enforced by builder

            } else if (currSuppliers.isEmpty()) {
                log.trace("[StateMachine={}] Implicit Leaf: {}", id, currState);
                resultBuilder.leafPath(runningPath.complete());
                continue;
            }

            for (Supplier<NextStateImpl<?>> supplier : currSuppliers) {
                try {
                    NextStateImpl<?> nextState = supplier.get();
                    remainingStates.add(runningPath.next(nextState));
                } catch (RuntimeException e) {  // transition breaking from normal flow
                    handleException(e, runningPath, remainingStates, context);
                }
            }
        }

        StateMachineResult<O> result = resultBuilder.build();

        log.trace("[StateMachine={}] Finished", id);
        return result;
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <E extends RuntimeException> void handleException(E e, RunningPath<?> runningPath, PriorityQueue<RunningPath<?>> remainingStates, ContextImpl context) {
        Optional<ExceptionHandlerWithContext<E>> handlerToUse = getClosestExceptionHandler(e.getClass());
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                NextStateImpl<?> nextState = (NextStateImpl<?>) handlerToUse.get().apply(e, context);  // this cast is safe... as long as this is the only implementing class
                remainingStates.add(runningPath.next(nextState));
            } catch (RuntimeException nestedException) {
                if (nestedException == e) { // avoid infinite recursion
                    throw e;
                }
                handleException(nestedException, runningPath, remainingStates, context);
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


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class RunningPath<T> implements Comparable<RunningPath<?>> {
        private final StateMachineResultPathImpl<T> path;

        static <R> RunningPath<R> initial(NextState<R> nextState) {
            List<NextState<?>> states = new LinkedList<>();
            states.add(nextState);
            StateMachineResultPathImpl<R> path = new StateMachineResultPathImpl<>(states, (NextStateImpl<R>) nextState);  // this cast is safe, we have only one implementing class
            return new RunningPath<>(path);
        }

        <R> RunningPath<R> next(NextStateImpl<R> followingState) {
            return new RunningPath<>(new StateMachineResultPathImpl<>(path, followingState));
        }

        StateMachineResultPath<T> complete() {
            return path;
        }

        @Override
        public int compareTo(RunningPath<?> o) {
            return this.path.getOutputState().getState().compareTo(o.path.getOutputState().getState());
        }
    }
}
