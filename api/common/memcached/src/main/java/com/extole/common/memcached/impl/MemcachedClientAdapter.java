package com.extole.common.memcached.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;
import com.extole.common.memcached.MemcachedExpiration;

interface MemcachedClientAdapter<T> {

    @Nullable
    T get(String key) throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    @Nullable
    Map<String, T> get(Collection<String> keys)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    @Nullable
    ValueWithVersion<T> getWithVersion(String key)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    boolean set(String key, T value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    void setWithNoReply(String key, T value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException;

    boolean add(String key, T value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    void addWithNoReply(String key, T value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException;

    boolean delete(String key) throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    boolean touch(String key, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    boolean cas(String key, T value, long cas, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    Long increment(String key, long by, long initialValue, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

    Long decrement(String key, long by, long initialValue, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException;

}
