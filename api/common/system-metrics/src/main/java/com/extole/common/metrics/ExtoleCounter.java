package com.extole.common.metrics;

import java.util.Optional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

public class ExtoleCounter {
    private final String name;
    private final MetricRegistry metricRegistry;

    private Optional<Counter> counter = Optional.empty();

    public ExtoleCounter(MetricRegistry metricRegistry, String name) {
        this.name = name;
        this.metricRegistry = metricRegistry;
    }

    public void increment() {
        increment(1);
    }

    public void increment(long n) {
        getCounter().inc(n);
    }

    public void decrement() {
        decrement(1);
    }

    public void decrement(long n) {
        getCounter().dec(n);
    }

    public long getCount() {
        return getCounter().getCount();
    }

    private Counter getCounter() {
        if (!counter.isPresent()) {
            counter = Optional.ofNullable(metricRegistry.counter(name));
        }
        return counter.get();
    }
}
