package com.extole.common.metrics;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Snapshot;

public class ExtoleHistogram {
    private final String name;
    private final MetricRegistry metricRegistry;

    private final Optional<Function<long[], Snapshot>> snapshotProvider;
    private Optional<Histogram> histogram = Optional.empty();

    public ExtoleHistogram(MetricRegistry metricRegistry, String name) {
        this(metricRegistry, name, Optional.empty());
    }

    public ExtoleHistogram(MetricRegistry metricRegistry, String name, Function<long[], Snapshot> snapshotProvider) {
        this(metricRegistry, name, Optional.of(snapshotProvider));
    }

    private ExtoleHistogram(MetricRegistry metricRegistry, String name,
        Optional<Function<long[], Snapshot>> snapshotProvider) {
        this.name = name;
        this.metricRegistry = metricRegistry;
        this.snapshotProvider = snapshotProvider;
    }

    public void update(Instant startTime, Instant stopTime) {
        update(stopTime.toEpochMilli() - startTime.toEpochMilli());
    }

    public long getCount() {
        return getHistogram().getCount();
    }

    public Snapshot getSnapshot() {
        return getHistogram().getSnapshot();
    }

    public void update(long value) {
        getHistogram().update(value);
    }

    private Histogram getHistogram() {
        if (histogram.isEmpty()) {
            histogram = Optional.of(metricRegistry.histogram(name,
                () -> {
                    Function<Function<long[], Snapshot>, SlidingTimeWindowArrayReservoir> customSnapshotToReservoir =
                        snapshotFunction -> new SlidingTimeWindowArrayReservoirWithCustomSnapshot(1, TimeUnit.MINUTES,
                            snapshotFunction);
                    return new Histogram(snapshotProvider.map(customSnapshotToReservoir)
                        .orElseGet(() -> new SlidingTimeWindowArrayReservoir(1, TimeUnit.MINUTES)));
                }));
        }
        return histogram.get();
    }
}
