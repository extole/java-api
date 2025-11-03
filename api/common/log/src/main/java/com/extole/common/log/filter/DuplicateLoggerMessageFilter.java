package com.extole.common.log.filter;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import org.slf4j.Marker;

public class DuplicateLoggerMessageFilter extends TurboFilter {

    private static final int DEFAULT_CACHE_SIZE = 100;
    private static final int DEFAULT_ALLOWED_REPETITIONS = 1;
    private static final int DEFAULT_EXPIRATION_SECONDS = 1;

    private final Set<String> loggers = Sets.newHashSet();

    private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;
    private int expirationSeconds = DEFAULT_EXPIRATION_SECONDS;
    private int cacheSize = DEFAULT_CACHE_SIZE;

    private Cache<String, AtomicInteger> messageCache;

    @Override
    public void start() {
        messageCache = CacheBuilder.newBuilder().recordStats().maximumSize(cacheSize)
            .expireAfterWrite(expirationSeconds, TimeUnit.SECONDS).build();
        super.start();
    }

    @Override
    public void stop() {
        messageCache.cleanUp();
        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level,
        String format, Object[] params, Throwable t) {
        if (loggers.stream().noneMatch(loggerName -> logger.getName().startsWith(loggerName))) {
            return FilterReply.NEUTRAL;
        }

        int count = 1;
        if (level.isGreaterOrEqual(logger.getEffectiveLevel()) && !Strings.isNullOrEmpty(format)) {
            try {
                count = messageCache.get(format, () -> new AtomicInteger(0)).incrementAndGet();
            } catch (ExecutionException ignore) {
                // should never happen
                count = 1;
            }
            if (count <= allowedRepetitions) {
                messageCache.put(format, new AtomicInteger(count));
            }
        }

        if (count <= allowedRepetitions) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public void addLogger(String logger) {
        this.loggers.add(logger);
    }

    public int getAllowedRepetitions() {
        return allowedRepetitions;
    }

    public void setAllowedRepetitions(int allowedRepetitions) {
        this.allowedRepetitions = allowedRepetitions;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setExpirationSeconds(int expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }
}
