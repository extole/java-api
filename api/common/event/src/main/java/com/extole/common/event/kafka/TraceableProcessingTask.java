package com.extole.common.event.kafka;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TraceableProcessingTask implements Callable<Void> {

    private final AtomicBoolean success = new AtomicBoolean(false);
    private final AtomicReference<Thread> executorThread = new AtomicReference<>();
    private final Callable<Void> callable;

    public TraceableProcessingTask(Callable<Void> callable) {
        this.callable = callable;
    }

    @Override
    public Void call() throws Exception {
        executorThread.set(Thread.currentThread());
        callable.call();
        success.set(true);
        return null;
    }

    public boolean isSuccess() {
        return success.get();
    }

    public StackTraceElement[] getStackTrace() {
        return executorThread.get() != null ? executorThread.get().getStackTrace() : new StackTraceElement[0];
    }
}
