package com.extole.common.memcached.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import com.spotify.folsom.BinaryMemcacheClient;
import com.spotify.folsom.GetResult;
import com.spotify.folsom.MemcacheClient;
import com.spotify.folsom.MemcacheStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.memcached.ExtoleMemcachedClient.ValueWithVersion;
import com.extole.common.memcached.MemcachedExpiration;

class FolsomMemcachedClientAdapter implements MemcachedClientAdapter<byte[]> {
    private static final Logger LOG = LoggerFactory.getLogger(FolsomMemcachedClientAdapter.class);

    private static final String ERROR_MESSAGE_TEMPLATE = "Unable to execute method %s with key %s in store %s";
    private static final Set<MemcacheStatus> TRANSIENT_ERROR_STATUSES = Set.of(
        MemcacheStatus.OUT_OF_MEMORY,
        MemcacheStatus.INTERNAL_ERROR,
        MemcacheStatus.BUSY,
        MemcacheStatus.TEMPORARY_FAILURE);
    private static final Set<MemcacheStatus> EXPECTED_STATUSES = Set.of(
        MemcacheStatus.OK,
        MemcacheStatus.KEY_NOT_FOUND,
        MemcacheStatus.KEY_EXISTS,
        MemcacheStatus.ITEM_NOT_STORED);

    private static final int FLAG_COMPRESSED = 2;

    private final String storeName;
    private final MemcacheClient<byte[]> client;
    private final long timeoutMs;
    private final long casTimeoutMs;
    private final int compressionThreshold;
    private final MemcachedCompressor memcachedCompressor;
    private final String clientName;

    FolsomMemcachedClientAdapter(String storeName, MemcacheClient<byte[]> client, long timeoutMs,
        long casTimeoutMs, int compressionThreshold, MemcachedCompressor memcachedCompressor,
        String clientName) {
        this.storeName = storeName;
        this.client = client;
        this.timeoutMs = timeoutMs;
        this.casTimeoutMs = casTimeoutMs;
        this.compressionThreshold = compressionThreshold;
        this.memcachedCompressor = memcachedCompressor;
        this.clientName = clientName;
    }

    @Override
    @Nullable
    public byte[] get(String key) throws MemcachedClientAdapterException, TimeoutException, InterruptedException {
        CompletionStage<byte[]> result = client.casGet(key).thenApply(value -> decompress(key, value));
        return timedWait("get", timeoutMs, key, result);
    }

    @Override
    @Nullable
    public Map<String, byte[]> get(Collection<String> keys)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException {
        CompletionStage<Map<String, byte[]>> result =
            client.casGetAsMap(new ArrayList<>(keys)).thenApply(this::decompress);
        return timedWait("bulkGet", timeoutMs, keys, result);
    }

    @Override
    @Nullable
    public ValueWithVersion<byte[]> getWithVersion(String key)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException {
        CompletionStage<ValueWithVersion<byte[]>> result =
            client.casGet(key).thenApply(value -> decompressWithVersion(key, value));
        return timedWait("getWithVersion", timeoutMs, key, result);
    }

    @Override
    public boolean set(String key, byte[] value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        MemcachedValue memcachedValue = compress(key, value);
        return timedWaitStatus("set", timeoutMs, key,
            client.set(key, memcachedValue.getValue(), expiration.getSeconds(), memcachedValue.getFlags()));
    }

