package com.extole.common.memcached;

import java.io.IOException;
import java.util.Optional;

import com.google.common.io.ByteSource;
import com.google.common.io.FileBackedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class ByteSourceMemcachedTransformer implements MemcachedTransformer<byte[], ByteSource> {
    private static final Logger LOG = LoggerFactory.getLogger(ByteSourceMemcachedTransformer.class);

    private final ExtoleMetricRegistry metricRegistry;
    private final String storeName;
    private final int fileThreshold;
    private final int warningThreshold;

    public ByteSourceMemcachedTransformer(ExtoleMetricRegistry metricRegistry, String storeName, int fileThreshold,
        int warningThreshold) {
        this.metricRegistry = metricRegistry;
        this.storeName = storeName;
        this.fileThreshold = fileThreshold;
        this.warningThreshold = warningThreshold;
    }

    @Override
    public byte[] encode(MemcachedKey key, ByteSource value) throws MemcachedTransformerException {
        try {
            byte[] result = value.read();
            if (result != null) {
                if (result.length > warningThreshold) {
                    LOG.warn("Encoded value size ({}) for key: {} is greater than the configured threshold ({})",
                        Integer.valueOf(result.length), key, Integer.valueOf(warningThreshold));
                }
                metricRegistry.histogram(buildMetricNameWithStore(MemcachedMetric.SERIALIZE_SIZE))
                    .update(result.length);
            }
            return result;
        } catch (IOException e) {
            throw new MemcachedTransformerException("Unable to read from ByteSource for key: " + key, e);
        }
    }

    @Override
    public Optional<ByteSource> decode(MemcachedKey key, byte[] serializedValue) throws MemcachedTransformerException {
        if (serializedValue == null) {
            return Optional.empty();
        }
        if (serializedValue.length > warningThreshold) {
            LOG.warn("Decoded value size ({}) for key: {} is greater than the configured threshold ({})",
                Integer.valueOf(serializedValue.length), key, Integer.valueOf(warningThreshold));
        }
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(fileThreshold, true)) {
            outputStream.write(serializedValue);
            return Optional.of(outputStream.asByteSource());
        } catch (IOException e) {
            throw new MemcachedTransformerException("Unable to write to ByteSource for key: " + key, e);
        }
    }

    private String buildMetricNameWithStore(MemcachedMetric memcachedMetric) {
        return storeName + "." + memcachedMetric.getMetricName();
    }

}
