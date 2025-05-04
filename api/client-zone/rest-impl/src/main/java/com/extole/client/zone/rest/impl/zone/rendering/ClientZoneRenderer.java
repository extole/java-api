package com.extole.client.zone.rest.impl.zone.rendering;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.zone.rest.impl.zone.rendering.response.ClientZoneRenderResponse;
import com.extole.consumer.service.zone.ZoneEventResult;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.consumer.service.zone.ZoneRenderService;
import com.extole.id.Id;
import com.extole.model.entity.program.GlobPattern;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.profile.PersonLockAcquireRuntimeException;

@Component
public class ClientZoneRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(ClientZoneRenderer.class);

    private static final String HEADER_EXTOLE_INPUT_EVENT_ID = "X-Extole-Input-Event-Id";

    private final ProgramDomainCache programDomainCache;
    private final ZoneRenderService zoneRenderService;

    @Autowired
    public ClientZoneRenderer(
        ProgramDomainCache programDomainCache,
        ZoneRenderService zoneRenderService) {
        this.programDomainCache = programDomainCache;
        this.zoneRenderService = zoneRenderService;
    }

    @WithSpan
    public ClientZoneRenderResponse render(ClientRequestContext requestContext, HttpServletRequest servletRequest,
        ZoneMetrics metric) throws PersonLockAcquireException, ZoneInvalidRedirectException {
        try {
            return doRender(requestContext, servletRequest, metric);
        } catch (PersonLockAcquireRuntimeException e) {
            throw new PersonLockAcquireException(e);
        }
    }

    private ClientZoneRenderResponse doRender(ClientRequestContext requestContext, HttpServletRequest servletRequest,
        ZoneMetrics metric) throws ZoneInvalidRedirectException {
        ClientAuthorization authorization = requestContext.getAuthorization();
        ZoneRenderResponse zoneResponse = zoneRenderService.render(authorization, requestContext.getPerson().get(),
            requestContext.getProcessedRawEvent(), requestContext.getPerformanceLogMessages(), metric,
            Optional.empty());

        if (isZoneResponseWithRedirect(zoneResponse)) {
            Collection<String> redirectUrls = zoneResponse.getHeaders().get(ZoneRenderResponse.HEADER_LOCATION);
            List<String> invalidRedirects =
                validateRedirects(zoneResponse, servletRequest, authorization.getClientId(), redirectUrls);
            if (!invalidRedirects.isEmpty()) {
                String errorMessage = "Client: " + authorization.getClientId() + " does not support the redirects to: "
                    + invalidRedirects;
                LOG.warn(errorMessage);
                throw new ZoneInvalidRedirectException(zoneResponse, errorMessage, invalidRedirects);
            }
        }

        ResponseBuilder responseBuilder = Response.status(zoneResponse.getStatusCode());

        for (Map.Entry<String, String> header : zoneResponse.getHeaders().entries()) {
            String headerValue = header.getValue();
            if (header.getKey().equals(ZoneRenderResponse.HEADER_LOCATION)) {
                headerValue =
                    parseRedirectURI(zoneResponse, servletRequest, authorization.getClientId(), headerValue).toString();
            }
            responseBuilder.header(header.getKey(), headerValue);
        }

        addExtoleInputEventIdHeaders(requestContext, zoneResponse, responseBuilder);
        responseBuilder.entity(zoneResponse.getBody());

        return new ClientZoneRenderResponse(responseBuilder.build(), zoneResponse);
    }

    private void addExtoleInputEventIdHeaders(ClientRequestContext requestContext, ZoneRenderResponse zoneResponse,
        ResponseBuilder responseBuilder) {
        if (!requestContext.getProcessedRawEvent().getRawEvent().isDebugRequest()) {
            return;
        }
        String headerName = HEADER_EXTOLE_INPUT_EVENT_ID;

        for (ZoneEventResult event : zoneResponse.getZoneEvents()) {
            String headerValue = event.getInputEvent().getId().getValue();
            if (!zoneResponse.getHeaders().get(headerName).contains(headerValue)) {
                responseBuilder.header(headerName, headerValue);
            }
        }
    }

    @WithSpan
    private List<String> validateRedirects(ZoneRenderResponse zoneResponse, HttpServletRequest servletRequest,
        Id<ClientHandle> clientId, Collection<String> redirectUrls) {
        String requestUrl = getRequestUrl(servletRequest);
        List<String> invalidRedirects = Lists.newArrayList();
        List<GlobPattern> clientSites = programDomainCache.getSitePatternsByClientId(clientId);
        redirectUrls.forEach(url -> {
            try {
                URI parsedUri;
                // Redirect URL can be set from JS (URI or URL) code or a redirect parameter (URI).
                // We don't know if it was meant to be an URI or URL, so we try both.
                try {
                    parsedUri = URI.create(url);
                } catch (IllegalArgumentException e) {
                    parsedUri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();
                }

                URI redirectUri;
                if (parsedUri.isAbsolute()) {
                    redirectUri = parsedUri;
                } else if (url.startsWith("//")) {
                    // T3-409 Scheme/Protocol relative URLs need to be treated as absolute
                    redirectUri = parsedUri;
                } else {
                    Optional<ZoneEventResult> zoneEventResult = zoneResponse.getZoneEvents().stream().findFirst();
                    Optional<?> inputEventId = zoneEventResult.map(value -> value.getInputEvent().getId());
                    Optional<?> stepEventId =
                        zoneEventResult.flatMap(value -> value.getStepEvent().map(stepEvent -> stepEvent.getId()));

                    LOG.warn("Relative URI redirect={} in client zone request={}"
                        + " for client={} inputEventId={} stepEventId={}",
                        url, requestUrl, clientId, inputEventId, stepEventId);
                    redirectUri = toAbsoluteUri(requestUrl, parsedUri);
                }

                if (clientSites.stream()
                    .noneMatch(site -> site.getRegex()
                        .matcher(redirectUri.getHost() == null ? "" : redirectUri.getHost()).matches())) {
                    invalidRedirects.add(url);
                }
            } catch (IllegalArgumentException e) {
                LOG.warn("Invalid redirect: {} in zone request: {} for client: {}", url, requestUrl, clientId);
                invalidRedirects.add(url);
            }
        });
        return invalidRedirects;
    }

    private boolean isZoneResponseWithRedirect(ZoneRenderResponse zoneResponse) {
        return zoneResponse.getStatusCode() == ZoneRenderResponse.HTTP_FOUND &&
            zoneResponse.getHeaders().containsKey(ZoneRenderResponse.HEADER_LOCATION);
    }

    private URI parseRedirectURI(ZoneRenderResponse zoneResponse, HttpServletRequest servletRequest,
        Id<ClientHandle> clientId, String value) throws ZoneInvalidRedirectException {
        String requestUrl = getRequestUrl(servletRequest);
        value = StringEscapeUtils.escapeJava(value);
        URI parsedUri;
        try {
            parsedUri = URI.create(value);
        } catch (IllegalArgumentException e) {
            try {
                parsedUri = UriComponentsBuilder.fromHttpUrl(value)
                    .build()
                    .toUri();
            } catch (IllegalArgumentException ex) {
                String errorMessage =
                    String.format("Unable to redirect to an invalid URI: %s for client: %s", value, clientId);
                LOG.warn(errorMessage, ex);

                throw new ZoneInvalidRedirectException(zoneResponse, errorMessage, Collections.singletonList(value),
                    ex);
            }
        }

        URI redirectUri;
        if (parsedUri.isAbsolute()) {
            redirectUri = parsedUri;
        } else if (value.startsWith("//")) {
            // T3-409 Scheme/Protocol relative URLs need to be treated as absolute
            redirectUri = parsedUri;
        } else {
            redirectUri = toAbsoluteUri(requestUrl, parsedUri);
        }

        return redirectUri;
    }

    private URI toAbsoluteUri(String requestUrl, URI parsedUri) {
        return UriComponentsBuilder.fromHttpUrl(requestUrl)
            .fragment(Strings.emptyToNull(parsedUri.getFragment()))
            .replaceQuery(parsedUri.getQuery())
            .replacePath(parsedUri.getPath())
            .build()
            .toUri();
    }

    private String getRequestUrl(HttpServletRequest servletRequest) {
        // TODO https://extole.atlassian.net/browse/ENG-25077
        if (Boolean.parseBoolean(System.getProperty("local.dev.env"))) {
            String incomingHostHeader = servletRequest.getHeader("host");
            String incomingXEnvoyOriginalPath = servletRequest.getHeader("x-envoy-original-path");

            if (StringUtils.isNotBlank(incomingHostHeader) && StringUtils.isNotBlank(incomingXEnvoyOriginalPath)) {
                return "https://" + incomingHostHeader + incomingXEnvoyOriginalPath;
            }
        }

        String incomingUrlFromHeaders = servletRequest.getHeader("X-Extole-Incoming-Url");

        if (StringUtils.isNotBlank(incomingUrlFromHeaders)) {
            return incomingUrlFromHeaders;
        }

        return servletRequest.getRequestURL().toString();
    }

}
