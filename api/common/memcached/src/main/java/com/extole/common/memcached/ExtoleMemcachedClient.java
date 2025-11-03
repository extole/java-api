package com.extole.common.memcached;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public interface ExtoleMemcachedClient<K, V> {

    String getName();

    Optional<V> get(K key) throws ExtoleMemcachedException, InterruptedException;

    Map<K, V> get(Collection<K> keys) throws ExtoleMemcachedException, InterruptedException;

    Optional<ValueWithVersion<V>> getWithVersion(K key) throws ExtoleMemcachedException, InterruptedException;

    Optional<V> getAndOptionallySet(K key, ValueProvider<V> newValueProvider)
        throws ExtoleMemcachedException, InterruptedException;

    boolean add(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void addWithNoReply(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void set(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void set(K key, V value, MemcachedExpiration expiration) throws ExtoleMemcachedException, InterruptedException;

    void setWithNoReply(K key, V value) throws ExtoleMemcachedException, InterruptedException;

    void setWithCasVersion(K key, V valueToSet, ValueWithVersion<V> previousValueWithVersion)
        throws ExtoleMemcachedException, InterruptedException, OutdatedCasVersionException;

    boolean delete(K memcachedKey) throws ExtoleMemcachedException, InterruptedException;

    boolean touch(K key) throws ExtoleMemcachedException, InterruptedException;

    Long increment(K key, long by, long initialValue, MemcachedExpiration expiration)
        throws ExtoleMemcachedException, InterruptedException, TimeoutException;

    Long decrement(K key, long by, long initialValue, MemcachedExpiration expiration)
        throws ExtoleMemcachedException, InterruptedException, TimeoutException;

    interface ValueWithVersion<T> {
        T getValue();

        long getCasVersion();

        String getClientName();
    }

    interface ValueProvider<T> {
        /**
         * IMPORTANT: The {@link ValueProvider} can be invoked multiple times due to memcached retries that might occur,
         * hence make sure that repeated calls to the value provider have no other consequences.
         *
         * @return Optional.empty() results in a no-op (existing value from memcached will be returned)
         */
        Optional<T> getValueToSet(Optional<T> currentValue);
    }
}
