package com.tsm4j.core.map;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyMapTest {

    @Test
    void getDependencies_typical() {
        DependencyMap<String, Integer> mapUnderTest = DependencyMap.<String, Integer>builder()
                        .addDependencies("1", setOf(1))
                        .addDependencies("1-3", setOf(1,2,3))
                        .addDependencies("1-5", setOf(1,2,3,4,5))
                        .build();

        assertThat(mapUnderTest.addIntent("1")).isTrue();
        assertThat(mapUnderTest.addIntent("1-3")).isTrue();
        assertThat(mapUnderTest.addIntent("1-5")).isTrue();
        assertThat(mapUnderTest.satisfy(1)).containsExactly("1");
        assertThat(mapUnderTest.satisfy(2)).isEmpty();
        assertThat(mapUnderTest.satisfy(3)).containsExactly("1-3");
        assertThat(mapUnderTest.satisfy(4)).isEmpty();
        assertThat(mapUnderTest.satisfy(5)).containsExactly("1-5");

    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}