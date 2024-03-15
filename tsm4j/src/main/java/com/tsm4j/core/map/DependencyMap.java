package com.tsm4j.core.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Manage dependency of type K and its set of dependencies of type D
 * */
public class DependencyMap<K, D> {
    private final Map<K, Set<D>> k2d;
    private final Map<D, Set<K>> d2k;
    private final Set<D> freedDependencies;

    public DependencyMap() {
        this.k2d = new HashMap<>();
        this.d2k = new HashMap<>();
        this.freedDependencies = new HashSet<>();
    }

    // return false if the key is already configured with some dependencies before
    public boolean addDependencies(K key, Set<D> dependencies) {
        if (k2dAdd(key, dependencies)) {
            dependencies.forEach(dep -> d2kAdd(dep, key));
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
                    // should not reach here, because it should be impossible for some affected keys have no dependencies
                    freedKeys.add(key);
                } else {
                    keyDependencies.remove(dependency);
                    if (keyDependencies.isEmpty()) {
                        freedKeys.add(key);
                        // we do not remove the key from k2d if it is freed
                        // because we want to differentiate keys that are configured with dependencies and keys that are not
                        // we do not allow adding new dependencies to the same key twice
                    }
                }
            });
        }  // else: unknown dependency given
        freedDependencies.add(dependency);
        return freedKeys;
    }

    public Set<K> removeDependencies(Set<D> dependencies) {
        Set<K> freedKeys = new HashSet<>();
        dependencies.forEach(d -> freedKeys.addAll(this.removeDependency(d)));
        return freedKeys;
    }

    public Set<D> getDependencies(K key) {
        return this.k2d.get(key);
    }

    public boolean containsKey(K key) {
        return this.k2d.containsKey(key);
    }

    public boolean isFree(K key) {
        Set<D> dependencies = getDependencies(key);
        return dependencies == null || dependencies.isEmpty();
        // note if a key is not seen, i.e. no dependencies is added before, we return true as well
    }

    // return false if dependencies is already added for this key
    private boolean k2dAdd(K key, Set<D> dependencies) {
        Set<D> existing = this.k2d.get(key);
        if (existing == null) {
            // new key and dependencies
            Set<D> toAdd = removeFreed(new HashSet<>(dependencies));
            this.k2d.put(key, toAdd);
            return true;
        } else {
            // dependencies can only be added once, so we ignore further given dependencies
            return false;
        }
    }

    private void d2kAdd(D dependent, K key) {
        Set<K> existing = this.d2k.get(dependent);
        if (existing != null) {
            existing.add(key);
        } else {
            Set<K> keys = new HashSet<>();
            keys.add(key);
            this.d2k.put(dependent, keys);
        }
    }

    private Set<D> removeFreed(Set<D> dependencies) {
        Set<D> notFreed = new HashSet<>();
        dependencies.forEach(d -> {
            if (!freedDependencies.contains(d)) {
                notFreed.add(d);
            }
        });
        return notFreed;
    }
}
