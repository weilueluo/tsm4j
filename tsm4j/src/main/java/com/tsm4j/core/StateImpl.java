package com.tsm4j.core.impl;

import com.tsm4j.core.OrderedTransition;
import com.tsm4j.core.TransitionWithContext;
import com.tsm4j.core.impl.ContextImpl;
import com.tsm4j.core.impl.NextStateImpl;
import com.tsm4j.core.state.State;
import com.tsm4j.core.statetypes.StateType;
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
@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PACKAGE)
public class StateImpl<T> implements State<T> {

    @NonNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final Id id;
    private final List<OrderedTransition<T>> transitions = new ArrayList<>();

    public NextStateImpl<T> of(T data) {
        return NextStateImpl.of(this, data);
    }

    // only state machine builder can modify this object, so package private
    void addTransition(TransitionWithContext<T> transition, int order) {
        this.transitions.add(new OrderedTransition<>(transition, order));
    }

    List<Supplier<NextStateImpl<?>>> getOrderedResultSupplier(T input, ContextImpl context) {
        Collections.sort(this.transitions);
        List<Supplier<NextStateImpl<?>>> suppliers = new ArrayList<>(this.transitions.size());
        this.transitions.forEach(transition -> suppliers.add(() -> transition.getTransition().apply(input, context)));
        return suppliers;
    }

    @Override
    public int compareTo(State<?> o) {
        return this.id.compareTo(o.getId());
    }

    @Getter
    @EqualsAndHashCode
    public static class Id implements State.Id {
        String name;
        StateType type;
        int order;

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
