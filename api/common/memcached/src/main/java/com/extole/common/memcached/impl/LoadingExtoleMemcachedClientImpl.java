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

class LoadingExtoleMemcachedClientImpl<K, V> implements LoadingExtoleMemcachedClient<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(LoadingExtoleMemcachedClientImpl.class);

    private final String storeName;
    private final ExtoleMemcachedClient<K, V> memcachedClient;
    private final ExtoleMemcachedLoader<K, Optional<V>> loader;

    LoadingExtoleMemcachedClientImpl(String storeName,
        ExtoleMemcachedClient<K, V> memcachedClient,
        ExtoleMemcachedLoader<K, Optional<V>> loader) {
        this.storeName = storeName;
        this.memcachedClient = memcachedClient;
        this.loader = loader;
    }

    @Override
    public Optional<ValueWithVersion<V>> getWithVersion(K key) throws InterruptedException, ExtoleMemcachedException {
        Optional<ValueWithVersion<V>> valueWithVersion = memcachedClient.getWithVersion(key);
        if (valueWithVersion.isEmpty()) {
            try {
                Optional<V> value = loader.load(key);
                if (value.isPresent()) {
                    memcachedClient.add(key, value.get());
                    valueWithVersion = memcachedClient.getWithVersion(key);
                }
            } catch (ExtoleMemcachedException e) {
                LOG.error("Failed to write key {} on read for store {}", key, storeName, e);
            }
        }
        return valueWithVersion;
    }

    @Override
    public Optional<V> get(K key, ValueValidationClosure<V> validationClosure) throws InterruptedException {
        try {
            Optional<V> value = memcachedClient.getAndOptionallySet(key, currentValue -> {
                if (currentValue.isPresent() && validationClosure.isValid(currentValue.get())) {
                    return Optional.empty();
                }
                return loader.load(key);
            });
            return value;
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
        return memcachedClient.add(key, value);
    }

    @Override
    public void set(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        memcachedClient.set(key, value);
    }

    @Override
    public Optional<V> getAndOptionallySet(K key, ValueProvider<V> newValueProvider)
        throws ExtoleMemcachedException, InterruptedException {
        return memcachedClient.getAndOptionallySet(key, newValueProvider);
    }

    @Override
    public void setWithCasVersion(K key, V value, ValueWithVersion<V> previousValueWithVersion)
        throws ExtoleMemcachedException, InterruptedException, OutdatedCasVersionException {
        memcachedClient.setWithCasVersion(key, value, previousValueWithVersion);
    }
}
