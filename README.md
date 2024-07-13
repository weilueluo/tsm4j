# tsm4j

A tiny, in-memory, state machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:1.0.0'
```

### Maven
```xml
<dependency>
    <groupId>com.tsm4j</groupId>
    <artifactId>tsm4j</artifactId>
    <version>1.0.0</version>
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

### More Examples
For more examples see tsm4j/test.

## About the older version
It was going in the wrong direction, we should separate state machine from action.


## Contributing
Feel free to open up an issue for a feature request, bug report, or pull request.
