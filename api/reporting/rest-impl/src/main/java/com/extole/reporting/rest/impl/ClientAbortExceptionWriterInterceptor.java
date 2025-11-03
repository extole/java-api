package com.extole.reporting.rest.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Priority;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.io.output.ProxyOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Based on answer from https://stackoverflow.com/questions/26752880/clientabortexception-when-using-jersey-2-13
@Provider
@Priority(1)
public class ClientAbortExceptionWriterInterceptor implements WriterInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(ClientAbortExceptionWriterInterceptor.class);
    @Context
    private UriInfo uriInfo;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        context.setOutputStream(new ClientAbortExceptionOutputStream(context.getOutputStream()));
        try {
            context.proceed();
        } catch (Throwable t) {
            for (Throwable cause = t; cause != null; cause = cause.getCause()) {
                if (cause instanceof ClientAbortException) {
                    LOG.warn("Client cancelled download/read of response to {}", uriInfo.getRequestUri(), cause);
                    return;
                }
            }
            throw t;
        }
    }

    private static class ClientAbortExceptionOutputStream extends ProxyOutputStream {
        ClientAbortExceptionOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        protected void handleIOException(IOException e) throws IOException {
            throw new ClientAbortException(e);
        }
    }

    private static class ClientAbortException extends IOException {
        ClientAbortException(IOException e) {
            super(e);
        }
    }
}
