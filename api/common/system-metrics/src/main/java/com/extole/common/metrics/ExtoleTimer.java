package com.extole.common.metrics;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Timer;

public class ExtoleTimer {
    private final String name;
    private final MetricRegistry metricRegistry;

    private Optional<Timer> timer = Optional.empty();

    public ExtoleTimer(MetricRegistry metricRegistry, String name) {
        this.name = name;
        this.metricRegistry = metricRegistry;
    }

    public void update(long duration, TimeUnit unit) {
        getTimer().update(duration, unit);
    }

    private Timer getTimer() {
        if (!timer.isPresent()) {
            timer = Optional.ofNullable(
                metricRegistry.timer(name, () -> new Timer(new SlidingTimeWindowArrayReservoir(1, TimeUnit.MINUTES))));
        }
        return timer.get();
    }

    public Context time() {
        return new Context(getTimer().time());
    }

    public static final class Context {

        private final Timer.Context context;

        public Context(Timer.Context context) {
            this.context = context;
        }

        public long stop() {
            return context.stop();
        }
    }
}
