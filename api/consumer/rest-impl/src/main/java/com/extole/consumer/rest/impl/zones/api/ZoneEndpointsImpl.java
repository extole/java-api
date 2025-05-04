package com.extole.consumer.rest.impl.zones.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.date.ExtoleTimeModule;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.zones.api.RenderZoneEventRestException;
import com.extole.consumer.rest.zones.api.RenderZoneRequest;
import com.extole.consumer.rest.zones.api.UnnamedRenderZoneRequest;
import com.extole.consumer.rest.zones.api.ZoneEndpoints;
import com.extole.consumer.rest.zones.api.ZoneResponse;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneEventResult;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.consumer.service.zone.ZoneRenderService;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.event.consumer.step.StepConsumerEvent;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;

@Provider
public class ZoneEndpointsImpl implements ZoneEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ZoneEndpointsImpl.class);

    private static final Pattern EVENT_PATTERN = Pattern.compile("[0-9A-Za-z_.-]+");
    private static final String JWT_ENTRY_KEY = "jwt";
    private static final String ID_TOKEN_ENTRY_KEY = "id_token";

    private final ConsumerRequestContextService consumerRequestContextService;
    private final ZoneRenderService zoneRenderService;
    private final HttpServletRequest servletRequest;
    private final HttpHeaders httpHeaders;
    private final HttpServletResponse servletResponse;
    private final ObjectMapper objectMapper;

    @Inject
    public ZoneEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        ZoneRenderService zoneRenderService,
        @Context HttpServletRequest servletRequest,
        @Context HttpHeaders httpHeaders,
        @Context HttpServletResponse servletResponse) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.zoneRenderService = zoneRenderService;
        this.servletRequest = servletRequest;
        this.httpHeaders = httpHeaders;
        this.servletResponse = servletResponse;
        this.objectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new ExtoleTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public ZoneResponse render(String accessToken, RenderZoneRequest request)
        throws AuthorizationRestException, RenderZoneEventRestException {
        if (Strings.isNullOrEmpty(request.getEventName())) {
            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.MISSING_ZONE_NAME)
                .build();
        }
        if (!EVENT_PATTERN.matcher(request.getEventName()).matches()) {
            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.INVALID_ZONE_NAME)
                .build();
        }

        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(request.getEventName())
            .withHttpHeaders(httpHeaders)
            .withEventProcessing(configurator -> {
                request.getData().forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
                request.getJwt().ifPresent(jwt -> configurator.addJwt(jwt, EventData.Source.REQUEST_BODY));
                request.getIdToken()
                    .ifPresent(idToken -> configurator.addIdToken(idToken, EventData.Source.REQUEST_BODY));
            })
            .build();

        ZoneRenderResponse renderResponse = zoneRenderService.render(requestContext.getAuthorization(),
            requestContext.getAuthorization().getIdentity(), requestContext.getProcessedRawEvent(),
            requestContext.getPerformanceLogMessages(), ZoneMetrics.ZONE_API_DURATION, Optional.empty());

        Multimap<String, String> responseHeaders = renderResponse.getHeaders();
        addResponseHeaders(servletResponse, responseHeaders);

        if (!hasStepEvent(renderResponse)) {
            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.NO_CREATIVE)
                .build();
        }

        Optional<String> redirectLocation = responseHeaders.get(ZoneRenderResponse.HEADER_LOCATION)
            .stream().findFirst();
        if (redirectLocation.isPresent()) {
            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.UNEXPECTED_REDIRECT)
                .addParameter("redirect_url", redirectLocation.get())
                .build();
        }

        Id<? extends ConsumerEvent> eventId = extractEventId(requestContext, renderResponse);

        String contentType = responseHeaders.get(ZoneRenderResponse.HEADER_CONTENT_TYPE)
            .stream().findFirst().orElse(MediaType.APPLICATION_OCTET_STREAM);
        if (!MediaType.APPLICATION_JSON.equals(contentType)) {
            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.UNEXPECTED_CONTENT_TYPE)
                .addParameter("contentType", contentType)
                .addParameter("event_id", eventId)
                .addParameter("accept_headers",
                    requestContext.getProcessedRawEvent().getRawEvent().getHttpHeaderValues("accept"))
                .addParameter("content", new String(renderResponse.getBody(), StandardCharsets.UTF_8))
                .build();
        }

        Optional<StepConsumerEvent> stepEvent = renderResponse.getZoneEvents().stream()
            .map(ZoneEventResult::getStepEvent).filter(Optional::isPresent).map(Optional::get).findFirst();
        try {
            JsonNode jsonNode = objectMapper.readTree(renderResponse.getBody());
            Map result = objectMapper.convertValue(jsonNode, Map.class);
            return new ZoneResponse(eventId.getValue(), result,
                stepEvent.flatMap(step -> step.getSelectedCampaignContext()
                    .map(campaign -> Id.valueOf(campaign.getCampaignId().getValue()))));
        } catch (IllegalArgumentException | IOException e) {
            Id<ClientHandle> clientId = requestContext.getAuthorization().getClientId();
            Optional<Id<CampaignHandle>> campaignId =
                stepEvent.flatMap(event -> event.getSelectedCampaignContext().map(context -> context.getCampaignId()));
            Optional<String> sandbox = stepEvent.map(event -> event.getSandbox().getSandboxId());
            Optional<?> stepEventId = stepEvent.map(event -> event.getId());

            LOG.warn("Invalid creative result for clientId={}, eventId={}, zoneName={},"
                + " campaignId={}, sandbox={}, stepEventId={}",
                clientId, eventId, request.getEventName(), campaignId, sandbox, stepEventId, e);

            throw RestExceptionBuilder.newBuilder(RenderZoneEventRestException.class)
                .withErrorCode(RenderZoneEventRestException.INVALID_CREATIVE_RESULT)
                .addParameter("event_id", eventId)
                .addParameter("content", new String(renderResponse.getBody(), StandardCharsets.UTF_8))
                .withCause(e).build();
        }
    }

    @Override
    public ZoneResponse render(String accessToken, String eventName,
        Optional<UnnamedRenderZoneRequest> renderZoneRequest)
        throws AuthorizationRestException, RenderZoneEventRestException {
        Map<String, Object> zoneRenderData = renderZoneRequest
            .map(unnamedRenderZoneRequest -> new HashMap<>(unnamedRenderZoneRequest.getData())).orElseGet(HashMap::new);
        Object jwt = zoneRenderData.remove(JWT_ENTRY_KEY);
        Object idToken = zoneRenderData.remove(ID_TOKEN_ENTRY_KEY);
        return render(accessToken, RenderZoneRequest.builder()
            .withEventName(eventName)
            .withJwt(jwt != null ? jwt.toString() : null)
            .withIdToken(idToken != null ? idToken.toString() : null)
            .withData(zoneRenderData)
            .build());
    }

    private static void addResponseHeaders(HttpServletResponse servletResponse,
        Multimap<String, String> responseHeaders) {
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_LOG);
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_CAMPAIGN);
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_FRONTEND_CONTROLLER_ID);
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_CREATIVE_ACTION_ID);
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_CREATIVE);
        addResponseHeaders(servletResponse, responseHeaders, ZoneRenderResponse.HEADER_EXTOLE_CREATIVE_VERSION);
    }

    private static void addResponseHeaders(HttpServletResponse servletResponse,
        Multimap<String, String> responseHeaders, String headerName) {
        if (responseHeaders.containsKey(headerName)) {
            responseHeaders.get(headerName).forEach(value -> servletResponse.addHeader(headerName, value));
        }
    }

    private static Id<? extends ConsumerEvent> extractEventId(ConsumerRequestContext requestContext,
        ZoneRenderResponse renderResponse) {
        Optional<Id<? extends ConsumerEvent>> eventId =
            renderResponse.getZoneEvents().stream().findFirst().map(value -> value.getInputEvent().getId());
        if (eventId.isPresent()) {
            return eventId.get();
        }
        LOG.error("Zone response for client_id={} has not event information, context: {}",
            requestContext.getAuthorization().getClientId(), requestContext);
        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build();
    }

    private static boolean hasStepEvent(ZoneRenderResponse renderResponse) {
        return renderResponse.getZoneEvents().stream()
            .map(ZoneEventResult::getStepEvent).filter(Optional::isPresent).map(Optional::get).findFirst().isPresent();
    }
}
