# tsm4j

Typed State Machine for Java

## Install

### Gradle
```
implementation 'com.tsm4j:tsm4j:0.0.2'
```

### Maven
```xml
<dependency>
    <groupId>com.tsm4j</groupId>
    <artifactId>tsm4j</artifactId>
    <version>0.0.2</version>
</dependency>
```

## Usage

```java
public class Demo {
    public void demo() {
        // Create a builder
        StateMachineBuilder<Integer, Integer> builder = StateMachineBuilder.create("demo");

        // Define states
        State<Integer> s1 = builder.newTransitionState("1");
        State<Integer> s2 = builder.newTransitionState("2");
        State<Integer> s3 = builder.newOutputState("3");

        // Define transitions: s1 --> s2 --> s3
        builder.addTransition(s1, (inp, ctx) -> s2.of(inp * 2));
        builder.addTransition(s2, (inp, ctx) -> s3.of(inp + 1));

        // Build and run
        StateMachine<Integer, Integer> stateMachine = builder.build();
        System.out.println(stateMachine.run(s1.of(2)).getOutputs().get(0));  // we get 2 * 2 + 1 = 5
        System.out.println(stateMachine.run(s2.of(2)).getOutputs().get(0));  // we get 2 + 1 = 3
    }
}
```

For more usage examples see tests.
