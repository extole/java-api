package com.extole.common.memcached.impl;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.memcached.ExtoleMemcachedClientBuilder;
import com.extole.common.memcached.ExtoleMemcachedFactory;
import com.extole.common.memcached.InvalidMemcachedExpirationException;
import com.extole.common.memcached.MemcachedExpiration;
import com.extole.common.memcached.MemcachedProtocol;
import com.extole.common.memcached.MemcachedServer;
import com.extole.spring.ServiceLocator;
import com.extole.spring.StartFirstStopLast;

@Component
class ExtoleMemcachedFactoryImpl implements ExtoleMemcachedFactory, StartFirstStopLast {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleMemcachedFactoryImpl.class);

    private final List<CloseableExtoleMemcachedClientBuilder> memcachedBuilders = new CopyOnWriteArrayList<>();
    private final String clientLibrary;
    private final String memcachedServers;
    private final MemcachedServer serverType;
    private final MemcachedProtocol protocol;
    private final MemcachedExpiration expiration;
    private final int itemMaxSize;
    private final int compressionThreshold;
    private final int clientPoolSize;
    private final long operationTimeoutMs;
    private final long casOperationTimeoutMs;
    private final int retryCount;
    private final long retryBackoffMs;
    private final ServiceLocator serviceLocator;

    @Autowired
    ExtoleMemcachedFactoryImpl(
        @Value("${memcached.servers:memcached-main.${extole.environment:lo}.intole.net:11211}") String memcachedServers,
        @Value("${memcached.client.library:folsom}") String clientLibrary,
        @Value("${memcached.serverType:elasticache}") String serverType,
        @Value("${memcached.protocol:text}") String protocol,
        @Value("${memcached.expiration.days:3}") int expirationInDays,
        @Value("${memcached.itemMaxSize:1048576}") int itemMaxSize,
        @Value("${memcached.compressionThreshold:2048}") int compressionThreshold,
        @Value("${memcached.clientPoolSize:1}") int clientPoolSize,
        @Value("${memcached.timeout.millis:800}") int operationTimeoutMs,
        @Value("${memcached.cas.timeout.millis:1600}") int casOperationTimeoutMs,
        @Value("${memcached.retry.count:2}") int retryCount,
        @Value("${memcached.retry.backoff.ms:50}") int retryBackoffMs,
        ServiceLocator serviceLocator) throws InvalidMemcachedExpirationException {

        this.clientLibrary = clientLibrary;
        this.memcachedServers = memcachedServers;
        this.serverType = MemcachedServer.valueOf(serverType.toUpperCase());
        this.protocol = MemcachedProtocol.valueOf(protocol.toUpperCase());
        this.expiration = new MemcachedExpiration(Duration.ofDays(expirationInDays));
        this.itemMaxSize = itemMaxSize;
        this.compressionThreshold = compressionThreshold;
        this.clientPoolSize = clientPoolSize;
        this.operationTimeoutMs = operationTimeoutMs;
        this.casOperationTimeoutMs = casOperationTimeoutMs;
        this.retryCount = retryCount;
        this.retryBackoffMs = retryBackoffMs;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public ExtoleMemcachedClientBuilder create() {
        return create(clientLibrary);
    }

    @Override
    public ExtoleMemcachedClientBuilder create(String clientLibrary) {
        CloseableExtoleMemcachedClientBuilder builder;
        if (FolsomExtoleMemcachedClientBuilder.CLIENT_LIBRARY_NAME.equals(clientLibrary)) {
            builder = initialize(serviceLocator.create(CloseableFolsomExtoleMemcachedClientBuilder.class));
        } else {
            throw new IllegalArgumentException("Memcached client library: " + clientLibrary + " is not supported");
        }
        memcachedBuilders.add(builder);
        return builder;
    }

    private <T extends ExtoleMemcachedClientBuilder> T initialize(T builder) {
        builder.withServers(memcachedServers)
            .withServerType(serverType)
            .withProtocol(protocol)
            .withExpiration(expiration)
            .withItemMaxSize(itemMaxSize)
            .withCompressionThreshold(compressionThreshold)
            .withClientPoolSize(clientPoolSize)
            .withOperationTimeoutMs(operationTimeoutMs)
            .withCasOperationTimeoutMs(casOperationTimeoutMs)
            .withRetryCount(retryCount)
            .withRetryBackoffMs(retryBackoffMs);
        return builder;
    }

    @Override
    public void stop(Runnable callback) {
        LOG.warn("Shutting down ExtoleMemcachedFactory");
        memcachedBuilders.forEach(value -> {
            try {
                value.close();
            } catch (Exception e) {
                LOG.error("Error closing memcached client: {}", value, e);
            }
        });
        callback.run();
    }
}
