package com.extole.common.memcached.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.spotify.folsom.ConnectionChangeListener;
import com.spotify.folsom.MemcacheClient;
import com.spotify.folsom.MemcacheClientBuilder;
import com.spotify.folsom.ObservableClient;
import com.spotify.folsom.client.NoopMetrics;
import com.spotify.folsom.elasticache.ElastiCacheResolver;
import com.spotify.folsom.guava.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedClientBuilder;
import com.extole.common.memcached.ExtoleMemcachedLoader;
import com.extole.common.memcached.LoadingExtoleMemcachedClient;
import com.extole.common.memcached.MemcachedExpiration;
import com.extole.common.memcached.MemcachedProtocol;
import com.extole.common.memcached.MemcachedServer;
import com.extole.common.memcached.MemcachedTransformer;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.spring.ServiceLocator;

@Component
@Scope(ServiceLocator.PROTOTYPE)
class FolsomExtoleMemcachedClientBuilderImpl implements CloseableFolsomExtoleMemcachedClientBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(FolsomExtoleMemcachedClientBuilderImpl.class);

    private static final int CONNECT_AWAIT_SECONDS = 60;
    private static final int SHUTDOWN_AWAIT_SECONDS = 60;
    private static final String DEFAULT_CLIENT_NAME = "folsom";

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicReference<MemcacheClient<byte[]>> memcachedClient = new AtomicReference<>();
    private final ExtoleMetricRegistry metricRegistry;
    private final int eventLoopThreadFlushMaxBatchSize;
    private final int connectionTimeoutBonusMs;

    private String memcachedServers;
    private MemcachedServer serverType;
    private MemcachedProtocol protocol;
    private MemcachedExpiration expiration;
    private int itemMaxSize;
    private int clientPoolSize;
    private int compressionThreshold;
    private long operationTimeoutMs;
    private long casOperationTimeoutMs;
    private int retryCount;
    private long retryBackoffMs;
    private int maxOutstandingRequests;
    private String clientName = DEFAULT_CLIENT_NAME;

    @Autowired
    FolsomExtoleMemcachedClientBuilderImpl(
        @Value("${memcached.folsom.eventLoopThreadFlushMaxBatchSize:20}") int eventLoopThreadFlushMaxBatchSize,
        @Value("${memcached.folsom.maxOutstandingRequests:1000}") int maxOutstandingRequests,
        @Value("${memcached.folsom.connectionTimeoutBonusMs:200}") int connectionTimeoutBonusMs,
        ExtoleMetricRegistry metricRegistry) {

        this.eventLoopThreadFlushMaxBatchSize = eventLoopThreadFlushMaxBatchSize;
        this.maxOutstandingRequests = maxOutstandingRequests;
        this.connectionTimeoutBonusMs = connectionTimeoutBonusMs;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public <S, K, V> ExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer) {
        return build(storeName, transformer, expiration);
    }

    @Override
    public <S, K, V> ExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        MemcachedExpiration expiration) {
        if (!initialized.get()) {
            throw new IllegalStateException("Not initialized");
        }
        MemcachedClientAdapter adapter = new FolsomMemcachedClientAdapter(storeName, memcachedClient.get(),
            operationTimeoutMs, casOperationTimeoutMs, compressionThreshold,
            new GzipMemcachedCompressor(storeName, metricRegistry),
            clientName);
        return new ExtoleMemcachedClientImpl<>(storeName, adapter, transformer, metricRegistry, expiration, retryCount,
            retryBackoffMs, clientName);
    }

    @Override
    public <S, K, V> LoadingExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        ExtoleMemcachedLoader<K, Optional<V>> loader) {
        return build(storeName, transformer, loader, expiration);
    }

    @Override
    public <S, K, V> LoadingExtoleMemcachedClient<K, V> build(String storeName, MemcachedTransformer<S, V> transformer,
        ExtoleMemcachedLoader<K, Optional<V>> loader, MemcachedExpiration expiration) {
        return new LoadingExtoleMemcachedClientImpl<>(storeName, build(storeName, transformer, expiration), loader);
    }

    @Override
    public ExtoleMemcachedClientBuilder initialize() {
        if (!initialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Already initialized");
        }
        memcachedClient.set(buildMemcachedClient());
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withServers(String memcachedServers) {
        this.memcachedServers = memcachedServers;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withServerType(MemcachedServer serverType) {
        this.serverType = serverType;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withProtocol(MemcachedProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withExpiration(MemcachedExpiration expiration) {
        this.expiration = expiration;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withItemMaxSize(int itemMaxSize) {
        this.itemMaxSize = itemMaxSize;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withClientPoolSize(int clientPoolSize) {
        this.clientPoolSize = clientPoolSize;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withOperationTimeoutMs(long operationTimeoutMs) {
        this.operationTimeoutMs = operationTimeoutMs;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withCasOperationTimeoutMs(long casOperationTimeoutMs) {
        this.casOperationTimeoutMs = casOperationTimeoutMs;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
        return this;
    }

    @Override
    public FolsomExtoleMemcachedClientBuilder withMaxOutstandingRequests(int maxOutstandingRequests) {
        this.maxOutstandingRequests = maxOutstandingRequests;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        return this;
    }

    @Override
    public ExtoleMemcachedClientBuilder withClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    @Override
    public void close() {
        if (memcachedClient.get() == null) {
            LOG.debug("No memcached clients to close");
            return;
        }
        LOG.debug("Closing memcached clients");
        MemcacheClient<byte[]> client = memcachedClient.get();
        try {
            client.shutdown();
            client.fullyDisconnectFuture().toCompletableFuture().get(SHUTDOWN_AWAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted on shutdown memcached client: {}", client, e);
        } catch (TimeoutException | ExecutionException e) {
            LOG.error("Failed to shutdown memcached client: {}", client, e);
        }
    }

    private MemcacheClient<byte[]> buildMemcachedClient() {
        MemcacheClientBuilder<byte[]> builder = MemcacheClientBuilder.newByteArrayClient();
        if (serverType == MemcachedServer.ELASTICACHE) {
            HostAndPort server = HostAndPort.fromString(memcachedServers);
            ElastiCacheResolver elastiCacheResolver = ElastiCacheResolver.newBuilder(server.getHostText())
                .withConfigPort(server.getPort())
                .build();
            builder.withResolver(elastiCacheResolver);
        } else {
            List<HostAndPort> servers =
                Arrays.stream(memcachedServers.split(",")).map(HostAndPort::fromString).collect(Collectors.toList());
            servers.forEach(value -> builder.withAddress(value.getHostText(), value.getPort()));
        }
        builder.withMetrics(new NoopMetrics() {
            @Override
            public void registerOutstandingRequestsGauge(OutstandingRequestsGauge gauge) {
                metricRegistry.histogram("memcached.client." + clientName + ".outstanding-requests")
                    .update(gauge.getOutstandingRequests());
            }
        });
        builder.withConnections(clientPoolSize);
        builder.withMaxOutstandingRequests(maxOutstandingRequests);
        builder.withEventLoopThreadFlushMaxBatchSize(eventLoopThreadFlushMaxBatchSize);
        builder.withMaxSetLength(itemMaxSize);
        builder.withConnectionTimeoutMillis(operationTimeoutMs + connectionTimeoutBonusMs);

        MemcacheClient<byte[]> client;
        if (protocol == MemcachedProtocol.TEXT) {
            client = builder.connectAscii();
        } else {
            client = builder.connectBinary();
        }

        client.registerForConnectionChanges(new FolsomConnectionChangeListener(metricRegistry));

        try {
            client.fullyConnectedFuture().toCompletableFuture().get(CONNECT_AWAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExtoleMemcachedClientBuildRuntimeException("MemcachedClient build was interrupted", e);
        } catch (TimeoutException | ExecutionException e) {
            throw new ExtoleMemcachedClientBuildRuntimeException("Failed to build MemcachedClient", e);
        }
        return client;
    }

    private static final class FolsomConnectionChangeListener implements ConnectionChangeListener {
        private final ExtoleMetricRegistry metricRegistry;
        private final String disconnectedMetricName;

        private FolsomConnectionChangeListener(ExtoleMetricRegistry metricRegistry) {
            this.metricRegistry = metricRegistry;
            this.disconnectedMetricName = "memcached.client.disconnected";
        }

        @Override
        public void connectionChanged(ObservableClient client) {
            if (!client.isConnected()) {
                metricRegistry.counter(disconnectedMetricName).increment();
            }
            LOG.info("ConnectionChanged for client (connected={}): {}", client.isConnected(), client);
        }
    }
}
