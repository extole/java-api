package com.extole.common.rest.support.jackson;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@javax.ws.rs.ext.Provider
@Consumes(MediaType.WILDCARD) // NOTE: required to support "non-standard" JSON variants
@Produces(MediaType.WILDCARD)
public final class ExtoleJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {

    @Inject
    public ExtoleJacksonJaxbJsonProvider(Provider<ObjectMapper> objectMapperProviderContextResolver) {
        super(objectMapperProviderContextResolver.get(),
            JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }
}
