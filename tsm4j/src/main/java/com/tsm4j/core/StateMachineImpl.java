package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Slf4j
class StateMachineImpl<I, O> implements StateMachine<I, O> {

    private final ExecutionContextImpl<I, O> executionContext;

    StateMachineImpl(
            String name,
            Set<State<?>> states,
            Set<State<I>> inputStates,
            Set<State<O>> outputStates,
            Map<Class<?>, ExceptionHandlerWithContext<? extends RuntimeException>> exceptionHandlerMap
    ) {
        this.executionContext = new ExecutionContextImpl<>(
                name,
                states,
                inputStates,
                outputStates,
                exceptionHandlerMap,
                new ArrayList<>()
        );
    }

    public String getName() {
        return this.executionContext.getName();
    }

    public Execution<I, O> send(NextState<I> initState) {

        Execution<I, O> currExecution = executionContext.startNewExecution(initState.getData());

        final LinkedList<StateMachinePath<?, I, O>> pendingPaths = new LinkedList<>();
        pendingPaths.add(new StateMachinePath<>(new ArrayList<>(), (NextStateImpl<?>) initState, executionContext));

        // run
        while (!pendingPaths.isEmpty()) {

            int pendingSize = pendingPaths.size();
            boolean nothingExecuted = true;  // going to break loop if no progress made, this is needed because state may exist in queue but cant be executed because condition not met

            while (pendingSize-- > 0) {
                final StateMachinePath<?, I, O> path = pendingPaths.pop();
                if (path.isOutput()) {
                    currExecution.recordOutput((O) path.getData());
                }
                if (path.isLeaf()) {
                    currExecution.recordPath(path.getPath());
                    continue;
                }
                StateMachinePath.ExecutionResult<I, O> result = path.tryExecute(executionContext);
                pendingPaths.addAll(result.getNextPaths());
                if (result.isExecuted()) {
                    nothingExecuted = false;
                }
            }

            if (nothingExecuted) {
                break;
            }
        }

        return executionContext.endCurrentExecution();
    }
}
