package com.extole.common.rest.support.request.body.validator;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

public class RequestBodyFeature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(OptionalRequestBodyParamHandlerResourceProvider.class)
                    .to(ResourceMethodInvocationHandlerProvider.class);
            }
        });
        return true;
    }
}
