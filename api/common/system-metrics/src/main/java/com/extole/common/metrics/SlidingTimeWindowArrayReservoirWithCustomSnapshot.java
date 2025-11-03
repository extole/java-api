package com.extole.common.metrics;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Snapshot;

public class SlidingTimeWindowArrayReservoirWithCustomSnapshot extends SlidingTimeWindowArrayReservoir {
    private final Function<long[], Snapshot> snapshotProvider;

    public SlidingTimeWindowArrayReservoirWithCustomSnapshot(long window, TimeUnit windowUnit,
        Function<long[], Snapshot> snapshotProvider) {
        super(window, windowUnit);
        this.snapshotProvider = snapshotProvider;
    }

    @Override
    public Snapshot getSnapshot() {
        return snapshotProvider.apply(super.getSnapshot().getValues());
    }
}
