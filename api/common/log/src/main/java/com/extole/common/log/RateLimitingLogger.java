package com.extole.common.log;

import java.time.Duration;
import java.util.function.BiConsumer;

import org.slf4j.Logger;

public class RateLimitingLogger implements AutoCloseable {
    private final Logger logger;
    private final SimpleRateLimiter rateLimiter;

    public RateLimitingLogger(Logger logger, long keysCacheMaxSize, Duration limitDuration, int limitDurationPermits) {
        this.logger = logger;
        this.rateLimiter = new SimpleRateLimiter(keysCacheMaxSize, limitDuration, limitDurationPermits,
            new BiConsumer<String, Long>() {
                @Override
                public void accept(String key, Long occurrences) {
                    logger.warn("Suppressed {} messages for key: {}", occurrences, key);
                }
            });
    }

    @Override
    public void close() {
        rateLimiter.close();
    }

    public void log(String eventKey, LoggerLevel level, String format, Object... arguments) {
        if (!rateLimiter.isAllowed(eventKey)) {
            return;
        }
        switch (level) {
            case INFO:
                logger.info(format, arguments);
                break;
            case WARN:
                logger.warn(format, arguments);
                break;
            case ERROR:
                logger.error(format, arguments);
                break;
            default:
                throw new IllegalArgumentException("Unsupported logger level: " + level);
        }
    }

    public enum LoggerLevel {
        INFO, WARN, ERROR
    }
}
