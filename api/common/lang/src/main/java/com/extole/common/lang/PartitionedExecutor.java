package com.extole.common.lang;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class PartitionedExecutor {
    private final List<ListeningExecutorService> executors;
    private final int threadsCount;

    public PartitionedExecutor(String name, int threadsCount) {
        this.threadsCount = threadsCount;
        ImmutableList.Builder<ListeningExecutorService> builder = ImmutableList.builder();
        ThreadFactory threadFactory = new ExtoleThreadFactory(name);
        for (int i = 0; i < threadsCount; i++) {
            builder.add(MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor(threadFactory)));
        }
        executors = builder.build();
    }

    public void shutdown() {
        for (ExecutorService executor : executors) {
            executor.shutdownNow();
        }
    }

    public <T> ListenableFuture<T> execute(Supplier<Integer> hash, Callable<T> command) {
        int threadPos = Math.abs(hash.get().intValue()) % threadsCount;
        return executors.get(threadPos).submit(command);
    }
}
