package com.extole.common.rest.support.request.body.validator;

import java.lang.reflect.InvocationHandler;

import javax.inject.Inject;

import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class OptionalRequestBodyParamHandlerResourceProvider implements ResourceMethodInvocationHandlerProvider {

    private final ExtoleMetricRegistry metricRegistry;

    @Inject
    public OptionalRequestBodyParamHandlerResourceProvider(ExtoleMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public InvocationHandler create(org.glassfish.jersey.server.model.Invocable method) {
        return new OptionalRequestBodyParamHandler(metricRegistry);
    }
}
