package com.extole.telemetry;

import java.util.concurrent.atomic.AtomicReference;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;

public final class ExtoleOpenTelemetryFactory {

    private static final AtomicReference<OpenTelemetry> OPEN_TELEMETRY = new AtomicReference<>();

    private ExtoleOpenTelemetryFactory() {
    }

    public static OpenTelemetry globalInstance() {
        if (OPEN_TELEMETRY.get() != null) {
            return OPEN_TELEMETRY.get();
        }
        OPEN_TELEMETRY.compareAndSet(null, GlobalOpenTelemetry.get());
        return OPEN_TELEMETRY.get();
    }
}
