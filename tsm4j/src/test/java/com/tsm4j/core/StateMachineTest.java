package com.tsm4j.core;

import com.tsm4j.core.exception.StateNotReachedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateMachineTest {

    @Test
    public void demo_basic() {
        // create a state machine builder with Integer output
        StateMachineBuilder<Integer> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<Integer> s2 = builder.addState();
        State<Integer> s3 = builder.addOutputState(); // any value arrived at this state is output

        // define transitions
        builder.addTransition(s1, (Integer i) -> s2.of(i * 2));
        builder.addTransition(s2, (Integer i) -> {
            if (i < 5) {
                return s3.of(i * 3);
            } else if (i < 10) {
                return s3.of(i + 3);
            } else {
                return NextState.leaf();
            }
        });

        StateMachine<Integer> stateMachine = builder.build();

        // trigger state machine from s1
        assertEquals(6, stateMachine.send(s1.of(1)).getOutputs().get(0));  // 1 * 2 * 3 = 6
        // trigger state machine from s2
        assertEquals(9, stateMachine.send(s2.of(6)).getOutputs().get(0));  // 6 + 3 = 9
        // trigger state machine from s2
        assertEquals(0, stateMachine.send(s2.of(11)).getOutputs().size());  // no output
    }

    @Test
    public void test() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<Integer> s2 = builder.addState();
        State<String> out = builder.addOutputState();

        // define transitions
        builder.addTransition(s1, (Integer i) -> s2.of(i * 2));
        builder.addTransition(s2, (Integer i) -> out.of(String.valueOf(i + 1)));
        StateMachine<String> stateMachine = builder.build();

        // trigger from s1
        Execution<Integer, String> results1 = stateMachine.send(s1.of(2));
        assertEquals(1, results1.getOutputs().size());
        assertEquals("5", results1.getOutputs().get(0));

        // trigger from s2
        Execution<Integer, String> results2 = stateMachine.send(s2.of(2));
        assertEquals(1, results2.getOutputs().size());
        assertEquals("3", results2.getOutputs().get(0));
    }

    @Test
    public void testTransitionRequiredState() {
        StateMachineBuilder<Integer> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> out = builder.addOutputState();
        State<Void> in = builder.addState();
        State<Void> s2 = builder.addState();
        State<Void> s3 = builder.addState();
        State<Void> s4 = builder.addState();

        // define transitions
        builder.addTransition(in, () -> out.of(2), setOf(out));

        builder.addTransition(in, () -> s2.of());
        builder.addTransition(s2, () -> s3.of());
        builder.addTransition(s3, () -> s4.of());
        builder.addTransition(s4, () -> out.of(1));

        StateMachine<Integer> stateMachine = builder.build();

        // trigger from state1
        Execution<Void, Integer> result = stateMachine.send(in.of());
        assertThat(result.getOutputs()).containsExactly(1, 2);
    }

    @Test
    public void demo_specifyingDependencies() {
        StateMachineBuilder<Integer> builder = StateMachineBuilder.newInstance();

        // define states
        State<Void> in = builder.addState();
        State<Integer> s1 = builder.addOutputState();
        State<Integer> s2 = builder.addOutputState();
        State<Integer> s3 = builder.addOutputState();
        State<Integer> s4 = builder.addOutputState();
        State<Integer> s5 = builder.addOutputState();

        // define transitions
        builder.addTransition(in, () -> s1.of(1));
        builder.addTransition(in, () -> s2.of(2), setOf(s1));      // will reach s2 after s1
        builder.addTransition(in, () -> s3.of(3), setOf(s2));      // will reach s3 after s2
        builder.addTransition(in, () -> s4.of(4), setOf(s3));      // will reach s4 after s3
        builder.addTransition(in, () -> s5.of(5), setOf(s4));      // will reach s5 after s4

        StateMachine<Integer> stateMachine = builder.build();

        Execution<Void, Integer> result = stateMachine.send(in.of());
        assertThat(result.getOutputs()).containsExactly(1, 2, 3, 4, 5);  // outputs are in specified order
    }

    @Test
    public void testGetStateData() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<String> in = builder.addState();
        State<Void> s2 = builder.addState();
        State<String> out = builder.addOutputState();

        // define transitions
        builder.addTransition(in, () -> s2.of());
        builder.addTransition(s2, (Context context) -> out.of(context.getOrError(in)));  // get data from "in" state using context

        StateMachine<String> stateMachine = builder.build();

        Execution<String, String> result = stateMachine.send(in.of("data"));
        assertThat(result.getOutputs()).containsExactly("data");
    }

    @Test
    public void testGetStateData2() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<String> in = builder.addState();
        State<String> s2 = builder.addState();
        State<String> s3 = builder.addState();
        State<String> out = builder.addOutputState();

        // define transitions
        builder.addTransition(in, () -> s3.of());
        builder.addTransition(s3, (Context context) -> out.of(context.getOrError(s2)));

        StateMachine<String> stateMachine = builder.build();

        assertThatThrownBy(() -> stateMachine.send(in.of("data")))
                .isInstanceOf(StateNotReachedException.class)
                .hasMessage(s2.toString());
    }

    @Test
    public void testGetStateData3() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<String> in = builder.addState();
        State<String> s2 = builder.addState();
        State<String> s3 = builder.addState();
        State<String> out = builder.addOutputState();

        // define transitions
        builder.addTransition(in, () -> s3.of());
        builder.addTransition(s3, (Context context) -> out.of(context.getOrDefault(s2, () -> "fallback")));

        StateMachine<String> stateMachine = builder.build();

        Execution<String, String> result = stateMachine.send(in.of("data"));
        assertThat(result.getOutputs()).containsExactly("fallback");
    }

    @Test
    public void testMultipleOutputs() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<Integer> s2 = builder.addState();
        State<String> s3 = builder.addOutputState();
        State<String> s4 = builder.addOutputState();

        // define transitions
        builder.addTransition(s1, (i, c) -> s2.of(i * 2));
        builder.addTransition(s2, (i, c) -> s3.of(String.valueOf(i + 1)));
        builder.addTransition(s2, (i, c) -> s4.of(String.valueOf(i + 2)));
        StateMachine<String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> results = stateMachine.send(s1.of(2));
        assertEquals(2, results.getOutputs().size());
        assertThat(results.getOutputs()).containsExactlyInAnyOrder("5", "6");
    }

    @Test
    public void testRecursiveTransition() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> state1 = builder.addState();
        State<Integer> state2 = builder.addState();
        State<String> state3 = builder.addOutputState();

        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i + 1));
        builder.addTransition(state2, (i, c) -> {
            if (i < 10) {
                return state1.of(i);
            } else {
                return state3.of(String.valueOf(i + 1));
            }
        });
        StateMachine<String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> results = stateMachine.send(state1.of(0));
        assertEquals(1, results.getOutputs().size());
        assertEquals("11", results.getOutputs().get(0));
    }

    @Test
    public void demo_handleException() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<String> out = builder.addOutputState();

        // define transitions
        builder.addTransition(s1, (i, c) -> {
            throw new RuntimeException("exception!");  // throw an exception
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, () -> out.of("successfully handled"));  // handle it and move to some state

        StateMachine<String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(s1.of());
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedExceptionHandler() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<String> s2 = builder.addOutputState();

        // define transitions
        builder.addTransition(s1, () -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> {
            throw new IllegalStateException("intentional error from handling runtime exception");
        });
        builder.addExceptionHandler(IllegalStateException.class, (e, c) -> s2.of("successfully handled"));

        StateMachine<String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(s1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNestedSameExceptionHandler() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> state1 = builder.addState();

        // define transitions
        builder.addTransition(state1, (i, c) -> {
            throw new RuntimeException("state1 transition to error, intentional exception");
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> {
            throw e; // throw the same exception should tell the state machine to not handle it, otherwise will get infinite recursion
        });

        StateMachine<String> stateMachine = builder.build();

        // run
        assertThrows(RuntimeException.class, () -> stateMachine.send(state1.of(123)), "state1 transition to error, intentional exception");
    }

    @Test
    public void testExceptionHandler_throwingSubclass() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> s1 = builder.addState();
        State<String> s2 = builder.addOutputState();

        // define transitions
        builder.addTransition(s1, (i, c) -> {
            throw new IllegalStateException("state1 transition to error, intentional exception");  // IllegalStateException is subclass of RuntimeException
        });

        // define exception handler
        builder.addExceptionHandler(RuntimeException.class, (e, c) -> s2.of("successfully handled"));

        StateMachine<String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(s1.of(123));
        assertEquals(1, results.getOutputs().size());
        assertEquals("successfully handled", results.getOutputs().get(0));
    }

    @Test
    public void testNoOutput() {
        StateMachineBuilder<String> builder = StateMachineBuilder.newInstance();

        // define states
        State<Integer> state1 = builder.addState();
        State<Integer> state2 = builder.addState();


        // define transitions
        builder.addTransition(state1, (i, c) -> state2.of(i));
        builder.addTransition(state2, (i, c) -> NextState.leaf());

        StateMachine<String> stateMachine = builder.build();

        // run
        Execution<Integer, String> results = stateMachine.send(state1.of(0));
        assertEquals(0, results.getOutputs().size());
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}