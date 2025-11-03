package com.extole.telemetry.config;

import io.opentelemetry.api.OpenTelemetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.telemetry.ExtoleOpenTelemetryFactory;
import com.extole.telemetry.TelemetryConfigMarker;

@Configuration
@ComponentScan(basePackageClasses = {TelemetryConfigMarker.class})
public class TelemetrySpringConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        return ExtoleOpenTelemetryFactory.globalInstance();
    }
}
