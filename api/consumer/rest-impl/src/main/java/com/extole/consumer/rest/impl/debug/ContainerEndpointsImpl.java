package com.extole.consumer.rest.impl.debug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.debug.ContainerEndpoints;
import com.extole.consumer.rest.debug.ContainerRestException;
import com.extole.consumer.service.zone.ZoneRenderRequest;

@Provider
public class ContainerEndpointsImpl implements ContainerEndpoints {
    private static final Pattern CONTAINER_PATTERN = Pattern.compile("[0-9a-zA-Z-]+");
    private static final String ROOT_PATH = "/";
    private static final int MAX_COOKIE_AGE_SECONDS = 86400;
    private final HttpServletRequest servletRequest;

    @Autowired
    public ContainerEndpointsImpl(@Context HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Override
    public Response setEnvironmentCookie(String container, boolean enable, Cookie cookie)
        throws ContainerRestException {
        Response.ResponseBuilder responseBuilder = Response.ok("OK");
        if (enable) {
            Matcher matcher = CONTAINER_PATTERN.matcher(container);
            if (matcher.matches()) {
                ExtoleCookie responseCookie =
                    new ExtoleCookie(ZoneRenderRequest.ZONE_COOKIE_CONTAINER, container, ROOT_PATH,
                        servletRequest.getServerName(), null, MAX_COOKIE_AGE_SECONDS);
                responseCookie.addCookieToResponse(responseBuilder);
            } else {
                throw RestExceptionBuilder.newBuilder(ContainerRestException.class)
                    .withErrorCode(ContainerRestException.INVALID_CONTAINER).build();
            }
        } else {
            ExtoleCookie responseCookie =
                new ExtoleCookie(ZoneRenderRequest.ZONE_COOKIE_CONTAINER, "", ROOT_PATH,
                    servletRequest.getServerName(), null, 0);
            responseCookie.addCookieToResponse(responseBuilder);
        }

        return responseBuilder.build();
    }

}
