package com.tsm4j.core.map;

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

    // intendMap tell the intended amount of K that the user want to use
    // this keep track of maximum amount of K we can return to the user when he keeps removing dependency without adding new intent
    private final Map<K, Integer> intendMap;

    // each element in the linked list is a set of dependencies.
    // when user remove a dependency, he is maybe removing a dependency that he has not declared an intent to use yet
    private final Map<K, LinkedList<Set<D>>> k2ds;

    public DependencyMap(Map<K, Set<D>> dependencies) {
        this.k2d = new HashMap<>(dependencies);
        this.d2k = initReverseMap(dependencies);
        this.intendMap = initIntendMap(dependencies);
        this.k2ds = initDependenciesMap(dependencies);
    }

    public static <K, D> Builder<K, D> builder() {
        return new Builder<>();
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

    private static <D, K> Map<D, Set<K>> initReverseMap(Map<K, Set<D>> k2d) {
        Map<D, Set<K>> reverseMap = new HashMap<>();
        k2d.forEach((key, value) -> value.forEach(dep -> reverseMap.computeIfAbsent(dep, k -> new HashSet<>()).add(key)));
        return reverseMap;
    }

    private static <D, K> Map<K, Integer> initIntendMap(Map<K, Set<D>> k2d) {
        Map<K, Integer> intendMap = new HashMap<>();
        k2d.forEach((key, value) -> intendMap.put(key, 0));
        return intendMap;
    }

    private static <D, K> Map<K, LinkedList<Set<D>>> initDependenciesMap(Map<K, Set<D>> k2d) {
        Map<K, LinkedList<Set<D>>> depMap = new HashMap<>();
        k2d.forEach((key, value) -> depMap.put(key, new LinkedList<>()));
        return depMap;
    }

    // return false if the key has all dependencies satisfied, and not added to some pending state to be returned later
    public boolean addIntent(K key) {
        // user is looking to use this key

        // if this key has no dependencies, then return false to tell user just use it
        Set<D> dependencies = this.k2d.get(key);
        if (dependencies == null || dependencies.isEmpty()) {
            return false;
        }

        // get the dependencies list of this key
        LinkedList<Set<D>> pendingDeps = this.k2ds.get(key);

        // if the next set of dependencies of the key are satisfied, tell the user to just use it
        if (headIsEmpty(pendingDeps)) {
            // we are going to consume a satisfied intent
            // remove the empty dependencies and decrement the overall intent
            pendingDeps.removeFirst();
            // note getIntends(key) must be zero at this point
            // otherwise the empty dependency would have returned when its last dependency has been removed in the other method.
            return false;
        } else {
            // else
            // either pending dependencies list is empty
            // or the latest dependencies are not satisfied

            // we want to increment the intent and add a fresh set of dependencies

            // we only add a fresh set of dependencies if we do not have enough set of dependencies
            // the intent count is always more than or equal to the pending dependencies list size,
            // so we only need to increment if the pending dependencies list are equal to the existing intent count

            // the pending dependencies list is more than the intent when
            // we are removing a dependency from the key that the user does not intent to use yet.
            if (pendingDeps.size() == getIntends(key)) {
                pendingDeps.add(new HashSet<>(this.k2d.get(key)));
            }
            incrementIntent(key);
            return true;
        }
    }

    public Set<K> satisfy(D dependency) {
        Set<K> free = new HashSet<>();  // the set of freed keys after removing this dependency

        // we loop through the keys that depends on this dependency
        this.d2k.get(dependency).forEach(dependingKey -> {
            // here loop through each dependency, removing it from existing intent
            // if the user has not declared an intent to use this key, then we need to remove a future dependency
            // that is, user called satisfy before adding an intent
            boolean removeFutureDependency = true;

            LinkedList<Set<D>> depsList = this.k2ds.get(dependingKey);
            for (Set<D> deps : depsList) {
                if (deps.remove(dependency)) {
                    if (deps.isEmpty()) {
                        // free this key
                        free.add(dependingKey);
                        if (getIntends(dependingKey) > 0) {
                            decrementIntent(dependingKey);
                            depsList.removeFirst();
                        }
                    }
                    removeFutureDependency = false;
                    break;
                }
            }
            if (removeFutureDependency) {
                // we are removing a future dependency
                Set<D> newDependencies = new HashSet<>(this.k2d.get(dependingKey));
                newDependencies.remove(dependency);
                depsList.add(newDependencies);
            }
        });
        return free;
    }

    public boolean headIsEmpty(LinkedList<Set<D>> dependencies) {
        Set<D> head = dependencies.peek();
        return head != null && head.isEmpty();
    }

    public Set<D> getDependencies(K key) {
        return this.k2d.get(key);
    }

    public boolean containsKey(K key) {
        return this.k2d.containsKey(key);
    }

    private int getIntends(K key) {
        return this.intendMap.get(key);
    }

    private void incrementIntent(K key) {
        this.intendMap.computeIfPresent(key, (k, v) -> v + 1);
    }

    private void decrementIntent(K key) {
        this.intendMap.computeIfPresent(key, (k, v) -> v - 1);
    }
}
