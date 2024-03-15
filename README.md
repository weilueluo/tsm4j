# tsm4j
> :warning: This library is still under heavy development, interfaces are subject to change.

Typed State Machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:0.0.7'
```

### Maven
```xml
<dependency>
    <groupId>com.tsm4j</groupId>
    <artifactId>tsm4j</artifactId>
    <version>0.0.7</version>
</dependency>
```

## Features
- Functional (kind of)
- Easy to use
- Type safe transition
- Use state like event and transition like action

## Usage

### Basic Example

Use `StateMachineBuilder` to create a `StateMachine` by specifying your states and transitions.

```java
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
        return s3.of(String.valueOf(i * 3));  // times 3 and go to s3
    } else if (i < 10) {
        return s3.of(String.valueOf(i + 3));  // add 3 and go to s3 
    } else {
        return NextState.leaf();  // do not go to another state after finish this transition
    }
});

StateMachine<Integer, String> stateMachine = builder.build();

// trigger state machine from s1
assertEquals("6", stateMachine.send(s1.of(1)).getOutputs().get(0));  // 1 * 2 * 3 = 6
// trigger state machine from s2
assertEquals("9", stateMachine.send(s2.of(6)).getOutputs().get(0));  // 6 + 3 = 9
// trigger state machine from s2
assertEquals(0, stateMachine.send(s2.of(11)).getOutputs().size());  // no output
```

### Specifying Dependencies
You can limit a transition to be run only after some states are reached.
Similar to event in other state machine libraries.
```java
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
// transition level dependencies
builder.addTransition(in, (i) -> s6.of(null), setOf(s2, s5));  // transition to s6 must run after reaching s2 and s5
builder.addTransition(in, (i) -> s4.of(null), setOf(s2, s3));  // transition to s4 must run after reaching s2 and s3
builder.addTransition(in, (i) -> s3.of(null), setOf(s2));      // transition to s3 must run after reaching s2
builder.addTransition(in, (i) -> s5.of(null), setOf(s4));      // transition to s5 must run after reaching s4

builder.addTransition(s2, (i) -> out.of(2));
builder.addTransition(s3, (i) -> out.of(3));
builder.addTransition(s4, (i) -> out.of(4));
builder.addTransition(s5, (i) -> out.of(5));
builder.addTransition(s6, (i) -> out.of(6));

StateMachine<Void, Integer> stateMachine = builder.build();

Execution<Void, Integer> result = stateMachine.send(in.of(null));
assertThat(result.getOutputs()).containsExactly(2, 3, 4, 5, 6);  // outputs are in specified order
```
### Getting data from another state
You can query any state's data at any transition if you want to use them.

```java
StateMachineBuilder<String, String> builder = StateMachineBuilder.create("test");

// define states
State<String> in = builder.addInputState("in");
State<Void> s2 = builder.addState("s2");
State<String> out = builder.addOutputState("out");

// define transitions
builder.addTransition(in, (s) -> s2.of(null));
builder.addTransition(s2, (s, context) -> out.of(context.getOrError(in)));  // get data of "in" state

StateMachine<String, String> stateMachine = builder.build();

Execution<String, String> result = stateMachine.send(in.of("data"));
assertThat(result.getOutputs()).containsExactly("data");
```

### Handling Exceptions
You can handle exception raised from transition and handle them accordingly.
```java
StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("testExceptionHandler");

// define states
State<Integer> s1 = builder.addState("s1");
State<String> out = builder.addOutputState("out");

// define transitions
builder.addTransition(s1, (i, c) -> {
    throw new RuntimeException("s1 throw an exception!");  // some transition can throw an exception
});

// define exception handler
builder.addExceptionHandler(RuntimeException.class, (e, c) -> out.of("successfully handled"));  // handle it and transition to appropriate state

StateMachine<Integer, String> stateMachine = builder.build();

// run
Execution<Integer, String> results = stateMachine.send(s1.of(null));
assertEquals(1, results.getOutputs().size());
assertEquals("successfully handled", results.getOutputs().get(0));
```
### More Examples
For more examples see tests.

## Contributing
Feel free to open up an issue for feature request or bug report.