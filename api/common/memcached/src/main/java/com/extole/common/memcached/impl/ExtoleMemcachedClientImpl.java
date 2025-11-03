package com.extole.common.memcached.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedException;
import com.extole.common.memcached.MemcachedExpiration;
import com.extole.common.memcached.MemcachedKey;
import com.extole.common.memcached.MemcachedMetric;
import com.extole.common.memcached.MemcachedTransformer;
import com.extole.common.memcached.MemcachedTransformerException;
import com.extole.common.memcached.OutdatedCasVersionException;
import com.extole.common.metrics.ExtoleMetricRegistry;

public class ExtoleMemcachedClientImpl<S, K, V> implements ExtoleMemcachedClient<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleMemcachedClientImpl.class);

    private final String storeName;
    private final MemcachedClientAdapter<S> memcachedClient;
    private final MemcachedTransformer<S, V> transformer;
    private final ExtoleMetricRegistry metricRegistry;
    private final MemcachedExpiration defaultExpiration;
    private final int maxAllowedAttempts;
    private final long retryBackoffMs;
    private final String clientName;

    ExtoleMemcachedClientImpl(String storeName, MemcachedClientAdapter<S> memcachedClient,
        MemcachedTransformer<S, V> transformer, ExtoleMetricRegistry metricRegistry,
        MemcachedExpiration defaultExpiration, int retryCount, long retryBackoffMs, String clientName) {
        this.storeName = storeName;
        this.memcachedClient = memcachedClient;
        this.transformer = transformer;
        this.metricRegistry = metricRegistry;
        this.defaultExpiration = defaultExpiration;
        this.maxAllowedAttempts = retryCount + 1;
        this.retryBackoffMs = retryBackoffMs;
        this.clientName = clientName;
    }

    @Override
    public String getName() {
        return clientName;
    }

    @Override
    public Optional<V> get(K key) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("get for key {} from store {}, attempt {}", key, storeName, attempt);
                try {
                    S serializedValue = memcachedClient.get(cacheKey.getValue());
                    LOG.trace("Key {} retrieved value {} from store {}", key, serializedValue, storeName);

                    Optional<V> value;
                    if (serializedValue == null) {
                        metricRegistry.histogram(nameOf(MemcachedMetric.READ_MISS_DURATION)).update(startTime,
                            Instant.now());
                        value = Optional.empty();
                    } else {
                        value = transformer.decode(cacheKey, serializedValue);
                        metricRegistry.histogram(nameOf(MemcachedMetric.READ_DURATION)).update(startTime,
                            Instant.now());
                    }
                    return value;
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.READ_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to get value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.READ_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.READ_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error getting value for key " + key + " from store " + storeName,
            exceptions);
    }

    @Override
    public Map<K, V> get(Collection<K> keys) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        List<Exception> exceptions = new ArrayList<>();
        Map<String, Pair<K, MemcachedKey>> keysByValue = new HashMap<>(keys.size());
        for (K key : keys) {
            MemcachedKey cacheKey = new MemcachedKey(key.toString());
            keysByValue.put(cacheKey.getValue(), Pair.of(key, cacheKey));
        }
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("bulk get for keys {} from store {}, attempt {}", keys, storeName, attempt);
                try {
                    Map<String, S> serializedValues = memcachedClient.get(keysByValue.keySet());
                    LOG.trace("Keys {} retrieved values {} from store {}", keys, serializedValues, storeName);

                    Map<K, V> result = new HashMap<>();
                    if (serializedValues != null && !serializedValues.isEmpty()) {
                        for (Entry<String, S> entry : serializedValues.entrySet()) {
                            Pair<K, MemcachedKey> key = keysByValue.get(entry.getKey());
                            Optional<V> value = transformer.decode(key.getRight(), entry.getValue());
                            value.ifPresent(v -> result.put(key.getLeft(), v));
                        }
                        metricRegistry.histogram(nameOf(MemcachedMetric.BULK_GET_DURATION)).update(startTime,
                            Instant.now());
                    } else {
                        metricRegistry.histogram(nameOf(MemcachedMetric.BULK_GET_MISS_DURATION)).update(startTime,
                            Instant.now());
                    }
                    return result;
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.BULK_GET_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to bulk get values for keys {} failed in store {}", attempt, keys, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.BULK_GET_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.BULK_GET_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error bulk reading values for keys " + keys + " from store " + storeName,
            exceptions);
    }

    @Override
    public Optional<ValueWithVersion<V>> getWithVersion(K key) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("gets for key {} from store {}, attempt {}", key, storeName, attempt);
                try {
                    return getWithVersionNoRetry(key, Optional.of(startTime));
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.GETS_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to gets value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.GETS_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.GETS_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error for gets for key " + key + " from store " + storeName, exceptions);
    }

    private Optional<ValueWithVersion<V>> getWithVersionNoRetry(K key, Optional<Instant> startTime)
        throws ExtoleMemcachedException, MemcachedClientAdapterException, MemcachedTransformerException,
        TimeoutException, InterruptedException {
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        ValueWithVersion<S> serializedValue = memcachedClient.getWithVersion(cacheKey.getValue());
        LOG.trace("Key {} retrieved value {} from store {}", key, serializedValue, storeName);
        if (serializedValue == null) {
            if (startTime.isPresent()) {
                metricRegistry.histogram(nameOf(MemcachedMetric.GETS_MISS_DURATION)).update(startTime.get(),
                    Instant.now());
            }
            return Optional.empty();
        }
        Optional<V> value = transformer.decode(cacheKey, serializedValue.getValue());
        if (startTime.isPresent()) {
            metricRegistry.histogram(nameOf(MemcachedMetric.GETS_DURATION)).update(startTime.get(), Instant.now());
        }
        return value
            .map(v -> new ValueWithVersionImpl<>(v, serializedValue.getCasVersion(), serializedValue.getClientName()));
    }

    @Override
    public void set(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        set(key, value, defaultExpiration);
    }

    @Override
    public void set(K key, V value, MemcachedExpiration expiration)
        throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            S serializedValue = transformer.encode(cacheKey, value);
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("set value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    boolean result = memcachedClient.set(cacheKey.getValue(), serializedValue, expiration);

                    LOG.trace("Setting value for key {} for store {} returned {}", key, storeName, result);
                    metricRegistry
                        .histogram(nameOf(result ? MemcachedMetric.SET_DURATION : MemcachedMetric.SET_MISS_DURATION))
                        .update(startTime, Instant.now());
                    if (result) {
                        return;
                    }
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.SET_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to set value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.SET_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.SET_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error setting value for key " + key + " to store " + storeName,
            exceptions);
    }

    @Override
    public void setWithNoReply(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            S serializedValue = transformer.encode(cacheKey, value);
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("set (noreply) value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    memcachedClient.setWithNoReply(cacheKey.getValue(), serializedValue, defaultExpiration);
                    metricRegistry.histogram(nameOf(MemcachedMetric.SET_DURATION)).update(startTime, Instant.now());
                    return;
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to set (noreply) value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.SET_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.SET_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error setting (noreply) value for key " + key + " to store " + storeName,
            exceptions);
    }

    @Override
    public boolean add(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            S serializedValue = transformer.encode(cacheKey, value);
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("add value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    boolean result = memcachedClient.add(cacheKey.getValue(), serializedValue, defaultExpiration);

                    LOG.trace("Adding value for key {} for store {} returned {}", key, storeName, result);
                    metricRegistry
                        .histogram(nameOf(result ? MemcachedMetric.ADD_DURATION : MemcachedMetric.ADD_MISS_DURATION))
                        .update(startTime, Instant.now());
                    return result;
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.ADD_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to add value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.ADD_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.ADD_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error adding value for key " + key + " to store " + storeName, exceptions);
    }

    @Override
    public void addWithNoReply(K key, V value) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            S serializedValue = transformer.encode(cacheKey, value);
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("add (noreply) value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    memcachedClient.addWithNoReply(cacheKey.getValue(), serializedValue, defaultExpiration);
                    metricRegistry.histogram(nameOf(MemcachedMetric.ADD_DURATION)).update(startTime, Instant.now());
                    return;
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to add (noreply) value for key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.ADD_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.ADD_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error adding (noreply) value for key " + key + " to store " + storeName,
            exceptions);
    }

    @Override
    public boolean delete(K key) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("delete key {} from store {}, attempt {}", key, storeName, attempt);
                try {
                    boolean result = memcachedClient.delete(cacheKey.getValue());

                    LOG.trace("Deleting value for key {} for store {} returned {}", key, storeName, result);
                    metricRegistry
                        .histogram(
                            nameOf(result ? MemcachedMetric.DELETE_DURATION : MemcachedMetric.DELETE_MISS_DURATION))
                        .update(startTime, Instant.now());
                    return result;
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.DELETE_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to delete value for key {} failed from store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.DELETE_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.DELETE_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error deleting key " + key + " from store " + storeName, exceptions);
    }

    @Override
    public boolean touch(K key) throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("touch key {} from store {}, attempt {}", key, storeName, attempt);
                try {
                    boolean result = memcachedClient.touch(cacheKey.getValue(), defaultExpiration);

                    LOG.trace("Touching value for key {} for store {} returned {}", key, storeName, result);
                    metricRegistry
                        .histogram(
                            nameOf(result ? MemcachedMetric.TOUCH_DURATION : MemcachedMetric.TOUCH_MISS_DURATION))
                        .update(startTime, Instant.now());
                    return result;
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.TOUCH_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to touch key {} failed in store {}", attempt, key, storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.TOUCH_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.TOUCH_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error touching key " + key + " from store " + storeName, exceptions);
    }

    @Override
    public void setWithCasVersion(K key, V valueToSet, ValueWithVersion<V> previousValueWithVersion)
        throws ExtoleMemcachedException, InterruptedException, OutdatedCasVersionException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            S serializedValue = transformer.encode(cacheKey, valueToSet);
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("setWithCasVersion value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    boolean result = memcachedClient.cas(cacheKey.getValue(), serializedValue,
                        previousValueWithVersion.getCasVersion(), defaultExpiration);

                    LOG.trace("CAS operation for key {} for store {} returned {}", key, storeName, result);
                    metricRegistry
                        .histogram(nameOf(result ? MemcachedMetric.CAS_DURATION : MemcachedMetric.CAS_MISS_DURATION))
                        .update(startTime, Instant.now());
                    if (result) {
                        return;
                    }
                    ValueWithVersion<S> currentValueWithVersion = memcachedClient.getWithVersion(cacheKey.getValue());
                    if (currentValueWithVersion == null) {
                        result = memcachedClient.add(cacheKey.getValue(), serializedValue, defaultExpiration);

                        LOG.trace("Add operation after failed CAS for key {} for store {} returned {}", key, storeName,
                            result);
                        metricRegistry
                            .histogram(
                                nameOf(result ? MemcachedMetric.CAS_DURATION : MemcachedMetric.CAS_MISS_DURATION))
                            .update(startTime, Instant.now());
                        if (result) {
                            return;
                        }
                    } else if (currentValueWithVersion.getCasVersion() != previousValueWithVersion.getCasVersion()) {
                        throw new OutdatedCasVersionException(String.format(
                            "Failed to setWithCasVersion key %s due to outdated casVersion (current %s, previous %s)",
                            key, currentValueWithVersion.getCasVersion(), previousValueWithVersion.getCasVersion()));
                    }
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.CAS_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }
            }

            LOG.debug("Attempt {} to setWithCasVersion value for key {} failed in store {}", attempt, key, storeName);

            sleepIfNeeded(attempt);
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.CAS_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.CAS_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error setWithCasVersion value for key " + key + " with CAS version "
            + previousValueWithVersion.getCasVersion() + " to store " + storeName, exceptions);
    }

    @Override
    public Optional<V> getAndOptionallySet(K key, ValueProvider<V> newValueProvider)
        throws ExtoleMemcachedException, InterruptedException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        List<Exception> exceptions = new ArrayList<>();
        int attempt = 0;
        try {
            while (attempt < maxAllowedAttempts) {
                attempt++;
                LOG.trace("getAndOptionallySet value for key {} in store {}, attempt {}", key, storeName, attempt);
                try {
                    Optional<ValueWithVersion<V>> valueWithVersion = getWithVersionNoRetry(key, Optional.empty());
                    Optional<V> value = valueWithVersion.map(ValueWithVersion::getValue);
                    V newValue = newValueProvider.getValueToSet(value).orElse(null);
                    if (newValue == null) {
                        LOG.trace("getAndOptionallySet for key {} for store {} returned existing value {}", key,
                            storeName, value);
                        metricRegistry
                            .histogram(
                                nameOf(value.isPresent() ? MemcachedMetric.GET_AND_SET_NOOP_DURATION
                                    : MemcachedMetric.GET_AND_SET_MISS_DURATION))
                            .update(startTime, Instant.now());
                        return value;
                    }

                    S serializedNewValue = transformer.encode(cacheKey, newValue);

                    boolean result;
                    if (valueWithVersion.isEmpty()) {
                        result = memcachedClient.add(cacheKey.getValue(), serializedNewValue, defaultExpiration);
                        LOG.trace("getAndOptionallySet add for key {} for store {} returned {}", key, storeName,
                            result);
                    } else {
                        result = memcachedClient.cas(cacheKey.getValue(), serializedNewValue,
                            valueWithVersion.get().getCasVersion(), defaultExpiration);
                        LOG.trace("getAndOptionallySet CAS for key {} for store {} returned {}", key, storeName,
                            result);
                    }

                    if (result) {
                        metricRegistry.histogram(nameOf(MemcachedMetric.GET_AND_SET_DURATION)).update(startTime,
                            Instant.now());
                        return Optional.of(newValue);
                    }
                } catch (TimeoutException e) {
                    metricRegistry.counter(nameOf(MemcachedMetric.GET_AND_SET_TIMEOUT_COUNTER)).increment();
                    exceptions.add(e);
                } catch (TransientMemcachedClientAdapterException e) {
                    exceptions.add(e);
                }

                LOG.debug("Attempt {} to getAndOptionallySet value for key {} failed in store {}", attempt, key,
                    storeName);

                sleepIfNeeded(attempt);
            }
        } catch (MemcachedTransformerException | MemcachedClientAdapterException e) {
            exceptions.add(e);
        } finally {
            metricRegistry.histogram(nameOf(MemcachedMetric.GET_AND_SET_ATTEMPTS)).update(attempt);
        }
        metricRegistry.histogram(nameOf(MemcachedMetric.GET_AND_SET_FAIL_DURATION)).update(startTime, Instant.now());
        throw newExtoleMemcachedException("Error getAndOptionallySet value for key " + key + " to store " + storeName,
            exceptions);
    }

    @Override
    public Long increment(K key, long by, long initialValue, MemcachedExpiration expiration)
        throws ExtoleMemcachedException, InterruptedException, TimeoutException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        try {
            LOG.trace("increment for key {} from store {}", key, storeName);
            Long value = memcachedClient.increment(cacheKey.getValue(), by, initialValue, expiration);
            LOG.trace("Received incremented value {} for key {} from store {}", value, key, storeName);
            metricRegistry.histogram(nameOf(MemcachedMetric.INCREMENT_DURATION)).update(startTime, Instant.now());
            return value;
        } catch (MemcachedClientAdapterException e) {
            metricRegistry.histogram(nameOf(MemcachedMetric.INCREMENT_FAIL_DURATION)).update(startTime, Instant.now());
            throw newExtoleMemcachedException("Error to increment value for key " + key + " from store " + storeName,
                Collections.singletonList(e));
        }
    }

    @Override
    public Long decrement(K key, long by, long initialValue, MemcachedExpiration expiration)
        throws ExtoleMemcachedException, InterruptedException, TimeoutException {
        Instant startTime = Instant.now();
        MemcachedKey cacheKey = new MemcachedKey(key.toString());
        try {
            LOG.trace("decrement for key {} from store {}", key, storeName);
            Long value = memcachedClient.decrement(cacheKey.getValue(), by, initialValue, expiration);
            LOG.trace("Received decremented value {} for key {} from store {}", value, key, storeName);
            metricRegistry.histogram(nameOf(MemcachedMetric.DECREMENT_DURATION)).update(startTime, Instant.now());
            return value;
        } catch (MemcachedClientAdapterException e) {
            metricRegistry.histogram(nameOf(MemcachedMetric.DECREMENT_FAIL_DURATION)).update(startTime, Instant.now());
            throw newExtoleMemcachedException("Error to decrement value for key " + key + " from store " + storeName,
                Collections.singletonList(e));
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[storeName=" + storeName + "]";
    }

    private void sleepIfNeeded(int attempt) throws InterruptedException {
        if (attempt < maxAllowedAttempts) {
            Thread.sleep(retryBackoffMs);
        }
    }

    private String nameOf(MemcachedMetric memcachedMetric) {
        return storeName + "." + memcachedMetric.getMetricName();
    }

    private static ExtoleMemcachedException newExtoleMemcachedException(String message, List<Exception> causes) {
        ExtoleMemcachedException extoleException;
        ListIterator<Exception> iterator = causes.listIterator(causes.size());
        if (iterator.hasPrevious()) {
            extoleException = new ExtoleMemcachedException(message, iterator.previous());
        } else {
            extoleException = new ExtoleMemcachedException(message);
        }
        while (iterator.hasPrevious()) {
            extoleException.addSuppressed(iterator.previous());
        }
        return extoleException;
    }
}
