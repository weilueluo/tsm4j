# tsm4j
> :warning: This library is still under heavy development, interfaces are subject to change.

State Machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:0.1.0'
```

### Maven
```xml
<dependency>
    <groupId>com.tsm4j</groupId>
    <artifactId>tsm4j</artifactId>
    <version>0.0.9</version>
</dependency>
```

## Usage
Tsm4j tries to provide a simple and intuitive state machine model with minimum configuration. 
The definition of state machine is just **states** and **transitions** that will be executed when some states is reached.

### Basic Example
An example of a class holding some states:
```java
private static class MyState {
    public static final State<Void> HUNGRY = State.create();
    public static final State<Void> NO_FOOD = State.create();
    public static final State<Void> FOOD_IS_NOT_READY = State.create();
    public static final State<Void> MAKE_FOOD = State.create();
    public static final State<Void> FOOD_IS_READY = State.create();
    public static final State<Void> NOT_HUNGRY = State.create();

    public static final State<Integer> ATTEMPTS = State.create();
}
```
An example of defining and running a state machine:

```java
StateMachine stateMachine = StateMachineBuilder.newInstance()
        .addTransition(setOf(MyState.HUNGRY, MyState.NO_FOOD), context -> context.send(MyState.MAKE_FOOD))
        .addTransition(MyState.MAKE_FOOD, context -> {
            Supplier<Boolean> tryMakeFood = () -> true;
            if (tryMakeFood.get()) {
                context.send(MyState.FOOD_IS_READY);
            } else {
                context.send(MyState.FOOD_IS_NOT_READY);
            }
        })
        .addTransition(setOf(MyState.HUNGRY, MyState.FOOD_IS_READY), context -> context.send(MyState.NOT_HUNGRY))
        .build();

assertTrue(stateMachine.send(setOf(MyState.HUNGRY, MyState.NO_FOOD)).hasReached(MyState.NOT_HUNGRY));
```

### Save and Loading data
Note that all states have a type associated with them, this is to allow you to save and load data from an arbitrary state,
To ensure the data is available in some transition, specify that state as a dependency.
```
// ...
.addTransition(MyState.MAKE_FOOD, context -> {
    int attempts = context.getOrDefault(MyState.ATTEMPTS, () -> 0);
    if (attempts > 3) {
        context.send(MyState.FOOD_IS_READY);
    } else {
        context.send(MyState.ATTEMPTS, attempts + 1);
        context.send(MyState.MAKE_FOOD);
    }
})
.addTransition(setOf(MyState.HUNGRY, MyState.FOOD_IS_READY, MyState.ATTEMPTS), context -> {
    System.out.println("attempts: " + context.getOrError(MyState.ATTEMPTS));
    context.send(MyState.NOT_HUNGRY);
})
.build();
// ...
```
### Handling Exceptions
You can handle exception raised from the state machine by defining an exception handler
```java
StateMachine stateMachine = StateMachineBuilder.newInstance()
        .addTransition(MyState.HUNGRY, context -> {
            throw new RuntimeException("exception!");
        })
        .addExceptionHandler(RuntimeException.class, (context, e) -> {
            // custom eat food logic
            context.send(MyState.NOT_HUNGRY);
        })
        .build();

assertTrue(stateMachine.send(MyState.HUNGRY).hasReached(MyState.NOT_HUNGRY));
```

### More Examples
For more examples see tsm4j/test.

## Why
- I prefer an intuitive graph model that is easier to debug and learns what's going on.
- I want to merge action and transition, event and state, and see if this simpler model works.
- I want to use data from another state with type safety.

## Contributing
Feel free to open up an issue for a feature request, bug report, or pull request.
