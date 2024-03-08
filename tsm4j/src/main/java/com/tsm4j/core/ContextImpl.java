package com.tsm4j.core;

import com.tsm4j.core.exception.CacheNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
class ContextImpl implements Context {
    private final LocalDateTime startTime = LocalDateTime.now();
    @Getter
    private final StateMachineId id;

    private final Map<CacheKey, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String name, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return Optional.ofNullable(cache.get(new CacheKey(name, clazz))).map(obj -> (T) obj);  // this cast is safe, enforced by set method
    }

    public <T> Optional<T> get(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return get(clazz.getSimpleName(), clazz);
    }

    public <T> T getOrError(String name, Class<T> clazz) {
        return get(name, clazz).orElseThrow(() -> new CacheNotFoundException("name=" + name + ", class=" + clazz));
    }

    public <T> T getOrError(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return getOrError(clazz.getSimpleName(), clazz);
    }

    public <T> T getOrDefault(String name, Class<T> clazz, Supplier<T> defaultSupplier) {
        return get(name, clazz).orElseGet(defaultSupplier);
    }

    public <T> T getOrDefault(Class<T> clazz, Supplier<T> defaultSupplier) {
        Objects.requireNonNull(clazz);
        return getOrDefault(clazz.getSimpleName(), clazz, defaultSupplier);
    }

    public <T> void set(String name, Class<? extends T> clazz, T obj) {
        Objects.requireNonNull(clazz);
        this.cache.put(new CacheKey(name, clazz), obj);
    }

    public <T> void set(Class<? extends T> clazz, T obj) {
        Objects.requireNonNull(clazz);
        set(clazz.getSimpleName(), clazz, obj);
    }

    public <T> void set(String name, T obj) {
        Objects.requireNonNull(obj);
        set(name, obj.getClass(), obj);
    }

    public void set(Object obj) {
        Objects.requireNonNull(obj);
        set(obj.getClass(), obj);
    }


    @RequiredArgsConstructor
    @Value
    private static class CacheKey {
        String name;
        Class<?> clazz;
    }
}
