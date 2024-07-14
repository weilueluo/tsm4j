package com.tsm4j.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StateMachineTest {

    private static StateListener<TestState> debugLoggingListener() {
        return context -> System.out.printf("[STATE]: %s%n", context.getLatestState());
    }

    @Test
    public void serialTransition() {
        StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
                .addTransition(TestState.HUNGRY, TestState.MAKE_FOOD)
                .addTransition(TestState.MAKE_FOOD, TestState.FOOD_IS_READY)
                .addTransition(TestState.FOOD_IS_READY, TestState.EAT_FOOD)
                .addTransition(TestState.EAT_FOOD, TestState.FULL)
                .addListener(debugLoggingListener())
                .build();

        assertThat(stateMachine.send(TestState.HUNGRY).reached(TestState.FULL)).isTrue();
    }

    @Test
    public void compoundTransition1() {
        StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
                .addTransition(setOf(TestState.HUNGRY, TestState.NO_FOOD), TestState.MAKE_FOOD)
                .addTransition(TestState.MAKE_FOOD, TestState.FOOD_IS_READY)
                .addTransition(setOf(TestState.HUNGRY, TestState.FOOD_IS_READY), TestState.EAT_FOOD)
                .addTransition(TestState.EAT_FOOD, TestState.FULL)
                .addListener(debugLoggingListener())
                .build();

        assertThat(stateMachine.send(listOf(TestState.HUNGRY, TestState.NO_FOOD)).reached(TestState.FULL)).isTrue();
    }

    @Test
    public void compoundTransition2() {
        StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
                .addTransition(setOf(TestState.HUNGRY, TestState.NO_FOOD), TestState.MAKE_FOOD)
                .addTransition(TestState.MAKE_FOOD, TestState.FOOD_IS_READY)
                .addTransition(setOf(TestState.HUNGRY, TestState.FOOD_IS_READY), TestState.EAT_FOOD)
                .addTransition(TestState.EAT_FOOD, TestState.FULL)
                .addListener(debugLoggingListener())
                .build();

        assertThat(stateMachine.send(TestState.HUNGRY).reached(TestState.FULL)).isFalse();
        assertThat(stateMachine.send(TestState.NO_FOOD).reached(TestState.FULL)).isTrue();
    }

    @Test
    public void queueWithContext1() {
        StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
                .addTransition(TestState.NO_FOOD, TestState.MAKE_FOOD)
                .addTransition(TestState.MAKE_FOOD, TestState.FOOD_IS_READY)

                .addTransition(setOf(TestState.HUNGRY, TestState.FOOD_IS_READY), TestState.EAT_FOOD)
                .addTransition(TestState.EAT_FOOD, TestState.FULL)

                .addListener(TestState.FOOD_IS_READY, context -> context.queue(TestState.HUNGRY))

                .addListener(debugLoggingListener())
                .build();

        assertThat(stateMachine.send(TestState.NO_FOOD).reached(TestState.FULL)).isTrue();
    }

    @Test
    public void queueWithContext2() {
        StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
                .addTransition(TestState.NO_FOOD, TestState.MAKE_FOOD)
                .addListener(TestState.MAKE_FOOD, context -> {
                    System.out.println("making food...");
                    context.queue(TestState.FOOD_IS_READY);
                })
                .addListener(debugLoggingListener())
                .build();

        assertThat(stateMachine.send(TestState.NO_FOOD).reached(TestState.FOOD_IS_READY)).isTrue();
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

//    @Test
//    public void testHandleException() {
//        StateMachine stateMachine = StateMachineBuilder.newInstance()
//                .addTransition(MyState.HUNGRY, context -> {
//                    throw new RuntimeException("exception!");
//                })
//                .addExceptionHandler(RuntimeException.class, (context, e) -> {
//                    // custom eat food logic
//                    context.send(MyState.NOT_HUNGRY);
//                })
//                .build();
//
//        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
//    }
//
//    @Test
//    public void testHandleNestedException() {
//        StateMachine stateMachine = StateMachineBuilder.newInstance()
//                .addTransition(MyState.HUNGRY, context -> {
//                    throw new RuntimeException("exception!");
//                })
//                .addExceptionHandler(RuntimeException.class, (context, e) -> {
//                    throw new IllegalStateException("nested exception!");
//                })
//                .addExceptionHandler(IllegalStateException.class, (context, e) -> {
//                    context.send(MyState.NOT_HUNGRY);
//                })
//                .build();
//
//        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
//    }
//
//    @Test
//    public void testHandleSubclassException() {
//        StateMachine stateMachine = StateMachineBuilder.newInstance()
//                .addTransition(MyState.HUNGRY, context -> {
//                    throw new IllegalStateException("nested exception!");
//                })
//                .addExceptionHandler(RuntimeException.class, (context, e) -> {
//                    context.send(MyState.NOT_HUNGRY);
//                })
//                .build();
//
//        assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
//    }
//
//    @Test
//    public void testHandleThrowingSameException() {
//        StateMachine stateMachine = StateMachineBuilder.newInstance()
//                .addTransition(MyState.HUNGRY, context -> {
//                    throw new RuntimeException("exception!");
//                })
//                .addExceptionHandler(RuntimeException.class, (context, e) -> {
//                    throw e;
//                })
//                .build();
//
//        assertThatThrownBy(() -> stateMachine.send(MyState.HUNGRY))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessage("exception!");
//    }
//
//    @Test
//    public void testThrowingNotHandledException() {
//        StateMachine stateMachine = StateMachineBuilder.newInstance()
//                .addTransition(MyState.HUNGRY, context -> {
//                    throw new RuntimeException("exception!");
//                })
//                .build();
//
//        assertThatThrownBy(() -> stateMachine.send(MyState.HUNGRY))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessage("exception!");
//    }

    @SafeVarargs
    private final <T> List<T> listOf(T... values) {
        return Arrays.asList(values);
    }

    private enum TestState {
        HUNGRY,
        NO_FOOD,
        FOOD_IS_NOT_READY,
        MAKE_FOOD,
        FOOD_IS_READY,
        NOT_HUNGRY,
        ATTEMPTS,
        EAT_FOOD,
        FULL,
    }
}