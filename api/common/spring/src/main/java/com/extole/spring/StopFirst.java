package com.extole.spring;

import org.springframework.context.SmartLifecycle;

public interface StopFirst extends SmartLifecycle {
    @Override
    default int getPhase() {
        return Integer.MAX_VALUE;
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
    void stop();
}
