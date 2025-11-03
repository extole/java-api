package com.extole.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class ExtoleTaskScheduler extends ThreadPoolTaskScheduler implements StopFirst {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleTaskScheduler.class);

    private static final int AWAIT_TERMINATION_SECONDS = 60;

    public ExtoleTaskScheduler() {
        setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        setThreadNamePrefix(ExtoleTaskScheduler.class.getSimpleName());
    }

    @Override
    public void stop() {
        LOG.warn("Stopping ExtoleTaskScheduler");
        destroy();
    }
}
