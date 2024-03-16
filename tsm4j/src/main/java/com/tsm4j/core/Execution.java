package com.tsm4j.core;

import java.util.List;
import java.util.Map;

/*
 * Represents a complete execution of the state machine
 * */
public interface Execution<I, O> {

    /*
     * Returns all paths
     * */
    List<List<State<?>>> getPaths();

    /*
     * Returns all outputs generated
     * */
    List<O> getOutputs();

    /*
     * Returns the input state
     * */
    State<I> getInputState();

    /*
     * Returns the input data
     * */
    I getInput();

    /*
     * Returns the map of state to latest data
     * */
    Map<State<?>, Object> getStateDataMap();
}
