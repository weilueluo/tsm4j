package com.tsm4j.core;

import com.tsm4j.core.exception.StateNotReachedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateMachineTest {

    @Test
    public void demo() {
        // create a state machine builder with Integer input and String output named demo
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("demo");

        // define states
        // s1 has Integer input with name "s1"
        State<Integer> s1 = builder.addState("s1");
        // s2 has Integer input with name "s2"
        State<Integer> s2 = builder.addState("s2");
        // s3 has String input with name "s3"
        // it is an output state, so any value arrived at this state is considered as an output
        State<String> s3 = builder.addOutputState("s3");

        // define transitions
        // s1 --> s2 --> s3
        builder.addTransition(s1, i -> s2.of(i * 2));
        builder.addTransition(s2, i -> {
            if (i < 5) {
                return s3.of(String.valueOf(i * 3));
            } else if (i < 10) {
                return s3.of(String.valueOf(i + 3));
            } else {
                return NextState.leaf();
            }
        });

        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger state machine from s1
        assertEquals("6", stateMachine.send(s1.of(1)).getOutputs().get(0));  // 1 * 2 * 3 = 6
        // trigger state machine from s2
        assertEquals("9", stateMachine.send(s2.of(6)).getOutputs().get(0));  // 6 + 3 = 9
        // trigger state machine from s2
        assertEquals(0, stateMachine.send(s2.of(11)).getOutputs().size());  // no output
    }

    @Test
    public void test() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");
        State<String> state3 = builder.addOutputState("stringState");

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i * 2));
        builder.addTransition(state2, (i, c) -> state3.of(String.valueOf(i + 1)));
        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> results1 = stateMachine.send(state1.of(2));
        assertEquals(1, results1.getOutputs().size());
        assertEquals("5", results1.getOutputs().get(0));

        // trigger from state2
        Execution<Integer, String> results2 = stateMachine.send(state2.of(2));
        assertEquals(1, results2.getOutputs().size());
        assertEquals("3", results2.getOutputs().get(0));
    }

    @Test
    public void testRequiredState() {
        StateMachineBuilder<Void, Integer> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> out = builder.addOutputState("out");

        State<Void> in = builder.addInputState("in");
        State<Void> s2 = builder.addState("s2");
        State<Void> s3 = builder.addState("s3");
        State<Void> s4 = builder.addState("s4");
        State<Void> s5 = builder.addState("s5", Collections.singleton(out));  // s5 can only run when there is an output
        State<Void> s6 = builder.addState("s6");


        // define transitions
        builder.addTransition(in, (i) -> s5.of(null));
        builder.addTransition(s5, (i) -> s6.of(null));
        builder.addTransition(s6, (i) -> out.of(2));

        builder.addTransition(in, (i) -> s2.of(null));
        builder.addTransition(s2, (i) -> s3.of(null));
        builder.addTransition(s3, (i) -> s4.of(null));
        builder.addTransition(s4, (i) -> out.of(1));


        StateMachine<Void, Integer> stateMachine = builder.build();

        // trigger from state1
        Execution<Void, Integer> result = stateMachine.send(in.of(null));
        assertThat(result.getOutputs()).containsExactly(1, 2);
    }


    @Test
    public void testTransitionRequiredState() {
        StateMachineBuilder<Void, Integer> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> out = builder.addOutputState("out");

        State<Void> in = builder.addInputState("in");
        State<Void> s2 = builder.addState("s2");
        State<Void> s3 = builder.addState("s3");
        State<Void> s4 = builder.addState("s4");
        State<Void> s5 = builder.addState("s5");
        State<Void> s6 = builder.addState("s6");

        // define transitions
        builder.addTransition(in, (i) -> s5.of(null), setOf(out));
        builder.addTransition(s5, (i) -> s6.of(null));
        builder.addTransition(s6, (i) -> out.of(2));

        builder.addTransition(in, (i) -> s2.of(null));
        builder.addTransition(s2, (i) -> s3.of(null));
        builder.addTransition(s3, (i) -> s4.of(null));
        builder.addTransition(s4, (i) -> out.of(1));


        StateMachine<Void, Integer> stateMachine = builder.build();

        // trigger from state1
        Execution<Void, Integer> result = stateMachine.send(in.of(null));
        assertThat(result.getOutputs()).containsExactly(1, 2);
    }

    @Test
    public void testTransitionRequiredState2() {
        StateMachineBuilder<Void, Integer> builder = StateMachineBuilder.create("test");

        // define states
        State<Void> in = builder.addInputState("in");
        State<Void> s2 = builder.addState("s2");
        State<Void> s3 = builder.addState("s3");
        State<Void> s4 = builder.addState("s4");
        State<Void> s5 = builder.addState("s5");
        State<Void> s6 = builder.addState("s6");
        State<Integer> out = builder.addOutputState("out");

        // define transitions
        builder.addTransition(in, (i) -> s2.of(null));
        builder.addTransition(in, (i) -> s6.of(null), setOf(s2, s5));
        builder.addTransition(in, (i) -> s4.of(null), setOf(s2, s3));
        builder.addTransition(in, (i) -> s3.of(null), setOf(s2));
        builder.addTransition(in, (i) -> s5.of(null), setOf(s4));

        builder.addTransition(s2, (i) -> out.of(2));
        builder.addTransition(s3, (i) -> out.of(3));
        builder.addTransition(s4, (i) -> out.of(4));
        builder.addTransition(s5, (i) -> out.of(5));
        builder.addTransition(s6, (i) -> out.of(6));

        StateMachine<Void, Integer> stateMachine = builder.build();

        Execution<Void, Integer> result = stateMachine.send(in.of(null));
        assertThat(result.getOutputs()).containsExactly(2, 3, 4, 5, 6);
    }

    @Test
    public void testGetStateData() {
        StateMachineBuilder<String, String> builder = StateMachineBuilder.create("test");

        // define states
        State<String> in = builder.addInputState("in");
        State<Void> s2 = builder.addState("s2");
        State<String> out = builder.addOutputState("out");

        // define transitions
        builder.addTransition(in, (s) -> s2.of(null));
        builder.addTransition(s2, (s, context) -> out.of(context.getOrError(in)));

        StateMachine<String, String> stateMachine = builder.build();

        Execution<String, String> result = stateMachine.send(in.of("data"));
        assertThat(result.getOutputs()).containsExactly("data");
    }

    @Test
    public void testGetStateData2() {
        StateMachineBuilder<String, String> builder = StateMachineBuilder.create("test");

        // define states
        State<String> in = builder.addInputState("in");
        State<String> s2 = builder.addState("s2");
        State<String> s3 = builder.addState("s3");
        State<String> out = builder.addOutputState("out");

        // define transitions
        builder.addTransition(in, (s) -> s3.of(null));
        builder.addTransition(s3, (s, context) -> out.of(context.getOrError(s2)));

        StateMachine<String, String> stateMachine = builder.build();

        assertThatThrownBy(() -> stateMachine.send(in.of("data")))
                .isInstanceOf(StateNotReachedException.class)
                .hasMessage(s2.toString());
    }

    @Test
    public void testGetStateData3() {
        StateMachineBuilder<String, String> builder = StateMachineBuilder.create("test");

        // define states
        State<String> in = builder.addInputState("in");
        State<String> s2 = builder.addState("s2");
        State<String> s3 = builder.addState("s3");
        State<String> out = builder.addOutputState("out");

        // define transitions
        builder.addTransition(in, (s) -> s3.of(null));
        builder.addTransition(s3, (s, context) -> out.of(context.getOrDefault(s2, () -> "fallback")));

        StateMachine<String, String> stateMachine = builder.build();

        Execution<String, String> result = stateMachine.send(in.of("data"));
        assertThat(result.getOutputs()).containsExactly("fallback");
    }

    @Test
    public void testMultipleOutputs() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");
        State<String> state3 = builder.addOutputState("stringState1");
        State<String> state4 = builder.addOutputState("stringState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i * 2));
        builder.addTransition(state2, (i, c) -> state3.of(String.valueOf(i + 1)));
        builder.addTransition(state2, (i, c) -> state4.of(String.valueOf(i + 2)));
        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> results = stateMachine.send(state1.of(2));
        assertEquals(2, results.getOutputs().size());
        assertThat(results.getOutputs()).containsExactlyInAnyOrder("5", "6");
    }

    @Test
    public void testRecursiveTransition() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");
        State<String> state3 = builder.addOutputState("stringState1");

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
        Execution<Integer, String> results = stateMachine.send(state1.of(0));
        assertEquals(1, results.getOutputs().size());
        assertEquals("11", results.getOutputs().get(0));
    }

    @Test
    public void testExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<String> state2 = builder.addOutputState("intState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> state2.of("successfully handled"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<String> state2 = builder.addOutputState("intState2");

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
        Execution<Integer, String> results = stateMachine.send(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedSameExceptionHandler() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

        // define states
        State<Integer> state1 = builder.addState("intState1");

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
        assertThrows(RuntimeException.class, () -> stateMachine.send(state1.of(123)), "state1 transition to error, intentional exception");
    }

    @Test
    public void testExceptionHandler_throwingSubclass() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler_throwingSubclass");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<String> state2 = builder.addOutputState("intState2");

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new IllegalStateException("state1 transition to error, intentional exception");  // IllegalStateException is subclass of RuntimeException
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> state2.of("successfully handled"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(state1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNoOutput() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testNoOutput");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");


        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i));
        builder.addTransition(state2, (i, c) -> NextState.leaf());

        StateMachine<Integer, String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(state1.of(0));
        assertEquals(0, results.getOutputs().size());
    }


    private <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}