package com.extole.consumer.rest.impl.web.zone;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.ExtoleHeaderType;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService.ConsumerRequestType;
import com.extole.consumer.rest.web.zone.ZoneEndpoints;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.id.DeprecatedIdGenerator;

@Provider
public class ZoneEndpointsImpl implements ZoneEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ZoneEndpointsImpl.class);

    private final HttpServletRequest servletRequest;
    private final UriInfo uriInfo;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final WebZoneRenderer webZoneRenderer;
    private final DeprecatedIdGenerator<Long> idGenerator;

    @Inject
    public ZoneEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        @Context UriInfo uriInfo,
        ConsumerRequestContextService consumerRequestContextService,
        WebZoneRenderer webZoneRenderer,
        DeprecatedIdGenerator<Long> idGenerator) {
        this.servletRequest = servletRequest;
        this.uriInfo = uriInfo;
        this.consumerRequestContextService = consumerRequestContextService;
        this.webZoneRenderer = webZoneRenderer;
        this.idGenerator = idGenerator;
    }

    @Override
    public Response render(String accessToken, String zoneName) throws AuthorizationRestException {
        ConsumerRequestContext requestContext;
        try {
            requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withConsumerRequestType(ConsumerRequestType.WEB)
                .withReplaceableAccessTokenBasedOnCoreSettings(accessToken)
                .withUriInfo(uriInfo)
                .withEventName(zoneName)
                .build();
        } catch (AuthorizationRestException e) {
            if (e.getErrorCode().equals(AuthorizationRestException.ACCESS_TOKEN_EXPIRED.getName()) ||
                e.getErrorCode().equals(AuthorizationRestException.ACCESS_TOKEN_INVALID.getName()) ||
                e.getErrorCode().equals(AuthorizationRestException.JWT_AUTHENTICATION_FAILED.getName())) {
                throw e;
            }
            String message = idGenerator.generateId().toString()
                + " Software error building request context for zone request " + servletRequest.getRequestURI();
            LOG.error(message, e);
            return Response.ok()
                .header(ExtoleHeaderType.ERROR_MESSAGE.getHeaderName(), '"' + message + '"')
                .build();
        }
        try {
            return webZoneRenderer.render(requestContext, servletRequest, ZoneMetrics.ZONE_WEB_DURATION)
                .getWebResponse();
        } catch (PersonLockAcquireException e) {
            throw e.getCause();
        }
    }

    @Override
    public Response render(String accessToken) {
        return Response.ok()
            .header(ZoneRenderResponse.HEADER_EXTOLE_LOG, "No zone name")
            .build();
    }
}
