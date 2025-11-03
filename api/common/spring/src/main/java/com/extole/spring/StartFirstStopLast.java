package com.extole.spring;

import org.springframework.context.SmartLifecycle;

public interface StartFirstStopLast extends SmartLifecycle {
    @Override
    default int getPhase() {
        return Integer.MIN_VALUE;
    }

    @Override
    default void start() {
        // Not called by Spring unless isRunning() is false during startup
    }

    @Override
    default boolean isRunning() {
        return true;
    }

    @Override
    default void stop() {
        // Only called by Spring if isRunning() is true during shutdown
    }
}
