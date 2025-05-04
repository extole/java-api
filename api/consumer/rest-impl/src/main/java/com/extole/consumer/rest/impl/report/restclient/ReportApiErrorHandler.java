package com.extole.consumer.rest.impl.report.restclient;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.model.RestExceptionResponse;

public final class ReportApiErrorHandler implements ClientResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ReportApiErrorHandler.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        ((ClientResponse) responseContext).bufferEntity(); // ensures repeatable reads

        if (hasClientError(responseContext.getStatusInfo())) {
            String error = IOUtils.toString(responseContext.getEntityStream(), Charset.defaultCharset());
            RestExceptionResponse exceptionResponse =
                ((ClientResponse) responseContext).readEntity(RestExceptionResponse.class);
            LOG.debug("HTTP {} {}. Error {}", Integer.valueOf(responseContext.getStatus()),
                responseContext.getStatusInfo(), error);
            throw new ReportRestRuntimeException(exceptionResponse);
        }
    }

    private boolean hasClientError(Response.StatusType statusType) {
        return statusType.getFamily().equals(Response.Status.Family.CLIENT_ERROR);
    }

}
