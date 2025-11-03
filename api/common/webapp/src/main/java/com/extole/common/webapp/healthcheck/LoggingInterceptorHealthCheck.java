package com.extole.common.webapp.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.stereotype.Component;

@Component
public class LoggingInterceptorHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        if (LoggingInterceptor.IS_HEALTHY.get()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Log interceptor has triggered");
        }
    }
}
