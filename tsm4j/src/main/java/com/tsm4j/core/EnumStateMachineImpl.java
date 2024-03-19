package com.tsm4j.core;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ToString
@Slf4j
class EnumStateMachineImpl<E extends Enum<E>> implements StateMachine<E> {

    private final ContextImpl<E> context;
    private final Map<Class<? extends RuntimeException>, ContextExceptionHandler<? extends RuntimeException, E>> exceptionHandlerMap;

    EnumStateMachineImpl(
            Map<NamedTransition<E>, Set<E>> transitionMap,
            Map<Class<? extends RuntimeException>, ContextExceptionHandler<? extends RuntimeException, E>> exceptionHandlerMap
    ) {
        this.exceptionHandlerMap = exceptionHandlerMap;
        this.context = new ContextImpl<>(transitionMap);
    }

    public Context<E> send(E state) {
        return this.send(Collections.singleton(state));
    }

    public Context<E> send(Set<E> states) {
        states.forEach(context::send);
        this.execute();
        return context;
    }

    private void execute() {
        while (!context.isEmpty()) {
            NamedTransition<E> transition = context.pop();
            try {
                transition.accept(context);
            } catch (RuntimeException e) {
                this.handleException(e, context);
            }
        }
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <RE extends RuntimeException> void handleException(RE e, ContextImpl<E> context) {
        Optional<ContextExceptionHandler<RE, E>> handlerToUse = getClosestExceptionHandler(e.getClass(), context);
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                handlerToUse.get().accept(e, context);
            } catch (RuntimeException nestedException) {
                if (nestedException == e) { // avoid infinite recursion
                    throw e;
                }
                handleException(nestedException, context);
            }
        }
    }

    private <RE extends RuntimeException> Optional<ContextExceptionHandler<RE, E>> getClosestExceptionHandler(Class<?> clazz, ContextImpl<E> context) {
        if (clazz == null) {
            return Optional.empty();
        } else if (exceptionHandlerMap.containsKey(clazz)) {
            return Optional.ofNullable((ContextExceptionHandler<RE, E>) exceptionHandlerMap.get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass(), context);
        }
    }
}
