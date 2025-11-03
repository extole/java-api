package com.extole.common.memcached;

import java.util.Optional;

/**
 * @param <S> - type used for serializing the value in memcached (e.g. String, byte[], etc)
 * @param <T> - high-level type representing the value
 */
public interface MemcachedTransformer<S, T> {
    S encode(MemcachedKey key, T value) throws MemcachedTransformerException;

    Optional<T> decode(MemcachedKey key, S serializedValue) throws MemcachedTransformerException;
}
