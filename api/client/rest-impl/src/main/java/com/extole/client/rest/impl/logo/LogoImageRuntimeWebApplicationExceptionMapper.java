package com.extole.client.rest.impl.logo;

import java.io.ByteArrayInputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class LogoImageRuntimeWebApplicationExceptionMapper
    implements ExceptionMapper<LogoImageRuntimeWebApplicationException> {

    private static final Logger LOG = LoggerFactory.getLogger(LogoImageRuntimeWebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(LogoImageRuntimeWebApplicationException exception) {
        LOG.debug("Error during logo download", exception);
        return Response.status(exception.getStatusCode()).entity(new ByteArrayInputStream(new byte[0]))
            .type("image/png").build();
    }
}
