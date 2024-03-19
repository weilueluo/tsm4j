package com.tsm4j.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyMapTest {

    @Test
    void orderedSatisfy() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("1", setOf(1))
                .addDependencies("1-3", setOf(1, 2, 3))
                .addDependencies("1-5", setOf(1, 2, 3, 4, 5))
                .build();

        assertThat(mapUnderTest.satisfy(1)).containsExactly("1");
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
        assertThat(mapUnderTest.satisfy(4)).isEmpty();
        assertThat(mapUnderTest.satisfy(5)).containsExactly("1-5");
    }

    @Test
    void multipleSameValue() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("1", setOf(1))
                .build();

        assertThat(mapUnderTest.satisfy(1)).containsExactly("1");
        assertThat(mapUnderTest.satisfy(1)).containsExactly("1");
        assertThat(mapUnderTest.satisfy(1)).containsExactly("1");
    }

    @Test
    void sameDependencies() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("a", setOf(1, 2))
                .addDependencies("b", setOf(1, 2))
                .build();

        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    void unevenSatisfyOrder() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("1-3", setOf(1, 2, 3))
                .build();

        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
        assertThat(mapUnderTest.satisfy(3)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).containsExactly("1-3");
    }

    @Test
    void overlappingDependencies() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("1-3", setOf(1, 2, 3))
                .addDependencies("2-4", setOf(2, 3, 4))
                .addDependencies("3-5", setOf(3, 4, 5))
                .build();

        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
        assertThat(mapUnderTest.satisfy(4)).containsExactly("2-4");
        assertThat(mapUnderTest.satisfy(5)).containsExactly("3-5");
    }

    @Test
    void addingDependenciesOfSameKey() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                .addDependencies("1-3", setOf(1))
                .addDependencies("1-3", setOf(2))
                .addDependencies("1-3", setOf(3))
                .build();

        assertThat(mapUnderTest.satisfy(1)).isEmpty();
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}