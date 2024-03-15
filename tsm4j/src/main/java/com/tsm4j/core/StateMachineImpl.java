package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
class StateMachineImpl<I, O> implements StateMachine<I, O> {

    private final Set<State<?>> states;
    private final Set<State<I>> inputStates;
    private final Set<State<O>> outputStates;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;
    @Getter
    private final String name;

    StateMachineImpl(
            String name,
            Set<State<?>> states,
            Set<State<I>> inputStates,
            Set<State<O>> outputStates,
            Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap
    ) {
        this.name = name;
        this.states = states;
        this.inputStates = inputStates;
        this.outputStates = outputStates;
        this.exceptionHandlerMap = exceptionHandlerMap;
    }

    public Execution<I, O> send(NextState<I> initState) {

        final StateMachinePath<I, I, O> initPath = new StateMachinePath<>(new ArrayList<>(), (NextStateImpl<I>) initState);
        final ExecutionContextImpl<I, O> context = new ExecutionContextImpl<>(name, states, exceptionHandlerMap, initPath);

        this.execute(context);

        return context.getExecution();
    }

    private void execute(ExecutionContextImpl<I, O> context) {
        PathQueue<I, O> pathQueue = context.getPathQueue();
        TransitionQueue<I, O> transitionQueue = context.getTransitionQueue();
        // run
        while (!transitionQueue.isEmpty() || !pathQueue.isEmpty()) {
            if (!transitionQueue.isEmpty()) {
                StateMachineTransition<?> transition = transitionQueue.pop();
                NextStateImpl<?> nextState;
                try {
                    nextState = (NextStateImpl<?>) transition.apply(context);
                } catch (RuntimeException e) {
                    nextState = this.handleException(e, context);
                }

                StateMachinePath<?, I, O> nextPath = new StateMachinePath<>(transition.getPath(), nextState);
                context.notifyNewPath(nextPath);
                pathQueue.add(nextPath);
            } else {
                StateMachinePath<?, I, O> path = pathQueue.pop();
                transitionQueue.consume(path);
            }
        }
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <E extends RuntimeException> NextStateImpl<?> handleException(E e, ExecutionContextImpl<I, O> executionContext) {
        Optional<ExceptionHandlerWithContext<E>> handlerToUse = getClosestExceptionHandler(e.getClass(), executionContext);
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                return (NextStateImpl<?>) handlerToUse.get().apply(e, executionContext);
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
