package com.tsm4j;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Supplier;


// T denote the data type of this State
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PACKAGE)
public class State<T> implements Comparable<State<?>> {

    public static final int HIGHEST_PRECEDENCE = StateId.HIGHEST_PRECEDENCE;
    public static final int LOWEST_PRECEDENCE = StateId.LOWEST_PRECEDENCE;
    public static final int DEFAULT_PRECEDENCE = StateId.DEFAULT_PRECEDENCE;

    @NonNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final StateId id;
    @Getter
    private final PriorityQueue<OrderedTransition<T>> orderedTransitions = new PriorityQueue<>();

    public NextState<T> of(T data) {
        return NextState.of(this, data);
    }

    // only state machine builder can modify this object, so package private
    void addTransition(Transition<T> transition, int order) {
        this.orderedTransitions.offer(new OrderedTransition<>(transition, order));
    }

    List<Supplier<NextState<?>>> getOrderedNextStateSuppliers(T input, Context context) {
        List<Supplier<NextState<?>>> orderedTransitions = new ArrayList<>(this.orderedTransitions.size());
        PriorityQueue<OrderedTransition<T>> clone = new PriorityQueue<>(this.orderedTransitions);
        while (!clone.isEmpty()) {
            Transition<T> transition = clone.poll().transition;
            orderedTransitions.add(() -> transition.apply(input, context));
        }
        return orderedTransitions;
    }

    @Override
    public int compareTo(State<?> o) {
        return this.id.compareTo(o.id);
    }

    @Value
    static class OrderedTransition<T> implements Comparable<OrderedTransition<?>> {
        Transition<T> transition;
        int order;

        @Override
        public int compareTo(OrderedTransition o) {
            return o.order - this.order;
        }

    }
}
