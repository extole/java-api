package com.extole.consumer.rest.impl.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.ClientHandle;
import com.extole.client.change.model.ClientChangeOperation;
import com.extole.common.client.pod.ClientPod;
import com.extole.common.client.pod.ClientPodService;
import com.extole.common.lang.ToString;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.GuavaCacheMetrics;
import com.extole.event.model.change.ClientChangeEvent;
import com.extole.event.model.change.ClientChangeEventListener;
import com.extole.id.Id;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.pojo.client.ClientPojo;
import com.extole.model.pojo.media.MediaAssetPojo;
import com.extole.model.pojo.program.PublicProgramPojo;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.creative.assemble.AssembledArchiveDownloader;
import com.extole.model.shared.PreloadedCache;
import com.extole.model.shared.client.ClientCache;

@Component
public class CoreJsCache implements PreloadedCache {
    private static final Logger LOG = LoggerFactory.getLogger(CoreJsCache.class);

    private static final Path CORE_JS_ASSET_PATH = Paths.get("/core.js");

    private final boolean primeCacheOnStartup;
    private final LoadingCache<CoreJsKey, String> coreJsCache;
    private final CacheLoader<CoreJsKey, String> cacheLoader;
    private final ExtoleMetricRegistry metricRegistry;
    private final ClientCache clientCache;
    private final ClientPodService clientPodService;

