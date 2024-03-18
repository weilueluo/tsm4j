package com.tsm4j.core;

import javax.swing.plaf.nimbus.State;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@FunctionalInterface
public interface Transition<E extends Enum<E>> extends Consumer<Context<E>> {
}
