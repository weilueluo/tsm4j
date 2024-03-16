package com.tsm4j.core.exception;

import lombok.experimental.StandardException;

/*
 * StateNotReachedException represent user is trying to use a state that is not reached in the current execution.
 * */
@StandardException
public class StateNotReachedException extends Tsm4jException {
}