    @Autowired
    public CoreJsCache(
        @Value("${consumer.coreJsCache.size:1000}") long cacheSize,
        @Value("${consumer.coreJsCache.expiration.minutes:2880}") long cacheExpirationMinutes,
        @Value("${prime.cache.coreJsCache.onStartup:false}") boolean primeCacheOnStartup,
        AssembledArchiveDownloader assembledArchiveDownloader,
        ExtoleMetricRegistry metricRegistry,
        ClientPodService clientPodService,
        ClientCache clientCache) {
        this.metricRegistry = metricRegistry;
        this.clientPodService = clientPodService;
        this.clientCache = clientCache;
        this.cacheLoader = new CacheLoader<>() {
            @Override
            public String load(CoreJsKey key) throws Exception {
                Long coreAssetsVersion = clientCache.getById(key.getClientId()).getCoreAssetsVersion().getValue();
                LOG.debug("downloading {} from origin", key);
                return assembledArchiveDownloader.downloadAssembledCoreArchiveTextAssetFromSource(key.getClientId(),
                    coreAssetsVersion, key.isMinified(), CORE_JS_ASSET_PATH);
            }
        };
        this.coreJsCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(cacheExpirationMinutes, TimeUnit.MINUTES)
            .build(cacheLoader);
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor(CoreJsCache.class.getSimpleName(),
            this.coreJsCache));
        this.primeCacheOnStartup = primeCacheOnStartup;
    }

    @Override
    public void primeCache(List<Client> clients) {
        if (!primeCacheOnStartup) {
            LOG.warn("Skipping cache priming");
            return;
        }

        ClientPod currentPod = clientPodService.getCurrentPod();
        if (currentPod.equals(ClientPod.UNDEFINED)) {
            LOG.warn("Priming core.js for {} clients (instance has undefined pod)", clients.size());
            clients.forEach(client -> primeClient(client));
        } else {
            List<Client> clientsToPrime = clients.stream()
                .filter(client -> currentPod.equals(client.getPod()))
                .collect(Collectors.toList());
            LOG.warn("Priming core.js for clients from {} pod: {}", currentPod,
                clientsToPrime.stream().map(client -> client.getId()).collect(Collectors.toList()));
            clientsToPrime.forEach(client -> primeClient(client));
        }
    }

    public String getCoreJs(PublicClient client, boolean minified) throws CoreJsUnavailableException {
        try {
            return coreJsCache.get(new CoreJsKey(client.getId(), minified));
        } catch (ExecutionException e) {
            throw new CoreJsUnavailableException(
                "Unable to load core.js for client=" + client.getId() + " minified=" + minified, e);
        }
    }

    private void primeClient(Client client) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            getCoreJs(client, true);
        } catch (CoreJsUnavailableException e) {
            LOG.error("Unable to prime core archive cache for client={}", client.getId(), e);
        } finally {
            CoreArchiveMetrics.CORE_ARCHIVE_PRIME_DURATION.updateHistogram(metricRegistry,
                stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private void primeCacheOnEvent(ClientChangeEvent<?> event) {
        Id<ClientHandle> clientId = Id.valueOf(event.getClientId().getValue());
        boolean shouldPrime;
        try {
            shouldPrime = clientPodService.getCurrentPod().equals(clientCache.getById(clientId).getPod());
        } catch (ClientNotFoundException e) {
            LOG.debug("Invalidating core archive for deleted clientId={}, event={}", clientId, event);
            invalidateCache(clientId);
            return;
        }

        try {
            if (shouldPrime) {
                coreJsCache.invalidate(new CoreJsKey(clientId, false));
                CoreJsKey key = new CoreJsKey(clientId, true);
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    coreJsCache.put(key, cacheLoader.load(key));
                } finally {
                    stopwatch.stop();
                    long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                    CoreArchiveMetrics.CORE_ARCHIVE_PRIME_DURATION.updateHistogram(metricRegistry, elapsedMillis);
                    LOG.debug("Primed core for client {} in {} ms", clientId, Long.valueOf(elapsedMillis));
                }
            } else {
                invalidateCache(clientId);
            }
        } catch (Exception e) {
            throw new CorePrimeRuntimeException("Failed to prime core for client=" + clientId, e);
        }
    }

    private void invalidateCache(Id<ClientHandle> clientId) {
        coreJsCache.invalidate(new CoreJsKey(clientId, true));
        coreJsCache.invalidate(new CoreJsKey(clientId, false));
    }

    private enum CoreArchiveMetrics {
        CORE_ARCHIVE_PRIME_DURATION("core.archive.prime.duration.ms");

        private final String metricName;

        CoreArchiveMetrics(String metricName) {
            this.metricName = metricName;
        }

        public void updateHistogram(ExtoleMetricRegistry metricRegistry, long histogramValue) {
            metricRegistry.histogram(metricName).update(histogramValue);
        }
    }

    private static class CoreJsKey {

        private final Id<ClientHandle> clientId;
        private final boolean minified;

        CoreJsKey(Id<ClientHandle> clientId, boolean minified) {
            this.clientId = clientId;
            this.minified = minified;
        }

        public Id<ClientHandle> getClientId() {
            return clientId;
        }

        public boolean isMinified() {
            return minified;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            CoreJsKey that = (CoreJsKey) object;
            return Objects.equals(clientId, that.clientId)
                && Objects.equals(Boolean.valueOf(minified), Boolean.valueOf(that.minified));
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, Boolean.valueOf(minified));
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }

    }

    @Component
    public class ClientSettingsChangeEventListener implements ClientChangeEventListener<ClientPojo> {

        // TODO prime based on CoreAssetVersionPrimedEvent rather than ClientSettingsPojo

        @Override
        public void handleEvent(ClientChangeEvent<ClientPojo> event) {
            LOG.debug("Received {} : {}", event.getClass().getSimpleName(), event);
            if (ClientChangeOperation.DELETED.equals(event.getOperation())) {
                Id<ClientHandle> clientId = Id.valueOf(event.getClientId().getValue());
                LOG.debug("Invalidating core archive for deleted clientId={}, event={}", clientId, event);
                invalidateCache(clientId);
                return;
            }
            primeCacheOnEvent(event);
        }

        @Override
        public int getOrder() {
            return LOWEST_PRECEDENCE;
        }

        @Override
        public Class<ClientPojo> getKeyClass() {
            return ClientPojo.class;
        }
    }

    @Component
    public class ProgramChangeEventListener implements ClientChangeEventListener<PublicProgramPojo> {

        @Override
        public void handleEvent(ClientChangeEvent<PublicProgramPojo> event) {
            LOG.debug("Received {} : {}", event.getClass().getSimpleName(), event);
            primeCacheOnEvent(event);
        }

        @Override
        public int getOrder() {
            return LOWEST_PRECEDENCE;
        }

        @Override
        public Class<PublicProgramPojo> getKeyClass() {
            return PublicProgramPojo.class;
        }

    }

    @Component
    public class MediaAssetChangeEventListener implements ClientChangeEventListener<MediaAssetPojo> {

        @Override
        public void handleEvent(ClientChangeEvent<MediaAssetPojo> event) {
            LOG.debug("Received {} : {}", event.getClass().getSimpleName(), event);
            primeCacheOnEvent(event);
        }

        @Override
        public int getOrder() {
            return LOWEST_PRECEDENCE;
        }

        @Override
        public Class<MediaAssetPojo> getKeyClass() {
            return MediaAssetPojo.class;
        }
    }
}
