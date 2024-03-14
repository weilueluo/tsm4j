package com.tsm4j.core.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyMap<K, D> {
    private final Map<K, Set<D>> k2d;
    private final Map<D, Set<K>> d2k;
    private final Set<D> freedDependencies;

    public DependencyMap() {
        this.k2d = new HashMap<>();
        this.d2k = new HashMap<>();
        this.freedDependencies = new HashSet<>();
    }

    // return false if the key is already satisfied
    public boolean addDependencies(K key, Set<D> dependencies) {
        if (k2vAdd(key, dependencies)) {
            dependencies.forEach(d -> v2kAdd(d, key));
            return true;
        } else {
            return false;
        }
    }

    public Set<K> removeDependency(D dependency) {
        Set<K> freedKeys = new HashSet<>();
        Set<K> affectedKeys = this.d2k.remove(dependency);
        if (affectedKeys != null) {
            affectedKeys.forEach(key -> {
                Set<D> keyDependencies = this.k2d.get(key);
                if (keyDependencies == null) {
                    // should not reach here, because it is impossible for some affected keys have no dependencies
                    freedKeys.add(key);
                } else {
                    keyDependencies.remove(dependency);
                    if (keyDependencies.isEmpty()) {
                        // we do not remove the key if it is freed
                        // because we want to differentiate keys that are satisfied and keys that are not seen
                        freedKeys.add(key);
                    }
                }
            });
        }  // else: unknown dependency given
        freedDependencies.add(dependency);
        return freedKeys;
    }

    public boolean containsKey(K key) {
        return this.k2d.containsKey(key);
    }

    public boolean isFree(K key) {
        Set<D> dependencies = this.k2d.get(key);
        return dependencies == null || dependencies.isEmpty();
    }

    // return false if key is already satisfied
    private boolean k2vAdd(K key, Set<D> dependencies) {
        Set<D> copy = new HashSet<>(dependencies);
        Set<D> existing = this.k2d.get(key);
        if (existing == null) {
            // new key and dependencies
            freedDependencies.forEach(copy::remove);  // remove dependencies that already freed
            this.k2d.put(key, copy);
            return !copy.isEmpty();  // key is satisfied if it has empty dependencies
        } else if (existing.isEmpty()) {
            // key is already freed, we ignore given dependencies
            return false;
        } else {
            // key is not freed and we got some additional some dependencies
            freedDependencies.forEach(copy::remove);  // remove dependencies that already freed
            existing.addAll(copy);
            return true;
        }
    }

    private void v2kAdd(D dependent, K key) {
        Set<K> existing = this.d2k.get(dependent);
        if (existing != null) {
            existing.add(key);
        } else {
            Set<K> keys = new HashSet<>();
            keys.add(key);
            this.d2k.put(dependent, keys);
        }
    }
}
