# tsm4j
> :warning: This library is still under heavy development, interfaces are subject to change.

~~Typed~~ State Machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:0.0.9'
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

### Basic Example

```java
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
```

### Save and Loading data

```java
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
```
### Handling Exceptions

```java
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
```

### More Examples
For more examples see tsm4j/test.

## Why
- I prefer an intuitive graph model that is easier to debug and learns what's going on.
- I want to merge action and transition, event and state, and see if this simpler model works.
- ~~I want to use data from another state with type safety~~ (does not work nicely).

## Contributing
Feel free to open up an issue for a feature request, bug report, or pull request.
