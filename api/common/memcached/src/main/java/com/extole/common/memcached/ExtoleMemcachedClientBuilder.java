package com.extole.common.memcached;

import java.util.Optional;

public interface ExtoleMemcachedClientBuilder {

    <S, K, V> ExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer);

    <S, K, V> ExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        MemcachedExpiration expiration);

    <S, K, V> LoadingExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        ExtoleMemcachedLoader<K, Optional<V>> loader);

    <S, K, V> LoadingExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        ExtoleMemcachedLoader<K, Optional<V>> loader, MemcachedExpiration expiration);

    ExtoleMemcachedClientBuilder initialize();

    ExtoleMemcachedClientBuilder withServers(String memcachedServers);

    ExtoleMemcachedClientBuilder withServerType(MemcachedServer serverType);

    ExtoleMemcachedClientBuilder withProtocol(MemcachedProtocol protocol);

    ExtoleMemcachedClientBuilder withItemMaxSize(int itemMaxSize);

    ExtoleMemcachedClientBuilder withExpiration(MemcachedExpiration expiration);

    ExtoleMemcachedClientBuilder withOperationTimeoutMs(long operationTimeoutMs);

    ExtoleMemcachedClientBuilder withCasOperationTimeoutMs(long casOperationTimeoutMs);

    ExtoleMemcachedClientBuilder withRetryCount(int retryCount);

    ExtoleMemcachedClientBuilder withRetryBackoffMs(long retryBackoffMs);

    ExtoleMemcachedClientBuilder withClientPoolSize(int clientPoolSize);

    ExtoleMemcachedClientBuilder withCompressionThreshold(int compressionThreshold);

    ExtoleMemcachedClientBuilder withClientName(String clientName);
}
