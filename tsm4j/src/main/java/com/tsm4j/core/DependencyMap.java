package com.tsm4j.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*
 * Manage dependency of type K and its set of dependencies of type D
 * */
public class DependencyMap<K, D> {
    private final Map<K, Set<D>> k2d;  // unmodifiable
    private final Map<D, Set<K>> d2k;  // unmodifiable
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
        Map<D, Set<K>> revMap = new HashMap<>();
        k2d.forEach((key, value) -> value.forEach(dep -> revMap.computeIfAbsent(dep, k -> new HashSet<>()).add(key)));
        return Collections.unmodifiableMap(revMap);
    }

    private static <D, K> Map<K, LinkedList<Set<D>>> initDependenciesMap(Map<K, Set<D>> k2d) {
        Map<K, LinkedList<Set<D>>> depMap = new HashMap<>();
        k2d.forEach((key, value) -> depMap.put(key, new LinkedList<>()));
        return Collections.unmodifiableMap(depMap);
    }

    public Set<K> satisfy(D dep) {
        Set<K> freed = new HashSet<>();  // the set of freed keys after removing this dependency

        // we loop through the keys that depends on this dependency
        Set<K> affectedKeys = this.d2k.get(dep);
        if (affectedKeys != null) {
            affectedKeys.forEach(affectedKey -> {
                // here loop through each dependency, and see if we can release the key

                // if no existing dependency set for the key contains dependency, we will create a new set of dependency for it to remove
                boolean notRemoved = true;

                LinkedList<Set<D>> depsList = this.k2ds.get(affectedKey);

                // TODO: remove this loop by keeping track of the index of each dependency
                // NOTE: above will result in using arraylist instead of linked list thus potentially extra space
                for (Set<D> deps : depsList) {
                    if (deps.remove(dep)) {
                        notRemoved = false;
                        if (deps.isEmpty()) {
                            freed.add(affectedKey);
                            // if its empty, it has to be the first one, so we remove it
                            depsList.removeFirst();
                        }
                        break;
                    }
                }

                if (notRemoved) {
                    // the dependency is referring a fresh set of dependencies
                    Set<D> newDeps = new HashSet<>(this.k2d.get(affectedKey));
                    newDeps.remove(dep);
                    if (newDeps.isEmpty()) {
                        // special case for singleton dependency
                        freed.add(affectedKey);
                    } else {
                        depsList.add(newDeps);
                    }
                }
            });
        }
        return freed;
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
