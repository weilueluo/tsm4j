package com.tsm4j.core;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

@ToString
@Slf4j
class StateMachineImpl implements StateMachine {

    private final ContextImpl context;
    private final Map<Class<? extends RuntimeException>, BiConsumer<Context, ? extends RuntimeException>> exceptionHandlerMap;

    StateMachineImpl(
            Map<NamedTransition, Set<State<?>>> transitionMap,
            Map<Class<? extends RuntimeException>, BiConsumer<Context, ? extends RuntimeException>> exceptionHandlerMap
    ) {
        this.exceptionHandlerMap = exceptionHandlerMap;
        this.context = new ContextImpl(transitionMap);
    }

    @Override
    public Context send(State<Void> state) {
        return this.send(Collections.singleton(state));
    }

    @Override
    public Context send(Set<State<Void>> states) {
        states.forEach(context::send);
        this.execute();
        return context;
    }

    @Override
    public <T> Context send(State<T> state, T data) {
        context.send(state, data);
        this.execute();
        return context;
    }

    private void execute() {
        while (!context.isEmpty()) {
            NamedTransition transition = context.pop();
            try {
                transition.accept(context);
            } catch (RuntimeException e) {
                this.handleException(context, e);
            }
        }
    }

    // recursively handle exception from transition and exception from handlers themselves
    private <RE extends RuntimeException> void handleException(ContextImpl context, RE e) {
        Optional<BiConsumer<Context, RE>> handlerToUse = getClosestExceptionHandler(e.getClass(), context);
        if (!handlerToUse.isPresent()) {
            throw e;
        } else {
            try {
                handlerToUse.get().accept(context, e);
            } catch (RuntimeException nestedException) {
                if (nestedException == e) { // avoid infinite recursion
                    throw e;
                }
                handleException(context, nestedException);
            }
        }
    }

    private <RE extends RuntimeException> Optional<BiConsumer<Context, RE>> getClosestExceptionHandler(Class<?> clazz, ContextImpl context) {
        if (clazz == null) {
            return Optional.empty();
        } else if (exceptionHandlerMap.containsKey(clazz)) {
            return Optional.ofNullable((BiConsumer<Context, RE>) exceptionHandlerMap.get(clazz));
        } else {
            return getClosestExceptionHandler(clazz.getSuperclass(), context);
        }
    }
}
