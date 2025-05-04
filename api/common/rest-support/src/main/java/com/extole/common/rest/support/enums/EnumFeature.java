package com.extole.common.rest.support.enums;

import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.ParamConverterProvider;

import org.glassfish.jersey.internal.inject.AbstractBinder;

public class EnumFeature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        context.register(EnumParamConverterProvider.class);
        context.register(EnumParamExceptionMapper.class);
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(EnumParamConverterProvider.class)
                    .to(ParamConverterProvider.class)
                    .in(Singleton.class);
            }
        });
        return true;
    }
}
