package com.tsm4j.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateMachineTest {

    @Test
    public void testBasic() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(setOf(MyState.HUNGRY, MyState.NO_FOOD), context -> context.send(MyState.MAKE_FOOD))
                .addTransition(MyState.MAKE_FOOD, context -> {
                    Supplier<Boolean> tryMakeFood = () -> true;
                    if (tryMakeFood.get()) {
                        context.send(MyState.FOOD_IS_READY);
                    } else {
                        context.send(MyState.FOOD_IS_NOT_READY);
                    }
                })
                .addTransition(setOf(MyState.HUNGRY, MyState.FOOD_IS_READY), context -> context.send(MyState.NOT_HUNGRY))
                .build();

        assertTrue(stateMachine.send(setOf(MyState.HUNGRY, MyState.NO_FOOD)).hasReached(MyState.NOT_HUNGRY));
    }

    @Test
    public void testPutGet() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(setOf(MyState.HUNGRY, MyState.NO_FOOD), context -> context.send(MyState.MAKE_FOOD))
                .addTransition(MyState.MAKE_FOOD, context -> {
                    int attempts = context.getOrDefault(MyState.ATTEMPTS, () -> 0);
                    if (attempts > 3) {
                        context.send(MyState.FOOD_IS_READY);
                    } else {
                        context.send(MyState.ATTEMPTS, attempts + 1);
                        context.send(MyState.MAKE_FOOD);
                    }
                })
                .addTransition(setOf(MyState.HUNGRY, MyState.FOOD_IS_READY, MyState.ATTEMPTS), context -> {
                    System.out.println("attempts: " + context.getOrError(MyState.ATTEMPTS));
                    context.send(MyState.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(setOf(MyState.HUNGRY, MyState.NO_FOOD)).hasReached(MyState.NOT_HUNGRY));
    }

    @Test
    public void testHandleException() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(MyState.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (context, e) -> {
                    // custom eat food logic
                    context.send(MyState.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
    }

    @Test
    public void testHandleNestedException() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(MyState.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (context, e) -> {
                    throw new IllegalStateException("nested exception!");
                })
                .addExceptionHandler(IllegalStateException.class, (context, e) -> {
                    context.send(MyState.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
    }

    @Test
    public void testHandleSubclassException() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(MyState.HUNGRY, context -> {
                    throw new IllegalStateException("nested exception!");
                })
                .addExceptionHandler(RuntimeException.class, (context, e) -> {
                    context.send(MyState.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
    }

    @Test
    public void testHandleThrowingSameException() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(MyState.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (context, e) -> {
                    throw e;
                })
                .build();

        assertThatThrownBy(() -> stateMachine.send(MyState.HUNGRY))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("exception!");
    }

    @Test
    public void testThrowingNotHandledException() {
        StateMachine stateMachine = StateMachineBuilder.newInstance()
                .addTransition(MyState.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .build();

        assertThatThrownBy(() -> stateMachine.send(MyState.HUNGRY))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("exception!");
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static class MyState {
        public static final State<Void> HUNGRY = State.create();
        public static final State<Void> NO_FOOD = State.create();
        public static final State<Void> FOOD_IS_NOT_READY = State.create();
        public static final State<Void> MAKE_FOOD = State.create();
        public static final State<Void> FOOD_IS_READY = State.create();
        public static final State<Void> NOT_HUNGRY = State.create();

        public static final State<Integer> ATTEMPTS = State.create();
    }
}