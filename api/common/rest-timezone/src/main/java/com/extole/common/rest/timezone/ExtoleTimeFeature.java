package com.extole.common.rest.timezone;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import com.extole.common.rest.time.TimeZoneParam;
import com.extole.model.shared.client.ClientCache;

public final class ExtoleTimeFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(TimeZoneParamValueParamProvider.class);
        context.register(TimeZoneParamExceptionMapper.class);
        context.register(TimeParamConverterProviders.class);
        context.register(ZonedDateTimeParamExceptionMapper.class);
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                Provider<MultivaluedParameterExtractorProvider> extractorProvider =
                    createManagedInstanceProvider(MultivaluedParameterExtractorProvider.class);
                Provider<ClientCache> clientCache = createManagedInstanceProvider(ClientCache.class);
                Provider<ContainerRequest> request = createManagedInstanceProvider(ContainerRequest.class);

                TimeZoneParamValueParamProvider valueParamProvider =
                    new TimeZoneParamValueParamProvider(extractorProvider, clientCache);

                bind(Bindings.service(valueParamProvider).to(ValueParamProvider.class));
                bind(Bindings.injectionResolver(
                    new ParamInjectionResolver<>(valueParamProvider, TimeZoneParam.class, request)));
            }
        });
        return true;
    }
}
