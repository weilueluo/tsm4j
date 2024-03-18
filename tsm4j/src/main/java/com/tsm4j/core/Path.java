package com.tsm4j.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Represents a path in the state machine
 * */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Path<E> {
    private final Path<E> previous;
    private final E current;
}
