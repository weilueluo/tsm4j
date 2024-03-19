package com.tsm4j.core;

import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateMachineTest {

    @Test
    public void testBasic() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(setOf(MyEnum.HUNGRY, MyEnum.NO_FOOD), context -> context.send(MyEnum.MAKE_FOOD))
                .addTransition(MyEnum.MAKE_FOOD, context -> {
                    Supplier<Boolean> tryMakeFood = () -> true;
                    if (tryMakeFood.get()) {
                        context.send(MyEnum.FOOD_IS_READY);
                    } else {
                        context.send(MyEnum.FOOD_IS_NOT_READY);
                    }
                })
                .addTransition(setOf(MyEnum.HUNGRY, MyEnum.FOOD_IS_READY), context -> context.send(MyEnum.NOT_HUNGRY))
                .build();

        assertTrue(stateMachine.send(setOf(MyEnum.HUNGRY, MyEnum.NO_FOOD)).hasReached(MyEnum.NOT_HUNGRY));
    }

    @Test
    public void testPutGet() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(setOf(MyEnum.HUNGRY, MyEnum.NO_FOOD), context -> context.send(MyEnum.MAKE_FOOD))
                .addTransition(MyEnum.MAKE_FOOD, context -> {
                    int attempts = context.getOrDefault(Integer.class, () -> 0);
                    if (attempts > 3) {
                        context.send(MyEnum.FOOD_IS_READY);
                    } else {
                        context.put(attempts + 1);
                        context.send(MyEnum.MAKE_FOOD);
                    }
                })
                .addTransition(setOf(MyEnum.HUNGRY, MyEnum.FOOD_IS_READY), context -> {
                    // eat food logic...
                    context.send(MyEnum.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(setOf(MyEnum.HUNGRY, MyEnum.NO_FOOD)).hasReached(MyEnum.NOT_HUNGRY));
    }

    @Test
    public void testHandleException() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(MyEnum.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (e, context) -> {
                    // custom eat food logic
                    context.send(MyEnum.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyEnum.HUNGRY).hasReached(MyEnum.NOT_HUNGRY));
    }

    @Test
    public void testHandleNestedException() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(MyEnum.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (e, context) -> {
                    throw new IllegalStateException("nested exception!");
                })
                .addExceptionHandler(IllegalStateException.class, (e, context) -> {
                    context.send(MyEnum.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyEnum.HUNGRY).hasReached(MyEnum.NOT_HUNGRY));
    }

    @Test
    public void testHandleSubclassException() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(MyEnum.HUNGRY, context -> {
                    throw new IllegalStateException("nested exception!");
                })
                .addExceptionHandler(RuntimeException.class, (e, context) -> {
                    context.send(MyEnum.NOT_HUNGRY);
                })
                .build();

        assertTrue(stateMachine.send(MyEnum.HUNGRY).hasReached(MyEnum.NOT_HUNGRY));
    }

    @Test
    public void testHandleThrowingSameException() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(MyEnum.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .addExceptionHandler(RuntimeException.class, (e, context) -> {
                    throw e;
                })
                .build();

        assertThatThrownBy(() -> stateMachine.send(MyEnum.HUNGRY))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("exception!");
    }

    @Test
    public void testThrowingNotHandledException() {
        StateMachine<MyEnum> stateMachine = EnumStateMachineBuilder.newInstance(MyEnum.class)
                .addTransition(MyEnum.HUNGRY, context -> {
                    throw new RuntimeException("exception!");
                })
                .build();

        assertThatThrownBy(() -> stateMachine.send(MyEnum.HUNGRY))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("exception!");
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    enum MyEnum {
        HUNGRY, NO_FOOD, FOOD_IS_NOT_READY, MAKE_FOOD, FOOD_IS_READY, NOT_HUNGRY
    }
}