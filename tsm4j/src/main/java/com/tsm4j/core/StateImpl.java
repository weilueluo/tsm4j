package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class StateImpl<T> implements State<T> {

    @NonNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final Id id;
    private final List<OrderedTransition<T>> transitions = new ArrayList<>();
    private boolean sorted = false;

    public NextState<T> of(T data) {
        return NextStateImpl.of(this, data);
    }

    @Override
    public void addTransition(Transition<T> transition) {
        this.sorted = false;
        this.addTransition(transition, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public void addTransition(TransitionWithContext<T> transition) {
        this.sorted = false;
        this.addTransition(transition, Order.DEFAULT_PRECEDENCE);
    }

    @Override
    public void addTransition(Transition<T> transition, int order) {
        this.sorted = false;
        this.transitions.add(new OrderedTransition<>(transition, order));
    }

    @Override
    public void addTransition(TransitionWithContext<T> transition, int order) {
        this.sorted = false;
        this.transitions.add(new OrderedTransition<>(transition, order));

    }

    @Override
    public int compareTo(State<?> o) {
        return this.id.compareTo(o.getId());
    }

    List<Supplier<NextStateImpl<?>>> applyTransitions(T input, Context context) {
        if (!this.sorted) {
            Collections.sort(this.transitions);
            this.sorted = true;
        }
        List<Supplier<NextStateImpl<?>>> suppliers = new ArrayList<>(this.transitions.size());
        for (OrderedTransition<T> transition : this.transitions) {
            suppliers.add(() -> (NextStateImpl<?>) transition.getTransition().apply(input, context));  // this class is safe... as long as we only have this one implementing class
        }
        return suppliers;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Id implements State.Id {
        private final String name;
        private final AbstractStateType type;
        private final int order;

        @Override
        public int compareTo(State.Id o) {
            if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1;  // should not have null, if any, move them to the end.
            }
            int nameCmp = name.compareTo(o.getName());
            if (nameCmp != 0) {
                return nameCmp;
            }
            if (!type.equals(o.getType())) {
                return 0; // type has no order
            }
            return o.getOrder() - order;  // highest order first
        }
    }
}
