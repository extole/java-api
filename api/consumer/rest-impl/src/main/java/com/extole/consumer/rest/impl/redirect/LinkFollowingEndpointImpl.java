package com.extole.consumer.rest.impl.redirect;

import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.InternetDomainName;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.extole.api.step.action.display.ApiResponse;
import com.extole.api.step.action.display.DisplayActionContext;
import com.extole.api.step.action.display.DisplayActionResponseContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.InvalidExpiresAtException;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService.ConsumerRequestType;
import com.extole.consumer.rest.impl.web.zone.PersonLockAcquireException;
import com.extole.consumer.rest.impl.web.zone.WebZoneRenderResponse;
import com.extole.consumer.rest.impl.web.zone.WebZoneRenderer;
import com.extole.consumer.rest.redirect.LinkFollowingEndpoint;
import com.extole.consumer.rest.redirect.LinkFollowingRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderRequest;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.entity.promotion.PromotionLink;
import com.extole.model.service.metrics.MetricConfiguration;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.model.shared.promotion.PromotionLinkCache;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.shareable.Shareable;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.campaign.provider.RunningCampaignService;
import com.extole.running.service.campaign.step.RunningStepType;
import com.extole.running.service.campaign.step.controller.RunningFrontendController;
import com.extole.running.service.campaign.step.controller.action.RunningControllerActionType;
import com.extole.running.service.campaign.step.controller.action.display.RunningControllerActionDisplay;
import com.extole.running.service.campaign.step.trigger.RunningStepTriggerEvent;
import com.extole.running.service.campaign.step.trigger.RunningStepTriggerType;
import com.extole.sandbox.Container;
import com.extole.sandbox.SandboxModel;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;

