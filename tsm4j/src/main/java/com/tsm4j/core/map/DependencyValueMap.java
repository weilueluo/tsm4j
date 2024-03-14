package com.tsm4j.core.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Like dependency map, but return custom values V instead of K when dependency is satisfied
 * */
public class DependencyValueMap<K, D, V> {
    private final DependencyMap<K, D> dependencyMap;
    private final Map<K, Set<V>> valuesMap;

    public DependencyValueMap() {
        this.dependencyMap = new DependencyMap<>();
        this.valuesMap = new HashMap<>();
    }

    public boolean containsKey(K key) {
        return this.dependencyMap.containsKey(key);
    }

    public boolean isFree(K key) {
        return this.dependencyMap.isFree(key);
    }

    public void addValues(K key, Set<V> values) {
        Set<V> currValues = valuesMap.get(key);
        if (currValues == null) {
            valuesMap.put(key, new HashSet<>(values));
        } else {
            currValues.addAll(values);
        }
    }

    public void addValue(K key, V value) {
        Set<V> currValues = valuesMap.get(key);
        if (currValues == null) {
            currValues = new HashSet<>();
            currValues.add(value);
            valuesMap.put(key, currValues);
        } else {
            currValues.add(value);
        }
    }

    public boolean addDependencies(K key, Set<D> dependencies) {
        return this.dependencyMap.addDependencies(key, dependencies);
    }

    public Set<V> removeDependency(D dependency) {
        Set<K> freedKeys = this.dependencyMap.removeDependency(dependency);
        Set<V> allFreedValues = new HashSet<>();
        freedKeys.forEach(key -> {
            Set<V> freedValues = valuesMap.remove(key);
            if (freedValues != null) {
                allFreedValues.addAll(freedValues);
            }
        });
        return allFreedValues;
    }
}
