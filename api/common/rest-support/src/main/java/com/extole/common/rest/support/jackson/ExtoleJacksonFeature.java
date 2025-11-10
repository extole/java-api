package com.extole.common.rest.support.jackson;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ExtoleJacksonFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(ObjectMapperFactory.class).to(ObjectMapper.class);
            }
        });
        context.register(ExtoleJacksonJaxbJsonProvider.class);
        context.register(ZonedDateTimeDeserializationExceptionMapper.class);
        return true;
    }
}