@Provider
public class LinkFollowingEndpointImpl implements LinkFollowingEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(LinkFollowingEndpointImpl.class);
    private static final Predicate<RunningCampaign> IS_GLOBAL =
        campaign -> campaign.getName().equalsIgnoreCase("global");
    private static final String ZONE_PROMOTE_DESTINATION = "promote_destination";
    private static final String ZONE_SHARE_DESTINATION = "share_destination";
    private static final String EVENT_NAME_CUSTOM_NOT_FOUND = "static-404";
    private static final String ZONE_PARAMETER_PAGE_PREFIX = "extole_";

    private static final String PARAM_SOURCE = "source";
    private static final String PARAM_LABELS = "labels";
    private static final String PARAM_PROMOTABLE_CODE = "promotable_code";
    private static final String PARAM_SHARE_ID = "share_id";
    private static final String PARAM_ADVOCATE_PARTNER_SHARE_ID = "advocate_partner_share_id";
    private static final String PARAM_SHARE_CHANNEL = "share_channel";
    private static final String PARAM_SHAREABLE_ID = "shareable_id";
    private static final String PARAM_ADVOCATE_CODE = "advocate_code";

    private static final String DATA_EXTOLE_DESTINATION = "extole.destination";
    private static final String DATA_EXTOLE_MICROSITE = "extole.microsite";
    private static final String DATA_REDIRECT = "redirect";

    private static final Pattern ZONE_PATTERN = Pattern.compile("/zone/([0-9A-Za-z_.-]+)(\\?|$)");
    private static final TemporalAmount SHORT_LIVED_ACCESS_TOKEN_TTL = Duration.ofSeconds(5);

    private static final String NOT_FOUND_RESPONSE_HTML = "/assets/notFound.html";
    private static final Map<String, String> NOT_FOUND_RESPONSE_ASSETS = ImmutableMap.<String, String>builder()
        .put("/assets/extole-background-tile.png", "image/png")
        .put("/assets/extole-logomark.svg", "image/svg+xml")
        .put("/assets/favicon.ico", "image/x-icon")
        .put("/assets/favicon.png", "image/png")
        .build();
    // TODO remove when we allow easily configurable 404 again with https://extole.atlassian.net/browse/ENG-20924
    private static final Map<String, String> CLIENT_NOT_FOUND_FILES = ImmutableMap.<String, String>builder()
        .put("1350462568", "/assets/princessuk-notFound.html")
        .put("657543814", "/assets/acorns-sandbox-notFound.html")
        .put("1842186254", "/assets/acorns-notFound.html")
        .put("630221252", "/assets/walmartmoneycard-notFound.html")
        .put("832494786", "/assets/go2bank-notFound.html")
        .put("37246", "/assets/discover-notFound.html")
        .put("1685743885", "/assets/turbotax-notFound.html")
        .build();

    private final HttpServletRequest servletRequest;
    private final WebZoneRenderer webZoneRenderer;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAuthorizationService authorizationService;
    private final ProgramDomainCache programCache;
    private final PromotionLinkCache promotionLinkCache;
    private final ShortShareLinkResolver shortShareLinkResolver;
    private final ExtoleMetricRegistry metricRegistry;
    private final MetricConfiguration metricConfiguration;
    private final Response default404Response;
    private final Map<String, Response> client404CustomResponses;
    private final SandboxService sandboxService;
    private final RunningCampaignService runningCampaignService;

    @Inject
    public LinkFollowingEndpointImpl(
        @Context HttpServletRequest servletRequest,
        PersonAuthorizationService authorizationService,
        ProgramDomainCache programCache,
        WebZoneRenderer webZoneRenderer,
        ConsumerRequestContextService consumerRequestContextService,
        PromotionLinkCache promotionLinkCache,
        ShortShareLinkResolver shortShareLinkResolver,
        ExtoleMetricRegistry metricRegistry,
        MetricConfiguration metricConfiguration,
        SandboxService sandboxService,
        RunningCampaignService runningCampaignService) {
        this.sandboxService = sandboxService;
        this.runningCampaignService = runningCampaignService;
        this.default404Response = get404Response(NOT_FOUND_RESPONSE_HTML);
        ImmutableMap.Builder<String, Response> responseMapBuilder = ImmutableMap.<String, Response>builder();
        for (String clientId : CLIENT_NOT_FOUND_FILES.keySet()) {
            responseMapBuilder.put(clientId, get404Response(CLIENT_NOT_FOUND_FILES.get(clientId)));
        }
        this.client404CustomResponses = responseMapBuilder.build();
        this.servletRequest = servletRequest;
        this.authorizationService = authorizationService;
        this.programCache = programCache;
        this.webZoneRenderer = webZoneRenderer;
        this.consumerRequestContextService = consumerRequestContextService;
        this.promotionLinkCache = promotionLinkCache;
        this.shortShareLinkResolver = shortShareLinkResolver;
        this.metricRegistry = metricRegistry;
        this.metricConfiguration = metricConfiguration;
    }

    private Response get404Response(String assetName) {
        try (InputStream assetInputStream = getClass().getResourceAsStream(assetName)) {
            byte[] assetContent = IOUtils.toByteArray(assetInputStream);
            return Response.status(Response.Status.NOT_FOUND)
                .type("text/html")
                .entity(assetContent)
                .header("X-Error-Page", "NOT_FOUND")
                .build();
        } catch (IOException e) {
            LOG.error("Unable to initialize NOT_FOUND response asset: " + assetName, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response fetch(String accessToken, String incomingUrl, String incomingHost, String incomingPath)
            throws LinkFollowingRestException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Optional<Id<ClientHandle>> clientId = Optional.empty();
        URI incomingRequest;
        String finalIncomingUrl = "";
        if (incomingPath != null && !incomingPath.trim().isEmpty()) {
            finalIncomingUrl = "https://" + incomingHost + incomingPath;
        }

        try {
            try {
                if (finalIncomingUrl.isEmpty()) {
                    URI.create(incomingUrl);
                    incomingRequest = UriComponentsBuilder.fromHttpUrl(incomingUrl).build().toUri();
                } else {
                    incomingRequest = UriComponentsBuilder.fromHttpUrl(finalIncomingUrl).build().toUri();
                }

                if (incomingRequest.getPath().endsWith("/")) {
                    incomingRequest = UriComponentsBuilder.fromUri(incomingRequest)
                        .replacePath(incomingRequest.getPath().replaceFirst("/*$", "")).build().toUri();
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(LinkFollowingRestException.class)
                    .withErrorCode(LinkFollowingRestException.INVALID_URI)
                    .addParameter("incomingUrl", incomingUrl)
                    .withCause(e).build();
            }

            Optional<PublicProgram> program = lookupProgramFromRequest(incomingRequest);
            clientId = program.map(PublicProgram::getClientId);

            Response response = tryHandleNotFoundResponseAsset(incomingRequest);
            if (response != null) {
                return response;
            }

            if (program.isPresent()) {
                response = tryHandleRedirectProgramRequest(accessToken, incomingRequest);
                if (response != null) {
                    return response;
                }

                response = tryHandlePromotionLinkRequest(accessToken, program.get(), incomingRequest);
                if (response != null) {
                    return response;
                }

                response = tryHandleShareableRequest(accessToken, program.get(), incomingRequest);
                if (response != null) {
                    return response;
                }
                response = client404CustomResponses.get(program.get().getClientId().getValue());
                if (response != null) {
                    return response;
                }
                Optional<RunningControllerActionDisplay> campaignControllerActionDisplay =
                    findGlobalNotFoundDisplayAction(program.get().getClientId());
                if (campaignControllerActionDisplay.isPresent()) {
                    return convertBuiltDisplayActionToNotFoundResponse(campaignControllerActionDisplay.get());
                }
            }
            return default404Response;
        } catch (PersonLockAcquireException e) {
            throw e.getCause();
        } catch (ExtoleRestRuntimeException e) {
            throw e;
        } catch (URISyntaxException | RuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LinkFollowingRestException.class)
                .withErrorCode(LinkFollowingRestException.PROGRAM_NOT_FOUND)
                .addParameter("incomingUrl", incomingUrl)
                .withCause(e)
                .build();
        } finally {
            stopwatch.stop();
            LinkFollowingEndpointMetrics.LINK_FOLLOWING_DURATION.updateHistogram(metricRegistry, metricConfiguration,
                stopwatch.elapsed(TimeUnit.MILLISECONDS), clientId);
        }
    }

    private Response convertBuiltDisplayActionToNotFoundResponse(RunningControllerActionDisplay actionDisplay) {
        String body = StringUtils.EMPTY;
        int statusCode = Response.Status.NOT_FOUND.getStatusCode();
        Map<String, String> headers = emptyMap();
        if (actionDisplay.getResponse() instanceof Provided) {
            ApiResponse apiResponse =
                ((Provided<DisplayActionResponseContext, ApiResponse>) actionDisplay.getResponse())
                    .getValue();
            body = apiResponse.getBody();
            headers = apiResponse.getHeaders();
            statusCode = apiResponse.getStatusCode();
        } else if (actionDisplay.getBody() instanceof Provided && actionDisplay.getHeaders() instanceof Provided) {
            body = ((Provided<DisplayActionContext, String>) actionDisplay.getBody()).getValue();
            headers =
                ((Provided<DisplayActionContext, Map<String, String>>) actionDisplay.getHeaders()).getValue();
        } else {
            LOG.error("Neither headers with body, nor display response is of Provided type");
        }

        Response.ResponseBuilder responseBuilder = Response
            .status(statusCode)
            .entity(body.getBytes())
            .type("text/html")
            .header("X-Error-Page", "NOT_FOUND");

        headers.forEach((key, value) -> responseBuilder.header(key, value));
        return responseBuilder.build();
    }

    private Optional<RunningControllerActionDisplay> findGlobalNotFoundDisplayAction(Id<ClientHandle> clientId) {
        Optional<SandboxModel> sandboxBasedOnRequest = tryToGetSandboxUsingRequest(clientId);
        SandboxModel sandboxModel = sandboxBasedOnRequest.orElseGet(() -> provideDefaultProductionSandbox(clientId));
        Optional<RunningCampaign> runningCampaign = getGlobalCampaignBySandbox(clientId, sandboxModel);
        if (runningCampaign.isEmpty()) {
            return Optional.empty();
        }
        return findDisplayActionByEventName(runningCampaign.get(), EVENT_NAME_CUSTOM_NOT_FOUND);
    }

    private SandboxModel provideDefaultProductionSandbox(Id<ClientHandle> clientId) {
        return sandboxService.getProductionSandbox(clientId, Container.DEFAULT);
    }

    private Optional<SandboxModel> tryToGetSandboxUsingRequest(Id<ClientHandle> clientId) {
        String requestSandboxId = servletRequest.getParameter(ZoneRenderRequest.ZONE_PARAMETER_SANDBOX);
        if (StringUtils.isBlank(requestSandboxId)) {
            return Optional.empty();
        }

        Id<SandboxModel> sandboxId = Id.valueOf(requestSandboxId);
        try {
            SandboxModel explicitSandbox = sandboxService.getById(clientId, sandboxId);
            return Optional.of(explicitSandbox);
        } catch (SandboxNotFoundException e) {
            LOG.error("Was unable to retrieve sandbox for sandbox ID={}", sandboxId, e);
            return Optional.empty();
        }
    }

    private Optional<RunningCampaign> getGlobalCampaignBySandbox(Id<ClientHandle> clientId, SandboxModel sandboxModel) {
        return runningCampaignService.getCampaigns(clientId, sandboxModel)
            .stream()
            .filter(IS_GLOBAL)
            .findAny();
    }

    private Optional<RunningControllerActionDisplay> findDisplayActionByEventName(RunningCampaign runningCampaign,
        String eventName) {

        return runningCampaign
            .getControllers().stream().filter(value -> value.getType() == RunningStepType.FRONTEND_CONTROLLER)
            .map(RunningFrontendController.class::cast)
            .flatMap(controller -> controller.getTriggers().stream()
                .filter(trigger -> trigger.getType() == RunningStepTriggerType.EVENT)
                .map(trigger -> (RunningStepTriggerEvent) trigger)
                .filter(trigger -> trigger.getEventNames().contains(eventName))
                .map(trigger -> Pair.of(controller, trigger)))
            .findFirst()
            .map(pair -> {
                Optional<RunningControllerActionDisplay> action =
                    pair.getLeft().getActions().stream()
                        .filter(value -> value.getType() == RunningControllerActionType.DISPLAY)
                        .map(RunningControllerActionDisplay.class::cast).findFirst();
                return action;
            })
            .flatMap(value -> value);
    }

    @Nullable
    private Response tryHandleRedirectProgramRequest(String accessToken, URI incomingRequest)
        throws URISyntaxException {

        String incomingRequestPath = incomingRequest.getPath();
        if (!Strings.isNullOrEmpty(incomingRequestPath) && incomingRequestPath.startsWith("/api/")) {
            return null;
        }

        PublicProgram program;
        PublicProgram forwardedProgram;
        try {
            if (!InternetDomainName.isValid(incomingRequest.getHost())) {
                LOG.debug("Illegal domain name when trying redirect; request: {}", incomingRequest.toString());
                return null;
            }
            InternetDomainName programDomain = InternetDomainName.from(incomingRequest.getHost());
            program = programCache.getByProgramDomain(programDomain);
            forwardedProgram = programCache.getForwardedById(program.getId(), program.getClientId());
        } catch (ProgramNotFoundException e) {
            LOG.debug("No program found for the incoming request {}", incomingRequest.toString(), e);
            return null;
        }

        if (forwardedProgram.getId().equals(program.getId())) {
            return null;
        }

        URI redirectUrl = new URIBuilder(incomingRequest.toString().replace(incomingRequest.getHost(),
            forwardedProgram.getProgramDomain().toString())).build();

        Optional<PromotionLink> promotionLink = lookupPromotionLink(program, incomingRequest);
        Optional<ShortShareLink> shortShareLink =
            shortShareLinkResolver.resolveShortShareLink(incomingRequestPath, program);

        if (shortShareLink.isPresent() && !promotionLink.isPresent()) {
            redirectUrl =
                new URIBuilder(incomingRequest.toString().replace(incomingRequest.getHost() + incomingRequestPath,
                    forwardedProgram.getProgramDomain() + "/" + shortShareLink.get().getResolvingPath()))
                        .build();
            if (shortShareLink.get().getShareable().isPresent()) {
                String advocateCode = shortShareLink.get().getShareable().get().getCode();
                promotionLink = lookupPromotionLink(program, advocateCode);
                if (promotionLink.isPresent()) {
                    redirectUrl = new URIBuilder(
                        incomingRequest.toString().replace(incomingRequest.getHost() + incomingRequestPath,
                            forwardedProgram.getProgramDomain() + "/" + advocateCode))
                                .build();
                }
            }
        }

        URI destinationUrl =
            new DestinationUrlBuilder().withDestinationUrl(redirectUrl).withIncomingRequest(incomingRequest).build();

        return redirect(accessToken, incomingRequest, destinationUrl);
    }

    @Nullable
    private Response tryHandlePromotionLinkRequest(String accessToken, PublicProgram program, URI incomingRequest)
        throws URISyntaxException, PersonLockAcquireException, ProgramNotFoundException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        Id<ClientHandle> clientId = program.getClientId();
        try {
            Optional<PromotionLink> promotionLink = lookupPromotionLink(program, incomingRequest);
            if (promotionLink.isEmpty()) {
                return null;
            }
            LOG.debug("Found promotion link {} for incoming request {}", promotionLink.get(), incomingRequest);

            URI destinationUrl = new DestinationUrlBuilder().withDestinationUrl(getDestinationUrl(promotionLink.get()))
                .withIncomingRequest(incomingRequest).build();

            Matcher matcher = ZONE_PATTERN.matcher(destinationUrl.getPath());
            // render directly if it's a zone
            if (matcher.matches()) {
                LOG.debug("Rendering promotion link destinationUrl {}", destinationUrl);
                return renderZone(accessToken, matcher.group(1), destinationUrl,
                    LinkFollowingEndpointMetrics.RENDER_PROMOTABLE_DURATION, clientId).getWebResponse();
            }
            // otherwise redirect (v1/v2 flow, microsite)
            LOG.debug("Redirecting to promotion link destinationUrl {}", destinationUrl);
            return redirect(accessToken, incomingRequest, destinationUrl);
        } finally {
            stopwatch.stop();
            LinkFollowingEndpointMetrics.HANDLE_PROMOTABLE_DURATION.updateHistogram(metricRegistry, metricConfiguration,
                stopwatch.elapsed(TimeUnit.MILLISECONDS), Optional.of(clientId));
        }
    }

    @Nullable
    private Response tryHandleShareableRequest(String accessToken, PublicProgram program, URI incomingRequest)
        throws URISyntaxException, PersonLockAcquireException, ProgramNotFoundException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        Id<ClientHandle> clientId = program.getClientId();
        try {
            Optional<ShortShareLink> shortShareLink =
                shortShareLinkResolver.resolveShortShareLink(incomingRequest.getPath(), program);
            if (!shortShareLink.isPresent()) {
                return null;
            }

            LOG.debug("Found short share link {} for incoming request {}", shortShareLink, incomingRequest);

            URI destinationUrl =
                new DestinationUrlBuilder().withDestinationUrl(getDestinationUrl(shortShareLink.get(), program))
                    .withIncomingRequest(incomingRequest).build();

            Matcher matcher = ZONE_PATTERN.matcher(destinationUrl.getPath());
            // TODO - when v1 is removed, all shareable requests will map to zone requests
            // render directly if it's a zone
            if (matcher.matches()) {
                LOG.debug("Rendering shareable destinationUrl {}", destinationUrl);
                return renderZone(accessToken, matcher.group(1), destinationUrl,
                    LinkFollowingEndpointMetrics.RENDER_SHAREABLE_DURATION, clientId).getWebResponse();
            }
            // otherwise redirect
            LOG.debug("Redirecting to shareable destinationUrl {}", destinationUrl);
            return redirect(accessToken, incomingRequest, destinationUrl);
        } finally {
            stopwatch.stop();
            LinkFollowingEndpointMetrics.HANDLE_SHAREABLE_DURATION.updateHistogram(metricRegistry, metricConfiguration,
                stopwatch.elapsed(TimeUnit.MILLISECONDS), Optional.of(clientId));
        }
    }

    private Optional<PromotionLink> lookupPromotionLink(PublicProgram program, URI incomingRequest) {
        String code = incomingRequest.getPath();
        if (code.startsWith("/")) {
            code = code.substring(1);
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            code = mapPromotionCode(code);
            return promotionLinkCache.get(code, program.getClientId());
        } finally {
            LinkFollowingEndpointMetrics.LOOKUP_PROMOTABLE_DURATION.updateHistogram(metricRegistry,
                metricConfiguration, stopwatch.elapsed(TimeUnit.MILLISECONDS), Optional.of(program.getClientId()));
        }
    }

    private Optional<PromotionLink> lookupPromotionLink(PublicProgram program, String initialCode) {
        String code = initialCode;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            code = mapPromotionCode(code);
            return promotionLinkCache.get(code, program.getClientId());
        } finally {
            LinkFollowingEndpointMetrics.LOOKUP_PROMOTABLE_DURATION.updateHistogram(metricRegistry,
                metricConfiguration, stopwatch.elapsed(TimeUnit.MILLISECONDS), Optional.of(program.getClientId()));
        }
    }

    private static String mapPromotionCode(String code) {
        return StringUtils.isBlank(code) ? PromotionLink.BLANK_CODE : code;
    }

    private WebZoneRenderResponse renderZone(String accessToken, String zoneName, URI requestUri,
        LinkFollowingEndpointMetrics metric, Id<ClientHandle> clientId) throws PersonLockAcquireException {

        Map<String, String> parametersMap = new HashMap<>();
        if (requestUri.getQuery() != null) {
            List<NameValuePair> parameters = URLEncodedUtils.parse(requestUri.getRawQuery(), StandardCharsets.UTF_8);
            for (NameValuePair pair : parameters) {
                String name = pair.getName();
                String value = pair.getValue();
                try {
                    parametersMap.put(name != null ? URLDecoder.decode(name, "UTF-8") : null,
                        value != null ? URLDecoder.decode(value, "UTF-8") : null);
                } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                    LOG.debug(
                        "Invalid characters in parameter {}={} rendering zone: {} for client: {}. Appending undecoded.",
                        name, value, zoneName, clientId, e);
                    parametersMap.put(name, value);
                }
            }
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            ConsumerRequestContext requestContext;
            try {
                requestContext = consumerRequestContextService.createBuilder(servletRequest)
                    .withConsumerRequestType(ConsumerRequestType.WEB)
                    .withReplaceableAccessToken(accessToken)
                    .withEventName(zoneName)
                    .withEventProcessing(configurator -> {
                        parametersMap.forEach((key, value) -> {
                            configurator.addData(
                                new EventData(key, value, EventData.Source.REQUEST_QUERY_PARAMETER, false, true));
                        });
                    })
                    .build();
            } catch (AuthorizationRestException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }

            LOG.debug("Rendering zoneName: {}, params: {}, request: {}", zoneName, parametersMap, requestContext);

            return webZoneRenderer.render(requestContext, servletRequest, ZoneMetrics.ZONE_WEB_DURATION);
        } finally {
            stopwatch.stop();
            metric.updateHistogram(metricRegistry, metricConfiguration, stopwatch.elapsed(TimeUnit.MILLISECONDS),
                Optional.of(clientId));
        }
    }

    @Nullable
    private Response tryHandleNotFoundResponseAsset(URI incomingRequest) {
        String assetPath = incomingRequest.getPath();
        String assetContentType = NOT_FOUND_RESPONSE_ASSETS.get(assetPath);
        if (assetContentType == null) {
            return null;
        }

        try (InputStream assetInputStream = getClass().getResourceAsStream(assetPath)) {
            byte[] assetContent = IOUtils.toByteArray(assetInputStream);
            return Response.status(Status.OK)
                .type(assetContentType)
                .entity(assetContent)
                .header("X-Error-Page", "NOT_FOUND")
                .build();
        } catch (IOException e) {
            throw new LinkFollowingRuntimeException(
                "Unable return NOT_FOUND response asset: " + assetPath + " for incomingRequest: " + incomingRequest, e);
        }
    }

    private Response redirect(String accessToken, URI incomingRequest, URI redirectUrl) throws URISyntaxException {
        ConsumerRequestContext requestContext;
        try {
            requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withConsumerRequestType(ConsumerRequestType.WEB)
                .withReplaceableAccessToken(accessToken)
                .build();
        } catch (AuthorizationRestException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
        Id<ClientHandle> clientId = requestContext.getAuthorization().getClientId();

        if ("https".equals(incomingRequest.getScheme()) && "http".equals(redirectUrl.getScheme())) {
            LOG.warn(String.format("Redirecting a secure link [%s] to an insecure link [%s] for client [%s]",
                incomingRequest.toString(), redirectUrl.toString(), clientId));
        }

        URIBuilder builder = new URIBuilder(redirectUrl);
        if (accessToken != null) {
            try {
                Authorization authorize = authorizationService.reissue(requestContext.getAuthorization(),
                    requestContext.getAuthorization().getScopes(), SHORT_LIVED_ACCESS_TOKEN_TTL);
                builder.addParameter("extole_token", authorize.getAccessToken());
            } catch (InvalidExpiresAtException e) {
                throw new LinkFollowingRuntimeException("Attempted to create a new access_token for client=" + clientId
                    + " with an invalid expires_at=" + SHORT_LIVED_ACCESS_TOKEN_TTL, e);
            } catch (AuthorizationException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }

        LOG.debug("Redirecting to URL: {}", redirectUrl);
        return Response.status(Status.FOUND).location(builder.build()).build();
    }

    private Optional<PublicProgram> lookupProgramFromRequest(URI incomingRequest) {
        try {
            if (!InternetDomainName.isValid(incomingRequest.getHost())) {
                LOG.debug("Illegal domain name for the incoming request {}", incomingRequest);
                return Optional.empty();
            }
            InternetDomainName programDomain = InternetDomainName.from(incomingRequest.getHost());
            PublicProgram program = programCache.getForwardedByProgramDomain(programDomain);
            return Optional.of(program);
        } catch (ProgramNotFoundException e) {
            LOG.debug("No program found for the incoming request {}", incomingRequest, e);
            return Optional.empty();
        }
    }

    private URI getDestinationUrl(PromotionLink promotionLink) throws ProgramNotFoundException {
        try {
            PublicProgram program =
                programCache.getForwardedById(promotionLink.getProgramId(), promotionLink.getClientId());

            Map<String, String> data = promotionLink.getData();
            String microsite = data.get(DATA_EXTOLE_MICROSITE);

            String destination;
            if (Strings.isNullOrEmpty(microsite)) {
                destination = String.format("%s://%s/zone/%s", program.getScheme(), program.getProgramDomain(),
                    ZONE_PROMOTE_DESTINATION);
            } else {
                destination =
                    String.format("%s://%s/micro/%s", program.getScheme(), program.getProgramDomain(), microsite);
            }
            Optional<String> label = promotionLink.getLabel();
            String source =
                PromotionLink.BLANK_CODE.equals(promotionLink.getCode()) ? "direct" : promotionLink.getCode();

            URIBuilder builder = new URIBuilder(destination);
            builder.addParameter(PARAM_PROMOTABLE_CODE, promotionLink.getCode());
            builder.addParameter(PARAM_SOURCE, source);
            if (label.isPresent()) {
                builder.addParameter(PARAM_LABELS, label.get());
            }
            for (Entry<String, String> dataEntry : data.entrySet()) {
                if (!DATA_EXTOLE_MICROSITE.equals(dataEntry.getKey())) {
                    try {
                        builder.addParameter(dataEntry.getKey(),
                            URLEncoder.encode(dataEntry.getValue(), StandardCharsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException e) {
                        LOG.warn("Unable to encode parameter while building url " + destination + " - ignoring");
                    }
                }
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new LinkFollowingRuntimeException(
                "Unable to build the destinationUrl for promotion link: " + promotionLink, e);
        }
    }

    private URI getDestinationUrl(ShortShareLink shortShareLink, PublicProgram sourceProgram)
        throws ProgramNotFoundException {
        try {
            Optional<Shareable> shareable = shortShareLink.getShareable();
            Optional<Id<PersonShare>> shareId = shortShareLink.getShareId();
            Optional<PartnerEventId> partnerShareId = shortShareLink.getPartnerShareId();
            Optional<String> channel = shortShareLink.getChannel();
            PublicProgram program = shareable.isPresent()
                ? programCache.getForwardedById(shareable.get().getProgramId(), shareable.get().getClientId())
                : sourceProgram;
            String destination = "https://" + program.getProgramDomain() + "/zone/" + ZONE_SHARE_DESTINATION;
            URIBuilder builder;

            if (shareable.isPresent()) {
                Map<String, String> data = shareable.get().getData();
                if (isTargetUrlSchemaHttp(data)) {
                    destination = destination.replaceFirst("^https", "http");
                }

                Optional<String> label = shareable.get().getLabel();

                builder = new URIBuilder(destination);
                // TODO Remove shareable id parameter when no longer used https://extole.atlassian.net/browse/ENG-16408
                builder.addParameter(PARAM_SHAREABLE_ID, shareable.get().getShareableId().getValue());
                builder.addParameter(PARAM_ADVOCATE_CODE, shareable.get().getCode());
                if (label.isPresent()) {
                    builder.addParameter(PARAM_LABELS, label.get());
                }
                for (Entry<String, String> dataEntry : data.entrySet()) {
                    try {
                        builder.addParameter(dataEntry.getKey(),
                            URLEncoder.encode(dataEntry.getValue(), StandardCharsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException e) {
                        LOG.warn("Unable to encode parameter while building url " + destination + " - ignoring");
                    }
                }

                String target = Strings.emptyToNull(Strings.nullToEmpty(data.get(DATA_REDIRECT)).trim());
                URI targetUrl = Optional.ofNullable(target).map(URI::create).orElse(null);
                if (targetUrl != null) {
                    // ENG-1943 remove this code, parameters to share_destination should be explicitly passed
                    String queryString = targetUrl.getQuery();
                    if (queryString != null) {
                        List<NameValuePair> params = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
                        for (NameValuePair pair : params) {
                            if (pair.getName().startsWith(ZONE_PARAMETER_PAGE_PREFIX)) {
                                builder.addParameter(pair.getName(), pair.getValue());
                            }
                        }
                    }
                }
            } else {
                builder = new URIBuilder(destination);
            }

            if (shareId.isPresent()) {
                builder.addParameter(PARAM_SHARE_ID, shareId.get().getValue());
            }
            if (partnerShareId.isPresent()) {
                builder.addParameter(PARAM_ADVOCATE_PARTNER_SHARE_ID, partnerShareId.get().getValue());
            }
            if (channel.isPresent()) {
                builder.addParameter(PARAM_SHARE_CHANNEL, channel.get());
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new LinkFollowingRuntimeException(
                "Unable to build the destinationUrl for shareable link data: " + shortShareLink, e);
        }
    }

    private static boolean isTargetUrlSchemaHttp(Map<String, String> data) {
        String target = Strings.nullToEmpty(data.get(DATA_REDIRECT)).trim();
        if (!target.isEmpty()) {
            if ("http".equalsIgnoreCase(URI.create(target).getScheme())) {
                return true;
            }
        } else {
            String destination = Strings.nullToEmpty(data.get(DATA_EXTOLE_DESTINATION)).trim();
            if (!destination.isEmpty() && "http".equalsIgnoreCase(URI.create(destination).getScheme())) {
                return true;
            }
        }
        return false;
    }

    private enum RequestType {
        SHAREABLE, PROMOTION
    }

}
