# tsm4j

Typed State Machine for Java

## Install

### Gradle
```xml
implementation 'com.tsm4j:tsm4j:0.0.2'
```

### Maven
```groovy
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
        // Input type is Integer and output type is String
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("demo");

        // Define states
        // s1 takes Integer as input and has name "1"
        State<Integer> s1 = builder.newTransitionState("1");
        // s2 takes Integer as input and has name "2"
        State<Integer> s2 = builder.newTransitionState("2");
        // s3 takes String as input, any value arrive at this state is considered as a output
        State<String> s3 = builder.newOutputState("3");  

        // Define transitions
        // s1 multiply given integer by 2 and transition to s2
        builder.addTransition(s1, (inp, ctx) -> s2.of(inp * 2));
        // s2 adds given integer by 1 and transition to s3
        builder.addTransition(s2, (inp, ctx) -> s3.of(String.valueOf(inp + 1)));

        // Build and run
        StateMachine<Integer, String> stateMachine = builder.build();
        System.out.println(stateMachine.run(s1.of(2)).getOutputs().get(0));  // 2 * 2 + 1 = 5
        System.out.println(stateMachine.run(s2.of(2)).getOutputs().get(0));  // 2 + 1 = 3
    }
}
```

For more usage examples see tests.
