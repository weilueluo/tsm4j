package com.tsm4j.core;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*
* Wrap DependencyMap, provides an in-memory buffer of the made available keys
* */
class DependencyQueue<K, D> {
    private final DependencyMap<K, D> dependencyMap;
    private final LinkedList<K> buffer;

    DependencyQueue(Map<K, Set<D>> dependencies) {
        this.dependencyMap = new DependencyMap<>(dependencies);
        this.buffer = new LinkedList<>();
    }

    void satisfy(D dep) {
        this.buffer.addAll(this.dependencyMap.satisfy(dep));
    }

    boolean isEmpty() {
        return this.buffer.isEmpty();
    }

    K pop() {
        return this.buffer.pop();
    }
}
