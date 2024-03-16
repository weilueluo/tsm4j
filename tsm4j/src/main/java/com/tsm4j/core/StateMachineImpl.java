package com.tsm4j.core;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ToString
@Slf4j
class StateMachineImpl<O> implements StateMachine<O> {

    private final Set<State<?>> states;
    private final Set<State<O>> outputStates;
    private final Map<Class<?>, ContextExceptionHandler<? extends RuntimeException>> exceptionHandlerMap;

    StateMachineImpl(
            Set<State<?>> states,
            Set<State<O>> outputStates,
            Map<Class<?>, ContextExceptionHandler<? extends RuntimeException>> exceptionHandlerMap
    ) {
        this.states = states;
        this.outputStates = outputStates;
        this.exceptionHandlerMap = exceptionHandlerMap;
    }

    public <I> Execution<I, O> send(NextState<I> initState) {
        StateMachinePath<I> initPath = new StateMachinePath<>(new ArrayList<>(), (NextStateImpl<I>) initState);
        ContextImpl<I, O> context = new ContextImpl<>(states, exceptionHandlerMap, initPath);
        this.execute(context);
        return context.getExecution();
    }

    private <T> void execute(ContextImpl<T, O> context) {
        PathQueue pathQueue = context.getPathQueue();
        TransitionQueue transitionQueue = context.getTransitionQueue();
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
                StateMachinePath<?> nextPath = new StateMachinePath<>(transition.getPath(), nextState);
                context.notify(nextPath);
                pathQueue.add(nextPath);
            } else {
                StateMachinePath<?> path = pathQueue.pop();
                transitionQueue.consume(path);
            }
        }
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <I, E extends RuntimeException> NextStateImpl<?> handleException(E e, ContextImpl<I, O> executionContext) {
        Optional<ContextExceptionHandler<E>> handlerToUse = getClosestExceptionHandler(e.getClass(), executionContext);
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
    private <I, E extends RuntimeException> Optional<ContextExceptionHandler<E>> getClosestExceptionHandler(Class<?> clazz, ContextImpl<I, O> context) {
        if (clazz == null) {
            return Optional.empty();
        } else if (context.getExceptionHandlerMap().containsKey(clazz)) {
            return Optional.ofNullable((ContextExceptionHandler<E>) context.getExceptionHandlerMap().get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass(), context);
        }
    }
}
