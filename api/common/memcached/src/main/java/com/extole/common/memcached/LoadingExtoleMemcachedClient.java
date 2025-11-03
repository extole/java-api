package com.extole.common.memcached;

import java.util.Optional;

import com.extole.common.memcached.ExtoleMemcachedClient.ValueProvider;
import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;

// TODO should extend ExtoleMemcachedClient, see ENG-18245
public interface LoadingExtoleMemcachedClient<K, V> {

    default Optional<V> get(K key) throws InterruptedException {
        return get(key, value -> true);
    }

    Optional<V> get(K key, ValueValidationClosure<V> validationClosure) throws InterruptedException;

    Optional<ValueWithVersion<V>> getWithVersion(K key) throws InterruptedException, ExtoleMemcachedException;

    Optional<V> getAndOptionallySet(K key, ValueProvider<V> newValueProvider)
        throws ExtoleMemcachedException, InterruptedException;

    boolean add(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void set(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void setWithCasVersion(K key, V value, ValueWithVersion<V> previousValueWithVersion)
        throws ExtoleMemcachedException, InterruptedException, OutdatedCasVersionException;

    @FunctionalInterface
    interface ValueValidationClosure<V> {
        boolean isValid(V value);
    }
}
