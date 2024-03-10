package com.tsm4j.core;

import com.tsm4j.core.statetype.AbstractStateType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
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
    private final List<TransitionWithContext<T>> transitions = new ArrayList<>();

    public NextState<T> of(T data) {
        return NextStateImpl.of(this, data);
    }

    void addTransition(Transition<T> transition) {
        this.transitions.add(transition);
    }

    void addTransition(TransitionWithContext<T> transition) {
        this.transitions.add(transition);
    }

    List<Supplier<NextStateImpl<?>>> applyTransitions(T input, Context context) {
        List<Supplier<NextStateImpl<?>>> suppliers = new ArrayList<>(this.transitions.size());
        for (TransitionWithContext<T> transition : this.transitions) {
            suppliers.add(() -> (NextStateImpl<?>) transition.apply(input, context));  // this class is safe... as long as we only have this one implementing class
        }
        return suppliers;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class Id implements State.Id {
        private final String name;
        private final AbstractStateType type;
    }
}
