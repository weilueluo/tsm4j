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
        // Create a state machine builder with input and output type
        StateMachineBuilder<Integer, String> builder = StateMachineBuilder.create("demo");

        // Define states
        State<Integer> state1 = builder.newTransitionState("int state 1");
        State<Integer> state2 = builder.newTransitionState("int state 2");
        State<String> state3 = builder.newOutputState("string output state");

        // Define transitions
        // Our graph: state1 (times 2) ----> state2 (plus 1) ----> state3 (to string)
        builder.addTransition(state1, (i, c) -> state2.of(i * 2));
        builder.addTransition(state2, (i, c) -> state3.of(String.valueOf(i + 1)));
        StateMachine<Integer, String> stateMachine = builder.build();

        // Trigger state machine from state1
        StateMachineResult<String> results1 = stateMachine.run(state1.of(2));
        System.out.println(results1.getOutputs().get(0)); // we get 2 * 2 + 1 = 5

        // Trigger state machine from state2
        StateMachineResult<String> results2 = stateMachine.run(state2.of(2));
        System.out.println(results2.getOutputs().get(0));  // we get 2 + 1 = 3
    }
}
```

For more usage examples see tests.