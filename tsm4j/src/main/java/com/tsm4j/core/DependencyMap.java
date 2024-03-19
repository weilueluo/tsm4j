package com.tsm4j.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*
 * Manage dependency of type K and its set of dependencies of type D
 * */
public class DependencyMap<K, D> {
    private final Map<K, Set<D>> k2d;
    private final Map<D, Set<K>> d2k;
    private final Map<K, LinkedList<Set<D>>> k2ds;

    public DependencyMap(Map<K, Set<D>> dependencies) {
        this.k2d = new HashMap<>(dependencies);
        this.d2k = initReverseMap(dependencies);
        this.k2ds = initDependenciesMap(dependencies);
    }

    public static <K, D> Builder<K, D> builder() {
        return new Builder<>();
    }

    private static <D, K> Map<D, Set<K>> initReverseMap(Map<K, Set<D>> k2d) {
        Map<D, Set<K>> reverseMap = new HashMap<>();
        k2d.forEach((key, value) -> value.forEach(dep -> reverseMap.computeIfAbsent(dep, k -> new HashSet<>()).add(key)));
        return reverseMap;
    }

    private static <D, K> Map<K, LinkedList<Set<D>>> initDependenciesMap(Map<K, Set<D>> k2d) {
        Map<K, LinkedList<Set<D>>> depMap = new HashMap<>();
        k2d.forEach((key, value) -> depMap.put(key, new LinkedList<>()));
        return depMap;
    }

    public Set<K> satisfy(D dependency) {
        Set<K> free = new HashSet<>();  // the set of freed keys after removing this dependency

        // we loop through the keys that depends on this dependency
        Set<K> affectedKeys = this.d2k.get(dependency);
        if (affectedKeys != null) {
            affectedKeys.forEach(dependingKey -> {
                // here loop through each dependency, and see if we can release the key
                boolean notRemoved = true;

                LinkedList<Set<D>> depsList = this.k2ds.get(dependingKey);
                for (Set<D> deps : depsList) {
                    if (deps.remove(dependency)) {
                        notRemoved = false;
                        if (deps.isEmpty()) {
                            free.add(dependingKey);
                            depsList.removeFirst();
                        }
                        break;
                    }
                }

                if (notRemoved) {
                    // the dependency is referring a fresh set of dependencies
                    Set<D> newDependencies = new HashSet<>(this.k2d.get(dependingKey));
                    newDependencies.remove(dependency);
                    if (newDependencies.isEmpty()) {
                        // special case for singleton dependency
                        free.add(dependingKey);
                    } else {
                        depsList.add(newDependencies);
                    }
                }
            });
        }
        return free;
    }

    public static class Builder<K, D> {
        private final Map<K, Set<D>> deps;

        private Builder() {
            this.deps = new HashMap<>();
        }

        public Builder<K, D> addDependencies(K key, Set<D> deps) {
            this.deps.computeIfAbsent(key, (k) -> new HashSet<>()).addAll(deps);
            return this;
        }

        public DependencyMap<K, D> build() {
            return new DependencyMap<>(this.deps);
        }
    }
}
