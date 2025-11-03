package com.extole.common.memcached.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedClient.ValueProvider;
import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;
import com.extole.common.memcached.ExtoleMemcachedException;
import com.extole.common.memcached.ExtoleMemcachedLoader;
import com.extole.common.memcached.LoadingExtoleMemcachedClient;
import com.extole.common.memcached.MalformedMemcachedKeyException;
import com.extole.common.memcached.OutdatedCasVersionException;

public class MigrationLoadingExtoleMemcachedClient<K, V> implements LoadingExtoleMemcachedClient<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(MigrationLoadingExtoleMemcachedClient.class);

    private final String storeName;
    private final ExtoleMemcachedLoader<K, Optional<V>> loader;
    private final ExtoleMemcachedClientImpl<?, K, V> oldClient;
    private final ExtoleMemcachedClientImpl<?, K, V> newClient;

    public MigrationLoadingExtoleMemcachedClient(String storeName, ExtoleMemcachedLoader<K, Optional<V>> loader,
        ExtoleMemcachedClientImpl<?, K, V> oldClient, ExtoleMemcachedClientImpl<?, K, V> newClient) {
        this.storeName = storeName;
        this.loader = loader;
        this.oldClient = oldClient;
        this.newClient = newClient;
    }

    @Override
    public Optional<ValueWithVersion<V>> getWithVersion(K key) throws InterruptedException, ExtoleMemcachedException {
        Optional<ValueWithVersion<V>> valueWithVersion = newClient.getWithVersion(key);
        if (valueWithVersion.isEmpty()) {
            valueWithVersion = oldClient.getWithVersion(key);
            if (valueWithVersion.isEmpty()) {
                try {
                    Optional<V> value = loader.load(key);
                    if (value.isPresent()) {
                        newClient.add(key, value.get());
                        valueWithVersion = newClient.getWithVersion(key);
                    }
                } catch (ExtoleMemcachedException e) {
                    LOG.error("Failed to write key {} on read for store {}", key, storeName, e);
                }
            }
        }
        return valueWithVersion;
    }

    @Override
    public Optional<V> get(K key, ValueValidationClosure<V> validationClosure) throws InterruptedException {
        try {
            Optional<ValueWithVersion<V>> valueWithVersion = newClient.getWithVersion(key);
            if (valueWithVersion.isPresent()) {
                if (validationClosure.isValid(valueWithVersion.get().getValue())) {
                    return Optional.of(valueWithVersion.get().getValue());
                }
                return get(newClient, key, validationClosure);
            }

            valueWithVersion = oldClient.getWithVersion(key);
            if (valueWithVersion.isPresent() && validationClosure.isValid(valueWithVersion.get().getValue())) {
                return Optional.of(valueWithVersion.get().getValue());
            }
            return get(newClient, key, validationClosure);
        } catch (MalformedMemcachedKeyException e) {
            LOG.warn("Attempted to read malformed key {} for store {}", key, storeName);
            return Optional.empty();
        } catch (ExtoleMemcachedException e) {
            LOG.error("Failed to read key {} for store {}, falling back to loader", key, storeName, e);
            return loader.load(key);
        }
    }

    @Override
    public boolean add(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        return newClient.add(key, value);
    }

    @Override
    public void setWithCasVersion(K key, V value, ValueWithVersion<V> previousValueWithVersion)
        throws ExtoleMemcachedException, InterruptedException, OutdatedCasVersionException {
        if (previousValueWithVersion.getClientName().equals(oldClient.getName())) {
            oldClient.setWithCasVersion(key, value, previousValueWithVersion);
            boolean success = newClient.add(key, value);
            if (!success) {
                throw new OutdatedCasVersionException(
                    "Unable to add key: " + key + " to new client: " + newClient.getName()
                        + " after successfully setting with cas with the old client: " + oldClient.getName());
            }
        } else if (previousValueWithVersion.getClientName().equals(newClient.getName())) {
            newClient.setWithCasVersion(key, value, previousValueWithVersion);
        } else {
            throw new IllegalArgumentException(
                "Value has unsupported client name: " + previousValueWithVersion.getClientName());
        }
    }

    @Override
    public void set(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        newClient.set(key, value);
        oldClient.set(key, value);
    }

    @Override
    public Optional<V> getAndOptionallySet(K key, ValueProvider<V> newValueProvider)
        throws ExtoleMemcachedException, InterruptedException {
        // This implementation is designed to work for stores that are using this method for saveExisting functionality
        Optional<V> value = newClient.get(key);
        if (value.isPresent()) {
            return newClient.getAndOptionallySet(key, newValueProvider);
        }
        value = oldClient.get(key);
        if (value.isPresent()) {
            newClient.add(key, value.get());
        }
        return newClient.getAndOptionallySet(key, newValueProvider);
    }

    private Optional<V> get(ExtoleMemcachedClient<K, V> client, K key, ValueValidationClosure<V> validationClosure)
        throws InterruptedException {
        try {
            return client.getAndOptionallySet(key, currentValue -> {
                if (currentValue.isPresent() && validationClosure.isValid(currentValue.get())) {
                    return Optional.empty();
                }
                return loader.load(key);
            });
        } catch (MalformedMemcachedKeyException e) {
            LOG.warn("Attempted to read malformed key {} for store {}", key, storeName);
            return Optional.empty();
        } catch (ExtoleMemcachedException e) {
            LOG.error("Failed to read key {} for store {}, falling back to loader", key, storeName, e);
            return loader.load(key);
        }
    }
}
