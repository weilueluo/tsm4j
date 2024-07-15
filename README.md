# tsm4j

A tiny, in-memory, state machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:1.0.2'
```

### Maven
```xml
<dependency>
    <groupId>com.tsm4j</groupId>
    <artifactId>tsm4j</artifactId>
    <version>1.0.2</version>
</dependency>
```

## Usage

### Example
An example of defining and running a state machine:

```java
StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
    .addTransition(TestState.HUNGRY, TestState.MAKE_FOOD)
    .addTransition(TestState.MAKE_FOOD, TestState.FOOD_IS_READY)
    .addTransition(TestState.FOOD_IS_READY, TestState.EAT_FOOD)
    .addTransition(TestState.EAT_FOOD, TestState.FULL)
    .build();

assertThat(stateMachine.send(TestState.HUNGRY).reached(TestState.FULL)).isTrue();
```

You can bind a listener which is called whenever some state(s) is reached
```java
StateMachine<TestState> stateMachine = StateMachineBuilder.from(TestState.class)
    .addTransition(TestState.NO_FOOD, TestState.MAKE_FOOD)
    .addListener(TestState.MAKE_FOOD, context -> {
        System.out.println("making food...");
        context.queue(TestState.FOOD_IS_READY);
    })
    .addListener(debugLoggingListener())
    .build();

assertThat(stateMachine.send(TestState.NO_FOOD).reached(TestState.FOOD_IS_READY)).isTrue();
```

### More Examples
see tsm4j/test.

## About the older version
In short, it was going in the wrong direction, we should separate state machine from action.

## Contributing
Feel free to open up an issue for a feature request, bug report, or pull request.