    @Override
    public void setWithNoReply(String key, byte[] value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException {
        MemcachedValue memcachedValue = compress(key, value);
        noWaitCheckStatus("setWithNoReply", key,
            client.set(key, memcachedValue.getValue(), expiration.getSeconds(), memcachedValue.getFlags()));
    }

    @Override
    public boolean add(String key, byte[] value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        MemcachedValue memcachedValue = compress(key, value);
        return timedWaitStatus("add", timeoutMs, key,
            client.add(key, memcachedValue.getValue(), expiration.getSeconds(), memcachedValue.getFlags()));
    }

    @Override
    public void addWithNoReply(String key, byte[] value, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException {
        MemcachedValue memcachedValue = compress(key, value);
        noWaitCheckStatus("addWithNoReply", key,
            client.add(key, memcachedValue.getValue(), expiration.getSeconds(), memcachedValue.getFlags()));
    }

    @Override
    public boolean delete(String key) throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        return timedWaitStatus("delete", timeoutMs, key, client.delete(key));
    }

    @Override
    public boolean touch(String key, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        return timedWaitStatus("touch", timeoutMs, key, client.touch(key, expiration.getSeconds()));
    }

    @Override
    public boolean cas(String key, byte[] value, long cas, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        MemcachedValue memcachedValue = compress(key, value);
        return timedWaitStatus("cas", casTimeoutMs, key,
            client.set(key, memcachedValue.getValue(), expiration.getSeconds(), cas, memcachedValue.getFlags()));
    }

    @Override
    public Long increment(String key, long by, long initialValue, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException {
        if (client instanceof BinaryMemcacheClient) {
            BinaryMemcacheClient<byte[]> binaryClient = (BinaryMemcacheClient<byte[]>) client;
            return timedWait("increment", timeoutMs, key,
                binaryClient.incr(key, by, initialValue, expiration.getSeconds()));
        }
        throw new UnsupportedOperationException("increment is only supported by binary protocol");
    }

    @Override
    public Long decrement(String key, long by, long initialValue, MemcachedExpiration expiration)
        throws MemcachedClientAdapterException, TimeoutException, InterruptedException {
        if (client instanceof BinaryMemcacheClient) {
            BinaryMemcacheClient<byte[]> binaryClient = (BinaryMemcacheClient<byte[]>) client;
            return timedWait("decrement", timeoutMs, key,
                binaryClient.decr(key, by, initialValue, expiration.getSeconds()));
        }
        throw new UnsupportedOperationException("decrement is only supported by binary protocol");
    }

    private Map<String, byte[]> decompress(Map<String, GetResult<byte[]>> results) {
        if (results == null) {
            return null;
        }
        Map<String, byte[]> decompressedResults = new HashMap<>(results.size());
        for (Entry<String, GetResult<byte[]>> entry : results.entrySet()) {
            decompressedResults.put(entry.getKey(), decompress(entry.getKey(), entry.getValue()));
        }
        return decompressedResults;
    }

    private byte[] decompress(String key, GetResult<byte[]> result) {
        ValueWithVersion<byte[]> valueWithVersion = decompressWithVersion(key, result);
        return valueWithVersion != null ? valueWithVersion.getValue() : null;
    }

    private ValueWithVersion<byte[]> decompressWithVersion(String key, GetResult<byte[]> result) {
        if (result == null) {
            return null;
        }
        try {
            byte[] value = result.getValue();
            if ((result.getFlags() & FLAG_COMPRESSED) != 0) {
                LOG.trace("Decompressing value ({} bytes) for key {} in store {}", value.length, key, storeName);
                value = memcachedCompressor.decompress(key, value);
                LOG.trace("Decompressed value ({} bytes) for key {} in store {}", value.length, key, storeName);
            }
            return new ValueWithVersionImpl<>(value, result.getCas(), clientName);
        } catch (MemcachedCompressorException e) {
            throw new RuntimeMemcachedCompressorException(e);
        }
    }

    private MemcachedValue compress(String key, byte[] value) throws TransientMemcachedClientAdapterException {
        try {
            int flags = 0;
            if (value != null && value.length > compressionThreshold) {
                LOG.trace("Compressing value ({} bytes) for key {} in store {}", value.length, key, storeName);
                value = memcachedCompressor.compress(key, value);
                LOG.trace("Compressed value ({} bytes) for key {} in store {}", value.length, key, storeName);
                flags |= FLAG_COMPRESSED;
            }
            return new MemcachedValue(value, flags);
        } catch (MemcachedCompressorException e) {
            throw new TransientMemcachedClientAdapterException(e.getMessage(), e);
        }
    }

    private boolean timedWaitStatus(String method, long timeoutMs, Object key,
        CompletionStage<MemcacheStatus> completionStage)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        MemcacheStatus memcacheStatus = timedWait(method, timeoutMs, key, completionStage);
        translateStatusToException(method, key, memcacheStatus);
        return memcacheStatus == MemcacheStatus.OK;
    }

    private <R> R timedWait(String method, long timeoutMs, Object key, CompletionStage<R> completionStage)
        throws MemcachedClientAdapterException, InterruptedException, TimeoutException {
        CompletableFuture<R> future = completionStage.toCompletableFuture();
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new TransientMemcachedClientAdapterException(
                String.format(ERROR_MESSAGE_TEMPLATE, method, key, storeName), cause);
        } finally {
            if (!future.isDone()) {
                future.cancel(false);
            }
        }
    }

    private void noWaitCheckStatus(String method, Object key, CompletionStage<MemcacheStatus> completionStage)
        throws MemcachedClientAdapterException {
        CompletableFuture<MemcacheStatus> future = completionStage.toCompletableFuture();
        if (future.isDone()) {
            try {
                MemcacheStatus memcacheStatus = future.get();
                translateStatusToException(method, key, memcacheStatus);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                throw new TransientMemcachedClientAdapterException(
                    String.format(ERROR_MESSAGE_TEMPLATE, method, key, storeName), cause);
            } catch (InterruptedException e) {
                // should not happen because we are calling get on a completed future
                Thread.currentThread().interrupt();
                throw new NonTransientMemcachedClientAdapterException(
                    String.format(ERROR_MESSAGE_TEMPLATE, method, key, storeName), e);
            }
        }
    }

    private void translateStatusToException(String method, Object key, MemcacheStatus memcacheStatus)
        throws MemcachedClientAdapterException {
        LOG.trace("Operation {} for key {} in store {} received status {}", method, key, storeName, memcacheStatus);
        if (memcacheStatus == MemcacheStatus.VALUE_TOO_LARGE) {
            throw new ValueTooLargeMemcachedException(
                String.format("Value too large for key %s in store %s using method %s", key, storeName, method));
        }
        if (TRANSIENT_ERROR_STATUSES.contains(memcacheStatus)) {
            throw new TransientMemcachedClientAdapterException(String.format(
                "Operation %s for key %s in store %s received status %s", method, key, storeName, memcacheStatus));
        }
        if (!EXPECTED_STATUSES.contains(memcacheStatus)) {
            LOG.error("Operation {} for key {} in store {} received unexpected status {}", method, key, storeName,
                memcacheStatus);
        }
    }

    private static class RuntimeMemcachedCompressorException extends RuntimeException {
        RuntimeMemcachedCompressorException(MemcachedCompressorException cause) {
            super(cause);
        }
    }

    private static class MemcachedValue {
        private final byte[] value;
        private final int flags;

        MemcachedValue(byte[] value, int flags) {
            super();
            this.value = value;
            this.flags = flags;
        }

        public byte[] getValue() {
            return value;
        }

        public int getFlags() {
            return flags;
        }
    }
}
