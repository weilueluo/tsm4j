package com.tsm4j.core.queue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyMap<K, V> {
    private final Map<K, Set<V>> k2v;
    private final Map<V, Set<K>> v2k;

    public DependencyMap() {
        this.k2v = new HashMap<>();
        this.v2k = new HashMap<>();
    }

    public void addDependencies(K key, Set<V> dependencies) {
        if (k2vAdd(key, dependencies)) {
            dependencies.forEach(v -> v2kAdd(v, key));
        }
    }

    public Set<K> removeDependency(V dependency) {
        Set<K> freed = new HashSet<>();
        Set<K> affectedKeys = this.v2k.remove(dependency);
        if (affectedKeys != null) {
            affectedKeys.forEach(key -> {
                Set<V> keyDependencies = this.k2v.get(key);
                if (keyDependencies == null) {
                    freed.add(key);
                } else {
                    keyDependencies.remove(dependency);
                    if (keyDependencies.isEmpty()) {
                        freed.add(key);
                    }
                }
            });
        }
        return freed;
    }

    public boolean containsKey(K key) {
        return this.k2v.containsKey(key);
    }

    public boolean isFree(K key) {
        Set<V> dependencies = this.k2v.get(key);
        if (dependencies == null) {
            throw new IllegalArgumentException("Key is not known to this dependency map, so I dont know if it is free: " + key);
        }
        return dependencies.isEmpty();
    }

    private boolean k2vAdd(K key, Set<V> dependencies) {
        Set<V> existing = this.k2v.get(key);
        if (existing == null) {
            Set<V> copy = new HashSet<>(dependencies);
            this.k2v.put(key, copy);
            return true;
        } else {
            return false;
        }
    }

    private void v2kAdd(V dependent, K key) {
        Set<K> existing = this.v2k.get(dependent);
        if (existing != null) {
            existing.add(key);
        } else {
            Set<K> keys = new HashSet<>();
            keys.add(key);
            this.v2k.put(dependent, keys);
        }
    }
}
