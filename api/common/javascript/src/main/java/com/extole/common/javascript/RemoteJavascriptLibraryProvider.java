package com.extole.common.javascript;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.CharStreams;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.GuavaCacheMetrics;
import com.extole.common.rest.client.HttpClientBuilder;
import com.extole.common.rest.util.HttpResponseReader;
import com.extole.common.rest.util.HttpResponseReader.HttpResponseReaderException;

class RemoteJavascriptLibraryProvider implements JavascriptLibraryProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteJavascriptLibraryProvider.class);

    private static final int RETRY_INTERVAL_MS = 500;
    private static final int RETRY_COUNT = 5;
    private static final int MAX_CONNECTIONS = 200;
    private static final String EXTOLE_DEBUG_HEADER = "X-Extole-Debug";
    private static final JavascriptMetrics REMOTE_LIBRARY_LOAD_DURATION =
        new JavascriptMetrics("javascript.remote.library.load.duration.ms");

    private final ExtoleMetricRegistry metricRegistry;
    private final CloseableHttpClient httpClient;
    private final LoadingCache<String, String> libraryCache;
    private final boolean minified;

    RemoteJavascriptLibraryProvider(ExtoleMetricRegistry metricRegistry, int cacheExpirationMinutes, int cacheSize,
        boolean minified, String applicationName) {
        this.metricRegistry = metricRegistry;
        this.minified = minified;
        this.libraryCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(cacheExpirationMinutes, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public String load(String key) throws Exception {
                    return loadLibrary(key);
                }
            });
        String cacheName = minified ? "MinifiedRemoteJavascriptLibraryCache" : "RemoteJavascriptLibraryCache";
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor(cacheName, this.libraryCache));

        this.httpClient = new HttpClientBuilder()
            .withMaxConnections(MAX_CONNECTIONS)
            .withMaxConnectionsPerRoute(MAX_CONNECTIONS)
            .withRetryCount(RETRY_COUNT)
            .withRetryIntervalMs(RETRY_INTERVAL_MS)
            .withAppName(applicationName)
            .build();
    }

    public void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            LOG.error("HttpClient close exception", e);
        }
    }

    @Override
    public String getLibrary(String uri) throws JavascriptLibraryLoadException {
        try {
            return libraryCache.get(uri);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof JavascriptLibraryLoadException) {
                throw (JavascriptLibraryLoadException) e.getCause();
            }
            throw new JavascriptLibraryLoadException("Unable to get cached value", e);
        }
    }

    private String loadLibrary(String uri) throws JavascriptLibraryLoadException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader(EXTOLE_DEBUG_HEADER, String.valueOf(!minified));
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            String response = new HttpResponseReader<String>(httpResponse)
                .withSuccessHandler((statusLine, inputStream) -> CharStreams
                    .toString(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8)))
                .withErrorHandler((statusLine, inputStream) -> new HttpResponseReaderException(
                    "Error response: " + httpResponse.toString() + ", Body: " + CharStreams
                        .toString(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8)),
                    statusLine))
                .read();
            return response;
        } catch (HttpResponseReaderException | IOException e) {
            throw new JavascriptLibraryLoadException("Failed to load library: " + uri, e);
        } finally {
            stopwatch.stop();
            long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            LOG.trace("Loaded remote library {} in: {} ms", uri, Long.valueOf(elapsedTime));
            REMOTE_LIBRARY_LOAD_DURATION.updateHistogram(metricRegistry, elapsedTime);
        }
    }
}
