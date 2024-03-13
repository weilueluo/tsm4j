package com.tsm4j.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");
        State<Integer> state3 = builder.addState("intState3", state2);  // state3 depends on state2

        State<String> state4 = builder.addOutputState("out", state2);

        // define transitions
        builder.addTransition(state1, (i) ->  state3.of(1));
        builder.addTransition(state1, (i) ->  state2.of(1));

        builder.addTransition(state3, (i) -> state4.of("3->4"));
        builder.addTransition(state2, (i) -> state4.of("2->4"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> result = stateMachine.send(state1.of(1));
        assertThat(result.getOutputs()).containsExactly("2->4", "3->4");
    }

    @Test
    public void testRequiredState2() {
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("test");

        // define states
        State<Integer> state1 = builder.addState("intState1");
        State<Integer> state2 = builder.addState("intState2");
        State<Integer> state5 = builder.addState("intState5");
        State<Integer> state3 = builder.addState("intState3", state5);  // state3 depends on state5

        State<String> state4 = builder.addOutputState("out", state2);

        // define transitions
        builder.addTransition(state1, (i) ->  state3.of(1));
        builder.addTransition(state1, (i) ->  state2.of(1));

        builder.addTransition(state3, (i) -> state4.of("3->4"));
        builder.addTransition(state2, (i) -> state4.of("2->4"));

        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger from state1
        Execution<Integer, String> result = stateMachine.send(state1.of(1));
        assertThat(result.getOutputs()).containsExactly("2->4");  // no "3->4" output because condition not satisfied
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

//    @Test
//    public void testContext() {
//        StateMachineModelBuilder<Integer, String> builder = StateMachineModelBuilder.create("testContext");
//
//        // define states
//        State<Integer> state1 = builder.addState("intState1");
//        State<String> state2 = builder.addState("intState2");
//
//
//        // define transitions
//        builder.addTransition(state1, (i, c) -> {
//            if (i > 10) {
//                return state2.of("done");
//            }
//            c.set(String.valueOf(i), "cache item " + i);
//            return state1.of(i + 1);
//        });
//
//        builder.addTransition(state2, (i, c) -> {
//            for (int j = 0; j <= 10; j++) {
//                Optional<String> cacheItem = c.get(String.valueOf(j), String.class);
//                assertTrue(cacheItem.isPresent());
//                assertThat(cacheItem.get()).isEqualTo("cache item " + j);
//            }
//            return NextState.leaf();
//        });
//
//        StateMachine<Integer, String> stateMachine = builder.build();
//
//        // run
//        Execution<Integer, String> results = stateMachine.send(state1.of(0));
//        assertEquals(0, results.getOutputs().size());
//    }
//
//    @Test
//    public void testContext_setByObject() {
//        StateMachineModelBuilder<Integer, String> builder = StateMachineModelBuilder.create("testContext");
//
//        // define states
//        State<Integer> state1 = builder.addState("intState1");
//        State<Integer> state2 = builder.addState("intState2");
//
//
//        // define transitions
//        builder.addTransition(state1, (i, c) -> {
//            c.set("cache item");
//            return state2.of(i);
//        });
//
//        builder.addTransition(state2, (i, c) -> {
//            Optional<String> cacheItem = c.get(String.class);
//            assertTrue(cacheItem.isPresent());
//            assertThat(cacheItem.get()).isEqualTo("cache item");
//            return NextState.leaf();
//        });
//
//        StateMachine<Integer, String> stateMachine = builder.build();
//
//        // run
//        Execution<Integer, String> results = stateMachine.send(state1.of(0));
//        assertEquals(0, results.getOutputs().size());
//    }
//
//    @Test
//    public void testContext_getOrError() {
//        StateMachineModelBuilder<Integer, String> builder = StateMachineModelBuilder.create("testContext");
//
//        // define states
//        State<Integer> state1 = builder.addState("intState1");
//        State<Integer> state2 = builder.addState("intState2");
//
//
//        // define transitions
//        builder.addTransition(state1, (i, c) -> {
//            c.set("cache item");
//            return state2.of(i);
//        });
//
//        builder.addTransition(state2, (i, c) -> {
//            String cacheItem = c.getOrError(String.class);
//            assertThat(cacheItem).isEqualTo("cache item");
//            return NextState.leaf();
//        });
//
//        StateMachine<Integer, String> stateMachine = builder.build();
//
//        // run
//        Execution<Integer, String> results = stateMachine.send(state1.of(0));
//        assertEquals(0, results.getOutputs().size());
//    }
//
//    @Test
//    public void testContext_getOrDefault() {
//        StateMachineModelBuilder<Integer, String> builder = StateMachineModelBuilder.create("testContext");
//
//        // define states
//        State<Integer> state1 = builder.addState("intState1");
//        State<Integer> state2 = builder.addState("intState2");
//
//
//        // define transitions
//        builder.addTransition(state1, (i, c) -> {
//            c.set("cache item");
//            return state2.of(i);
//        });
//
//        builder.addTransition(state2, (i, c) -> {
//            String cacheItem = c.getOrDefault(String.class, null);
//            assertThat(cacheItem).isEqualTo("cache item");
//
//            Integer cacheItem2 = c.getOrDefault(Integer.class, () -> 123);
//            assertThat(cacheItem2).isEqualTo(123);
//
//            return NextState.leaf();
//        });
//
//        StateMachine<Integer, String> stateMachine = builder.build();
//
//        // run
//        Execution<Integer, String> results = stateMachine.send(state1.of(0));
//        assertEquals(0, results.getOutputs().size());
//    }
}