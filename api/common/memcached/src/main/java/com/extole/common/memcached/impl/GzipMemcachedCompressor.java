package com.extole.common.memcached.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.extole.common.memcached.MemcachedMetric;
import com.extole.common.metrics.ExtoleMetricRegistry;

class GzipMemcachedCompressor implements MemcachedCompressor {
    private static final int BUFFER_SIZE = 64 * 1024;

    private final String storeName;
    private final ExtoleMetricRegistry metricRegistry;

    GzipMemcachedCompressor(String storeName, ExtoleMetricRegistry metricRegistry) {
        this.storeName = storeName;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public byte[] decompress(String key, byte[] value) throws MemcachedCompressorException {
        if (value == null) {
            return null;
        }
        Instant startTime = Instant.now();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(value);
                GZIPInputStream gis = new GZIPInputStream(bis)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = gis.read(buffer)) > 0) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            return bos.toByteArray();
        } catch (IOException e) {
            metricRegistry.counter(buildMetricNameWithStore(MemcachedMetric.DECOMPRESS_ERROR_COUNTER)).increment();
            throw new MemcachedCompressorException(
                "Unable to decompress the value for key " + key + " in store " + storeName, e);
        } finally {
            long duration = System.currentTimeMillis() - startTime.toEpochMilli();
            metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.DECOMPRESS_TIME)).update(duration);
        }
    }

    @Override
    public byte[] compress(String key, byte[] value) throws MemcachedCompressorException {
        if (value == null) {
            return null;
        }
        Instant startTime = Instant.now();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
                gz.write(value);
            }
            byte[] result = bos.toByteArray();
            metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.COMPRESS_SIZE)).update(result.length);
            return result;
        } catch (IOException e) {
            metricRegistry.counter(buildMetricNameWithStore(MemcachedMetric.COMPRESS_ERROR_COUNTER)).increment();
            throw new MemcachedCompressorException("Unable to compress value for key " + key + " in store " + storeName,
                e);
        } finally {
            long duration = System.currentTimeMillis() - startTime.toEpochMilli();
            metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.COMPRESS_TIME)).update(duration);
        }
    }

    private String buildMetricNameWithStore(MemcachedMetric memcachedMetric) {
        return storeName + "." + memcachedMetric.getMetricName();
    }
}
