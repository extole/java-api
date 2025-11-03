package com.extole.common.lang;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtoleThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleThreadFactory.class);

    private final AtomicInteger sequence = new AtomicInteger(1);
    private final String threadNamePrefix;
    private final UncaughtExceptionHandler exceptionHandler;

    public ExtoleThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, (t, e) -> LOG.error("#FATAL Uncaught Exception from thread {}", t, e));
    }

    public ExtoleThreadFactory(String threadNamePrefix, UncaughtExceptionHandler exceptionHandler) {
        this.threadNamePrefix = threadNamePrefix;
        this.exceptionHandler = exceptionHandler;
    }

    public static ExtoleThreadFactory of(String threadNamePrefix) {
        return new ExtoleThreadFactory(threadNamePrefix);
    }

    @Override
    public Thread newThread(Runnable target) {
        String threadName = generateNewThreadName();

        LOG.debug("Creating new worker thread {}", threadName);

        Thread thread = new Thread(target, threadName);
        thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    }

    private String generateNewThreadName() {
        return threadNamePrefix + "-" + sequence.getAndIncrement();
    }
}
