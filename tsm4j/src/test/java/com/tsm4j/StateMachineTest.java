package com.tsm4j;

import com.tsm4j.core.NextState;
import com.tsm4j.core.State;
import com.tsm4j.core.StateMachine;
import com.tsm4j.core.StateMachineBuilder;
import com.tsm4j.core.StateMachineResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateMachineTest {

    @Test
    public void demo() {
        // create a state machine builder with Integer input and String output named demo
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("demo");

        // define states
        // s1 has Integer input with name "int 1"
        State<Integer> s1 = builder.newTransitionState("int 1");
        // s2 has Integer input with name "int 2"
        State<Integer> s2 = builder.newTransitionState("int 2");
        // s3 has String input with name "str"
        // it is an output state, so any value arrived at this state is considered as an output
        State<String> s3 = builder.newOutputState("str");

        // define transitions
        // state1 ----> state2 ----> state3 (to string)
        builder.addTransition(s1, i -> s2.of(i * 2));
        builder.addTransition(s2, i -> {
            if (i > 5) {
                return s3.of(String.valueOf(i + 1));
            } else {
                return s3.of(String.valueOf(i * 3));
            }
        });

        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger state machine from state1
        assertEquals("6", stateMachine.run(s1.of(1)).getOutputs().get(0));  // 1 * 2 * 3 = 6

        // trigger state machine from state2
        assertEquals("7", stateMachine.run(s2.of(6)).getOutputs().get(0));  // 6 + 1 = 7
    }

    @Test
    public void test() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<Integer> state2 = builder.newTransitionState("intState2");
        State<String> state3 = builder.newOutputState("stringState");

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i * 2));
        builder.addTransition(state2, (i, c) -> state3.of(String.valueOf(i + 1)));
        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        StateMachineResult<String> results1 = stateMachine.run(state1.of(2));
        assertEquals(1, results1.getOutputs().size());
        assertEquals("5", results1.getOutputs().get(0));

        // trigger from state2
        StateMachineResult<String> results2 = stateMachine.run(state2.of(2));
        assertEquals(1, results2.getOutputs().size());
        assertEquals("3", results2.getOutputs().get(0));
    }

    @Test
    public void testTransitionOrder() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");

        // define transitions
        final List<Integer> outputs = new ArrayList<>();
        builder.addTransition(state1, (i, c) -> {
            outputs.add(5);
            return NextState.leaf();
        }, 5);
        builder.addTransition(state1, (i, c) -> {
            outputs.add(4);
            return NextState.leaf();
        }, 4);
        builder.addTransition(state1, (i, c) -> {
            outputs.add(1);
            return NextState.leaf();
        }, 1);
        builder.addTransition(state1, (i, c) -> {
            outputs.add(3);
            return NextState.leaf();
        }, 3);
        builder.addTransition(state1, (i, c) -> {
            outputs.add(2);
            return NextState.leaf();
        }, 2);
        StateMachine<Integer, String> stateMachine = builder.build();

        stateMachine.run(state1.of(0));
        assertThat(outputs).contains(5, 4, 3, 2, 1);
    }

    @Test
    public void testStateOrder() {
        StateMachineBuilder<Integer, Integer> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state0 = builder.newTransitionState("intState0");
        State<Integer> state1 = builder.newOutputState("intState1", 1);
        State<Integer> state2 = builder.newOutputState("intState2", 2);
        State<Integer> state3 = builder.newOutputState("intState3", 3);
        State<Integer> state4 = builder.newOutputState("intState4", 4);
        State<Integer> state5 = builder.newOutputState("intState5", 5);

        // define transitions
        builder.addTransition(state0, (i, c) -> state3.of(3));
        builder.addTransition(state0, (i, c) -> state4.of(4));
        builder.addTransition(state0, (i, c) -> state1.of(1));
        builder.addTransition(state0, (i, c) -> state2.of(2));
        builder.addTransition(state0, (i, c) -> state5.of(5));
        StateMachine<Integer, Integer> stateMachine = builder.build();

        List<Integer> outputs = stateMachine.run(state0.of(0)).getOutputs();
        assertThat(outputs).contains(5, 4, 3, 2, 1);
    }

    @Test
    public void testMultipleOutputs() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<Integer> state2 = builder.newTransitionState("intState2");
        State<String> state3 = builder.newOutputState("stringState1");
        State<String> state4 = builder.newOutputState("stringState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i * 2));
        builder.addTransition(state2, (i, c) -> state3.of(String.valueOf(i + 1)));
        builder.addTransition(state2, (i, c) -> state4.of(String.valueOf(i + 2)));
        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        StateMachineResult<String> results = stateMachine.run(state1.of(2));
        assertEquals(2, results.getOutputs().size());
        assertThat(results.getOutputs()).containsExactlyInAnyOrder("5", "6");
    }

    @Test
    public void testRecursiveTransition() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<Integer> state2 = builder.newTransitionState("intState2");
        State<String> state3 = builder.newOutputState("stringState1");

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i + 1));
        builder.addTransition(state2, (i, c) -> {
            if (i < 10) {
                return state1.of(i);
            } else {
                return state3.of(String.valueOf(i + 1));
            }
        });
        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        StateMachineResult<String> results = stateMachine.run(state1.of(0));
        assertEquals(1, results.getOutputs().size());
        assertEquals("11", results.getOutputs().get(0));

    }

    @Test
    public void testExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<String> state2 = builder.newOutputState("intState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> state2.of("successfully handled"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        StateMachineResult<String> results = stateMachine.run(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<String> state2 = builder.newOutputState("intState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> {
            throw new IllegalStateException("intentional error from handling runtime exception");
        });
        builder.addExceptionHandler(IllegalStateException.class, (e, c) -> state2.of("successfully handled"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        StateMachineResult<String> results = stateMachine.run(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedSameExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> {
            throw e; // throw the same exception should tell the state machine to not handle it, otherwise will get infinite recursion
        });

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        assertThrows(RuntimeException.class, () -> stateMachine.run(state1.of(123)), "state1 transition to error, intentional exception");
    }

    @Test
    public void testExceptionHandler_throwingSubclass() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler_throwingSubclass");

        // define states
        State<Integer> state1 = builder.newTransitionState("intState1");
        State<String> state2 = builder.newOutputState("intState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new IllegalStateException("state1 transition to error, intentional exception");  // IllegalStateException is subclass of RuntimeException
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> state2.of("successfully handled"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        StateMachineResult<String> results = stateMachine.run(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }
}