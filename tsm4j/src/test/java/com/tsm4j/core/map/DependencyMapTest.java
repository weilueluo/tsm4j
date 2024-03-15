package com.tsm4j.core.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyMapTest {

    private DependencyMap<Integer, Integer> mapUnderTest;

    @BeforeEach
    void setUp() {
        this.mapUnderTest = new DependencyMap<>();
    }

    @Test
    void getDependencies_typical() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3, 4, 5, 6))).isTrue();
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void getDependencies_empty() {
        assertThat(mapUnderTest.addDependencies(1, setOf())).isTrue();
        assertThat(mapUnderTest.getDependencies(1)).isEmpty();
    }

    @Test
    void addDependencies_cannotAddToSameKeyTwice() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isFalse();
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(1, 2, 3);
    }

    @Test
    void addDependencies_typical() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(1, 2, 3);
    }

    @Test
    void addDependencies_cannotAddToSameKeyTwice2() {
        assertThat(mapUnderTest.addDependencies(1, setOf())).isTrue();
        assertThat(mapUnderTest.addDependencies(1, setOf(2, 3, 4, 5))).isFalse();
        assertThat(mapUnderTest.getDependencies(1)).isEmpty();
    }

    @Test
    void removeDependency_removeAll() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        mapUnderTest.removeDependency(1);
        mapUnderTest.removeDependency(2);
        mapUnderTest.removeDependency(3);
        assertThat(mapUnderTest.getDependencies(1)).isEmpty();
        assertThat(mapUnderTest.isFree(1)).isTrue();
    }

    @Test
    void removeDependency_removePartial() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        mapUnderTest.removeDependency(1);
        mapUnderTest.removeDependency(3);
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(2);
        assertThat(mapUnderTest.isFree(1)).isFalse();
    }

    @Test
    void crossDependency_removeAll() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        assertThat(mapUnderTest.addDependencies(2, setOf(1, 2, 3))).isTrue();
        mapUnderTest.removeDependency(1);
        mapUnderTest.removeDependency(2);
        mapUnderTest.removeDependency(3);
        assertThat(mapUnderTest.getDependencies(1)).isEmpty();
        assertThat(mapUnderTest.getDependencies(2)).isEmpty();
        assertThat(mapUnderTest.isFree(1)).isTrue();
        assertThat(mapUnderTest.isFree(2)).isTrue();
    }

    @Test
    void crossDependency_removePartial() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 2, 3))).isTrue();
        assertThat(mapUnderTest.addDependencies(2, setOf(1, 2, 3))).isTrue();
        mapUnderTest.removeDependency(1);
        mapUnderTest.removeDependency(3);
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(2);
        assertThat(mapUnderTest.getDependencies(2)).containsExactly(2);
        assertThat(mapUnderTest.isFree(1)).isFalse();
        assertThat(mapUnderTest.isFree(2)).isFalse();
    }

    @Test
    void crossDependency_removePartialAndAll() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 3))).isTrue();
        assertThat(mapUnderTest.addDependencies(2, setOf(1, 2, 3))).isTrue();
        mapUnderTest.removeDependency(1);
        mapUnderTest.removeDependency(3);
        assertThat(mapUnderTest.getDependencies(1)).isEmpty();
        assertThat(mapUnderTest.getDependencies(2)).containsExactly(2);
        assertThat(mapUnderTest.isFree(1)).isTrue();
        assertThat(mapUnderTest.isFree(2)).isFalse();
    }

    @Test
    void crossDependency_removePartialAndAll2() {
        assertThat(mapUnderTest.addDependencies(1, setOf(1, 3))).isTrue();
        assertThat(mapUnderTest.addDependencies(2, setOf(2, 3, 4))).isTrue();
        assertThat(mapUnderTest.addDependencies(3, setOf(2, 3, 4, 5))).isTrue();
        mapUnderTest.removeDependency(2);
        mapUnderTest.removeDependency(3);
        mapUnderTest.removeDependency(4);
        assertThat(mapUnderTest.getDependencies(1)).containsExactly(1);
        assertThat(mapUnderTest.getDependencies(2)).isEmpty();
        assertThat(mapUnderTest.getDependencies(3)).containsExactly(5);
        assertThat(mapUnderTest.isFree(1)).isFalse();
        assertThat(mapUnderTest.isFree(2)).isTrue();
        assertThat(mapUnderTest.isFree(3)).isFalse();
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}