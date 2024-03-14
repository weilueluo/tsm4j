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
- Functional
- Easy to use
- Type safe transition

## Usage

```java
public class Demo {
    public void demo() {
        // create a state machine builder with Integer input and String output named demo
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("demo");

        // define states
        // s1 has Integer input with name "s1"
        State<Integer> s1 = builder.newTransitionState("s1");
        // s2 has Integer input with name "s2"
        State<Integer> s2 = builder.newTransitionState("s2");
        // s3 has String input with name "s3"
        // it is an output state, so any value arrived at this state is considered as an output
        State<String> s3 = builder.newOutputState("s3");

        // define transitions
        // s1 --> s2 --> s3
        builder.addTransition(s1, i -> s2.of(i * 2));
        builder.addTransition(s2, i -> {
            if (i > 5) {
                return s3.of(String.valueOf(i + 1));
            } else {
                return s3.of(String.valueOf(i * 3));
            }
        });

        StateMachine<Integer, String> stateMachine = builder.build();

        // trigger state machine from s1
        assertEquals("6", stateMachine.run(s1.of(1)).getOutputs().get(0));  // 1 * 2 * 3 = 6

        // trigger state machine from s2
        assertEquals("7", stateMachine.run(s2.of(6)).getOutputs().get(0));  // 6 + 1 = 7
    }
}
```

For more usage examples see tests.

## Contributing
Feel free to open up an issue for feature request or bug report.