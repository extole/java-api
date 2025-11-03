package com.extole.common.webapp.healthcheck;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.qos.logback.classic.spi.ILoggingEvent;

public final class LoggingInterceptor {
    private static final String LOG_OF_DEATH_CLASS = "org.apache.kafka.clients.producer.internals.Sender";
    static final AtomicBoolean IS_HEALTHY = new AtomicBoolean(true);

    private LoggingInterceptor() {
    }

    public static void intercept(ILoggingEvent event) {
        if (event.getLoggerName().equals(LOG_OF_DEATH_CLASS)
            && event.getMessage().contains("Uncaught error in kafka producer") && IS_HEALTHY.get()) {
            IS_HEALTHY.set(false);
        }
    }
}
