package com.extole.consumer.rest.impl.web.zones;

import static com.extole.consumer.rest.web.zone.ZoneWebConstants.ZONE_ID_PARAMETER_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService.ConsumerRequestType;
import com.extole.consumer.rest.impl.web.zone.PersonLockAcquireException;
import com.extole.consumer.rest.impl.web.zone.WebZoneRenderer;
import com.extole.consumer.rest.web.zones.RenderZoneRequest;
import com.extole.consumer.rest.web.zones.UnnamedRenderZoneRequest;
import com.extole.consumer.rest.web.zones.ZonesEndpoints;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.id.Id;
import com.extole.id.IdGenerator;

@Provider
public class ZonesEndpointsImpl implements ZonesEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ZonesEndpointsImpl.class);
    private static final IdGenerator ID_GENERATOR = new IdGenerator();
    private static final Pattern EVENT_PATTERN = Pattern.compile("[0-9A-Za-z_.-]+");
    private static final Id<ClientHandle> ZOOM_CLIENT_ID = Id.valueOf("841133454");

    private static final String ZONE_NAME_INVALID = "Zone name not valid";

    private static final String JWT_ENTRY_KEY = "jwt";
    private static final String ID_TOKEN_ENTRY_KEY = "id_token";

    private final HttpServletRequest servletRequest;
    private final HttpHeaders httpHeaders;
    private final UriInfo uriInfo;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final WebZoneRenderer webZoneRenderer;

    @Inject
    public ZonesEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        @Context HttpHeaders httpHeaders,
        @Context UriInfo uriInfo,
        ConsumerRequestContextService consumerRequestContextService,
        WebZoneRenderer webZoneRenderer) {
        this.servletRequest = servletRequest;
        this.httpHeaders = httpHeaders;
        this.uriInfo = uriInfo;
        this.consumerRequestContextService = consumerRequestContextService;
        this.webZoneRenderer = webZoneRenderer;
    }

    @Override
    public Response fetch(String accessToken, String zoneName) throws AuthorizationRestException {
        return render(accessToken, RenderZoneRequest.builder().withZoneName(zoneName).build());
    }

    @Override
    public Response fetch(String accessToken) throws AuthorizationRestException {
        return render(accessToken, RenderZoneRequest.builder().build());
    }

    @Override
    public Response post(String accessToken, Optional<RenderZoneRequest> renderZoneRequest)
        throws AuthorizationRestException {
        return render(accessToken, renderZoneRequest.orElseGet(() -> RenderZoneRequest.builder().build()));
    }

    @Override
    public Response post(String accessToken, String zoneName, Optional<UnnamedRenderZoneRequest> request)
        throws AuthorizationRestException {
        return render(accessToken, mapToRenderZoneRequest(zoneName, request));
    }

    private Response render(String accessToken, RenderZoneRequest request) throws AuthorizationRestException {
        if (request.getEventName().isPresent() && StringUtils.isNotBlank(request.getEventName().get())
            && !EVENT_PATTERN.matcher(request.getEventName().get()).matches()) {
            return buildErrorResponse(accessToken, ZONE_NAME_INVALID);
        }

        // TODO T3-1189 remove on zoom churning or re-signing
        if (ZOOM_CLIENT_ID.getValue()
            .equals(servletRequest.getAttribute(RequestContextAttributeName.CLIENT_ID.getAttributeName()))) {
            return Response.ok().build();
        }

        // TODO ENG-9515 will remove zone_id, and x-zone-id
        Map<String, String> parameters = new HashMap<>();
        if (!uriInfo.getQueryParameters().containsKey(ZONE_ID_PARAMETER_NAME)) {
            parameters.put("x-zone-id", ID_GENERATOR.generateId().getValue());
        }

        ConsumerRequestContext requestContext;
        try {
            ConsumerRequestContextService.ConsumerRequestContextBuilder requestContextBuilder =
                consumerRequestContextService.createBuilder(servletRequest)
                    .withConsumerRequestType(ConsumerRequestType.WEB)
                    .withReplaceableAccessTokenBasedOnCoreSettings(accessToken)
                    .withHttpHeaders(httpHeaders)
                    .withUriInfo(uriInfo)
                    .withEventProcessing(configurator -> {
                        parameters.forEach((key, value) -> {
                            configurator.addData(
                                new EventData(key, value, EventData.Source.REQUEST_QUERY_PARAMETER, false, true));
                        });
                        request.getData().forEach((key, value) -> {
                            configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                        });
                        request.getJwt()
                            .ifPresent(jwt -> configurator.addJwt(jwt, EventData.Source.REQUEST_BODY));
                        request.getIdToken().ifPresent(
                            idToken -> configurator.addIdToken(idToken, EventData.Source.REQUEST_BODY));
                    });

            if (request.getEventName().isPresent() && StringUtils.isNotBlank(request.getEventName().get())) {
                requestContextBuilder.withEventName(request.getEventName().get());
            }

            requestContext = requestContextBuilder.build();
        } catch (AuthorizationRestException e) {
            if (e.getErrorCode().equals(AuthorizationRestException.ACCESS_TOKEN_EXPIRED.getName()) ||
                e.getErrorCode().equals(AuthorizationRestException.ACCESS_TOKEN_INVALID.getName()) ||
                e.getErrorCode().equals(AuthorizationRestException.JWT_AUTHENTICATION_FAILED.getName())) {
                throw e;
            }
            String message = ID_GENERATOR.generateId().toString()
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

    private Response buildErrorResponse(String accessToken, String message) {
        return Response.ok()
            .header(ZoneRenderResponse.HEADER_EXTOLE_LOG, message)
            .build();
    }

    private RenderZoneRequest mapToRenderZoneRequest(String zoneName, Optional<UnnamedRenderZoneRequest> request) {
        Map<String, Object> zoneRenderData =
            request.map(value -> new HashMap<>(value.getData())).orElseGet(HashMap::new);
        Optional<String> jwt = Optional.ofNullable(zoneRenderData.remove(JWT_ENTRY_KEY))
            .map(Object::toString);
        Optional<String> idToken =
            Optional.ofNullable(zoneRenderData.remove(ID_TOKEN_ENTRY_KEY))
                .map(Object::toString);

        RenderZoneRequest.Builder builder = RenderZoneRequest.builder();
        jwt.ifPresent(value -> builder.withJwt(value));
        idToken.ifPresent(value -> builder.withIdToken(value));

        return builder
            .withZoneName(zoneName)
            .withData(zoneRenderData)
            .build();
    }

}
