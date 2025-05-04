package com.extole.consumer.rest.impl.web.events;

import static com.extole.consumer.rest.web.zone.ZoneWebConstants.ZONE_ID_PARAMETER_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.ExtoleHeaderType;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService.ConsumerRequestType;
import com.extole.consumer.rest.impl.web.zone.PersonLockAcquireException;
import com.extole.consumer.rest.impl.web.zone.WebZoneRenderResponse;
import com.extole.consumer.rest.impl.web.zone.WebZoneRenderer;
import com.extole.consumer.rest.impl.web.zones.ZonesEndpointsImpl;
import com.extole.consumer.rest.web.events.EventEndpoints;
import com.extole.consumer.rest.web.events.SubmitEventRequest;
import com.extole.consumer.rest.web.events.SubmitEventResponse;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.id.IdGenerator;

@Provider
public class EventEndpointsImpl implements EventEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ZonesEndpointsImpl.class);
    private static final IdGenerator ID_GENERATOR = new IdGenerator();
    private static final Pattern EVENT_PATTERN = Pattern.compile("[0-9A-Za-z_.-]+");

    private static final String NO_EVENT_NAME_ERROR = "No event name";
    private static final String EVENT_NAME_INVALID = "Event name not valid";

    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final HttpHeaders httpHeaders;
    private final UriInfo uriInfo;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final WebZoneRenderer webZoneRenderer;

    @Inject
    public EventEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse,
        @Context HttpHeaders httpHeaders,
        @Context UriInfo uriInfo,
        ConsumerRequestContextService consumerRequestContextService,
        WebZoneRenderer webZoneRenderer) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.httpHeaders = httpHeaders;
        this.uriInfo = uriInfo;
        this.consumerRequestContextService = consumerRequestContextService;
        this.webZoneRenderer = webZoneRenderer;
    }

    @Override
    public SubmitEventResponse post(String accessToken, SubmitEventRequest submitEventRequest) {
        return submit(accessToken, submitEventRequest);
    }

    @Override
    public SubmitEventResponse postFormEncoded(String accessToken, SubmitEventRequest submitEventRequest) {
        return submit(accessToken, submitEventRequest);
    }

    @Override
    public SubmitEventResponse fetch(String accessToken, String eventName) {
        return submit(accessToken, SubmitEventRequest.builder().withEventName(eventName).build());
    }

    @Override
    public SubmitEventResponse fetch(String accessToken) {
        return buildErrorResponse(accessToken, NO_EVENT_NAME_ERROR);
    }

    private SubmitEventResponse submit(String accessToken, SubmitEventRequest submitEventRequest) {
        if (Strings.isNullOrEmpty(submitEventRequest.getEventName())) {
            return buildErrorResponse(accessToken, NO_EVENT_NAME_ERROR);
        }
        if (!EVENT_PATTERN.matcher(submitEventRequest.getEventName()).matches()) {
            return buildErrorResponse(accessToken, EVENT_NAME_INVALID);
        }

        // TODO ENG-9515 will remove zone_id, and x-zone-id
        Map<String, String> parameters = new HashMap<>();
        if (!uriInfo.getQueryParameters().containsKey(ZONE_ID_PARAMETER_NAME)) {
            parameters.put("x-zone-id", ID_GENERATOR.generateId().getValue());
        }

        ConsumerRequestContext requestContext;
        try {
            requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withConsumerRequestType(ConsumerRequestType.WEB)
                .withReplaceableAccessToken(accessToken)
                .withHttpHeaders(httpHeaders)
                .withUriInfo(uriInfo)
                .withEventName(submitEventRequest.getEventName())
                .withEventProcessing(configurator -> {
                    parameters.forEach((key, value) -> {
                        configurator
                            .addData(new EventData(key, value, EventData.Source.REQUEST_QUERY_PARAMETER, false, true));
                    });
                    submitEventRequest.getData().forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                    });
                    submitEventRequest.getJwt()
                        .ifPresent(jwt -> configurator.addJwt(jwt, EventData.Source.REQUEST_BODY));
                    submitEventRequest.getIdToken()
                        .ifPresent(idToken -> configurator.addIdToken(idToken, EventData.Source.REQUEST_BODY));
                })
                .build();
        } catch (AuthorizationRestException e) {
            String message = ID_GENERATOR.generateId().toString()
                + " Software error building request context for submit event request " + servletRequest.getRequestURI();
            LOG.error(message, e);
            return buildErrorResponse(accessToken, message);
        }

        try {
            return buildResponse(requestContext,
                webZoneRenderer.render(requestContext, servletRequest, ZoneMetrics.EVENT_WEB_DURATION));
        } catch (PersonLockAcquireException e) {
            throw e.getCause();
        } catch (Exception e) {
            // Catching all exceptions here as web requests must always return 200
            String message = ID_GENERATOR.generateId().toString() + " Software error submitting event "
                + submitEventRequest.getEventName();
            LOG.error(message + " with request context {}", requestContext, e);
            return buildErrorResponse(requestContext.getAuthorization().getAccessToken(), message);
        }
    }

    private SubmitEventResponse buildResponse(ConsumerRequestContext requestContext, WebZoneRenderResponse response) {
        String accessToken = requestContext.getAuthorization().getAccessToken();
        // TODO this is always null, to be fixed in https://extole.atlassian.net/browse/ENG-12108
        String cookieConsent =
            response.getWebResponse().getHeaderString(ExtoleHeaderType.COOKIE_CONSENT.getHeaderName());
        String errorMessage =
            response.getWebResponse().getHeaderString(ExtoleHeaderType.ERROR_MESSAGE.getHeaderName());
        if (!Strings.isNullOrEmpty(cookieConsent)) {
            servletResponse.addHeader(ExtoleHeaderType.COOKIE_CONSENT.getHeaderName(), cookieConsent);
        }
        if (!Strings.isNullOrEmpty(errorMessage)) {
            servletResponse.addHeader(ExtoleHeaderType.ERROR_MESSAGE.getHeaderName(), errorMessage);
        }

        MultivaluedMap<String, Object> logHeaders = response.getWebResponse().getHeaders();
        logHeaders.entrySet().stream()
            .filter(entry -> entry.getKey().equalsIgnoreCase(ZoneRenderResponse.HEADER_EXTOLE_LOG))
            .map(Entry::getValue)
            .flatMap(List::stream)
            .forEach(header -> servletResponse.addHeader(ZoneRenderResponse.HEADER_EXTOLE_LOG, header.toString()));

        Optional<ZoneRenderResponse> zoneResponse = response.getZoneRenderResponse();
        return !zoneResponse.isPresent() ? SubmitEventResponse.builder().build()
            : SubmitEventResponse.builder()
                .withId(zoneResponse.get().getZoneEvents().stream().findFirst()
                    .map(value -> value.getInputEvent().getId().getValue()).orElse(null))
                .withToken(accessToken)
                .withCookieConsent(cookieConsent)
                .build();
    }

    private SubmitEventResponse buildErrorResponse(String accessToken, String message) {
        servletResponse.addHeader(ExtoleHeaderType.ERROR_MESSAGE.getHeaderName(), message);
        return SubmitEventResponse.builder().build();
    }
}
