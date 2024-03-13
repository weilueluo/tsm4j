package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
class StateMachineImpl<I, O> implements StateMachine<I, O> {

    private final Set<State<?>> states;
    private final Set<State<I>> inputStates;
    private final Set<State<O>> outputStates;
    private final Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap;
    @Getter
    private final String name;

    StateMachineImpl(
            String name,
            Set<State<?>> states,
            Set<State<I>> inputStates,
            Set<State<O>> outputStates,
            Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap
    ) {
        this.name = name;
        this.states = states;
        this.inputStates = inputStates;
        this.outputStates = outputStates;
        this.exceptionHandlerMap = exceptionHandlerMap;
    }

    public Execution<I, O> send(NextState<I> initState) {

        final StateMachinePath<I, I, O> initPath = new StateMachinePath<>(new ArrayList<>(), (NextStateImpl<I>) initState);
        final ExecutionContextImpl<I, O> context = new ExecutionContextImpl<>(name, states, exceptionHandlerMap, initPath);

        this.execute(context);

        return context.getExecution();
    }

    private void execute(ExecutionContextImpl<I, O> context) {
        PathQueue<I, O> queue = context.getPathQueue();
        // run
        while (!queue.isEmpty()) {
            final StateMachinePath<?, I, O> path = queue.pop();
            if (path.isOutput()) {
                context.recordOutput((O) path.getData());
            }
            if (path.isLeaf()) {
                context.recordPath(path.getPath());
                continue;
            }
            queue.addAll(path.next(context));
        }
    }
}
