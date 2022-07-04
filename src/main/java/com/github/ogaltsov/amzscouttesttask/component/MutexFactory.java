package com.github.ogaltsov.amzscouttesttask.component;

import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;

@Component
public class MutexFactory<T> {

    private final ConcurrentReferenceHashMap<T, Object> mutexPerKey;

    public MutexFactory() {
        this.mutexPerKey = new ConcurrentReferenceHashMap<>();
    }

    public Object getMutex(T key) {
        return this.mutexPerKey.compute(key, (k, v) -> v == null ? new Object() : v);
    }
}
