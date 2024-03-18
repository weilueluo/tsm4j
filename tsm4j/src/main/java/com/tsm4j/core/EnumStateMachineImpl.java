package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ToString
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class EnumStateMachineImpl<E extends Enum<E>> implements StateMachine<E> {

    private final EnumSet<E> states;
    private final Map<NamedTransition<E>, Set<E>> transitionMap;
    private final Map<Class<? extends RuntimeException>, ContextExceptionHandler<? extends RuntimeException, E>> exceptionHandlerMap;


    public Context<E> send(E initState) {
        Path<E> initPath = new Path<>(null, initState);
        ContextImpl<E> context = new ContextImpl<>(transitionMap, initState);
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
                Path<?> nextPath = new Path<>(transition.getPath(), nextState);
                context.notify(nextPath);
                pathQueue.add(nextPath);
            } else {
                Path<?> path = pathQueue.pop();
                transitionQueue.add(path);
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
