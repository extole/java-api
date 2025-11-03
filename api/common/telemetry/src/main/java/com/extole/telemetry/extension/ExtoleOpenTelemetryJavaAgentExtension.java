package com.extole.telemetry.extension;

import java.util.List;
import java.util.logging.Logger;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

import com.extole.telemetry.sampling.DebugQueryParamSampler;
import com.extole.telemetry.sampling.ExtolePrimaryCompositeSampler;

@AutoService(AutoConfigurationCustomizerProvider.class)
public class ExtoleOpenTelemetryJavaAgentExtension implements AutoConfigurationCustomizerProvider {

    private static final Logger LOGGER = Logger.getLogger(ExtoleOpenTelemetryJavaAgentExtension.class.getName());

    static {
        LOGGER.info("ExtoleOpenTelemetryJavaAgentExtension class loaded successfully");
    }

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        LOGGER.info("ExtoleOpenTelemetryJavaAgentExtension.customize() called - applying custom sampler configuration");
        autoConfiguration.addSamplerCustomizer((defaultSampler, properties) -> {
            LOGGER.info("Creating Extole sampler with custom sampling configuration");
            return createExtoleSampler(defaultSampler);
        });
    }

    private static Sampler createExtoleSampler(Sampler defaultSampler) {
        LOGGER.info("Creating ExtolePrimaryCompositeSampler with DebugQueryParamSampler, and default sampler");
        DebugQueryParamSampler debugQueryParamSampler = new DebugQueryParamSampler();
        return Sampler.parentBased(new ExtolePrimaryCompositeSampler(List.of(debugQueryParamSampler, defaultSampler)));
    }
}
