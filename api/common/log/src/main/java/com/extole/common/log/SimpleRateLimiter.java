package com.extole.common.log;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class SimpleRateLimiter implements AutoCloseable {
    private final LoadingCache<String, AtomicLong> keysCache;
    private final int limitDurationPermits;

    public SimpleRateLimiter(long keysCacheMaxSize, Duration limitDuration, int limitDurationPermits,
        BiConsumer<String, Long> suppressListener) {
        this.limitDurationPermits = limitDurationPermits;
        this.keysCache = CacheBuilder.newBuilder()
            .maximumSize(keysCacheMaxSize)
            .expireAfterWrite(limitDuration)
            .removalListener(new RemovalListener<String, AtomicLong>() {
                @Override
                public void onRemoval(RemovalNotification<String, AtomicLong> notification) {
                    String key = notification.getKey();
                    AtomicLong value = notification.getValue();
                    if (key != null && value != null && value.longValue() > limitDurationPermits) {
                        suppressListener.accept(notification.getKey(), value.longValue() - limitDurationPermits);
                    }
                }
            })
            .build(new CacheLoader<String, AtomicLong>() {
                @Override
                public AtomicLong load(String key) {
                    return new AtomicLong(0);
                }
            });
    }

    @Override
    public void close() {
        // this is required in order to execute cache removal listener which will trigger the suppress listener
        this.keysCache.invalidateAll();
        this.keysCache.cleanUp();
    }

    public boolean isAllowed(String key) {
        try {
            return keysCache.get(key).incrementAndGet() <= limitDurationPermits;
        } catch (ExecutionException e) {
            // should not happen
            return false;
        }
    }
}
