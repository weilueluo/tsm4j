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

## Usage

### Basic Example

Use `StateMachineBuilder` to create a `StateMachine` by specifying your states and transitions.

```java
// create a state machine builder with String output
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
```

### Specifying Dependencies
You can limit a transition to be run only after some states are reached.
Similar to event in other state machine libraries.
```java
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
```
### Getting data from another state
You can query any state's data at any transition if you want to use them.

```java
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
```

### Handling Exceptions
You can handle exception raised from transition and handle them accordingly.
```java
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
```
### More Examples
For more examples see tests.

## Why
- I prefer an intuitive graph model that is easier to debug and learns what's going on.
- I want to merge action and transition, event and state, and see if this simpler model works.
- I want to use data from another state.

## Contributing
Feel free to open up an issue for feature request, bug report, or pull request.
