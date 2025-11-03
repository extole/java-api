package com.extole.consumer.rest.impl.request;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.Priority;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.ExtoleCookieType;
import com.extole.common.rest.exception.ExtoleAuthorizationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.person.PersonAuthorizationFilter;
import com.extole.id.Id;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.model.shared.program.ProgramDomainCache;

@Provider
@PreMatching
@Priority(PersonAuthorizationFilter.AUTH_FILTER_PRIORITY - 1)
public class ConsumerRequestFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerRequestFilter.class);
    private static final String HEADER_X_PROTO = "X-Proto";

    private static final String HEADER_X_EXTOLE_INCOMING_URL = "X-Extole-Incoming-Url";

    @Context
    private HttpServletRequest servletRequest;

    private final ProgramDomainCache programCache;
    private final ClientCache clientCache;

    @Autowired
    public ConsumerRequestFilter(ProgramDomainCache programCache, ClientCache clientCache) {
        this.programCache = programCache;
        this.clientCache = clientCache;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            PublicProgram program = digestProgram(requestContext);
            servletRequest.setAttribute(ConsumerContextAttributeName.PROGRAM.getAttributeName(), program);
            try {
                PublicClient publicClient = clientCache.getById(program.getClientId());
                String clientShortName = publicClient.getShortName();
                requestContext.setProperty(RequestContextAttributeName.CLIENT_SHORT_NAME.getAttributeName(),
                    clientShortName);
                boolean deprecatedAccessTokenCookieAllowed =
                    publicClient.getCoreSettings().isDeprecatedAccessTokenCookieAllowed();
                requestContext.setProperty(
                    RequestContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName(),
                    deprecatedAccessTokenCookieAllowed);
                servletRequest.setAttribute(
                    ConsumerContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName(),
                    deprecatedAccessTokenCookieAllowed);
            } catch (ClientNotFoundException e) {
                LOG.error("Retrieved program {} for non existent client", program);
            }
            servletRequest.setAttribute(RequestContextAttributeName.CLIENT_ID.getAttributeName(),
                program.getClientId().getValue());

            URI requestUri = requestContext.getUriInfo().getRequestUri();
            String originalSchemeInProtoHeader = requestContext.getHeaderString(HEADER_X_PROTO);
            String incomingUrl = servletRequest.getHeader(HEADER_X_EXTOLE_INCOMING_URL);

            LOG.trace("Verifying secure request with parameters: request URI: {} X-Extole-Incoming-Url: {} "
                + "X-Proto: {}", requestUri, incomingUrl, originalSchemeInProtoHeader);

            if (!Strings.isNullOrEmpty(incomingUrl)) {
                try {
                    URI requestUriFromHeader = UriComponentsBuilder.fromHttpUrl(incomingUrl).build().toUri();
                    if (!"https".equals(requestUriFromHeader.getScheme())) {
                        redirectInsecureRequestToSecure(requestContext, program, requestUriFromHeader);
                        return;
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    LOG.warn("Unable to redirect possibly insecure request-Client {}-due to invalid http url {}",
                        program.getClientId(), incomingUrl, e);
                }

                if (!Strings.isNullOrEmpty(originalSchemeInProtoHeader)
                    && !"https".equals(originalSchemeInProtoHeader)) {
                    redirectInsecureRequestToSecure(requestContext, program, requestUri);
                }
            }
        } catch (ExtoleAuthorizationRestException e) {
            LOG.info("Unable to identify program for request {}", requestContext.getUriInfo().getRequestUri());
            String originClientId = resolveOriginUriClient(requestContext.getUriInfo());
            if (originClientId != null) {
                servletRequest.setAttribute(RequestContextAttributeName.CLIENT_ID.getAttributeName(), originClientId);
            } else {
                throw e;
            }
        }
    }

    private void redirectInsecureRequestToSecure(ContainerRequestContext requestContext, PublicProgram program,
        URI requestUri) {
        LOG.warn("Unsecured request redirected to secured URL for client: {}. Requested URI: {}",
            program.getClientId().getValue(), requestUri.toString());
        URI secureLocation = UriBuilder.fromUri(requestUri).scheme("https").build();

        ResponseBuilder response =
            Response.status(Response.Status.MOVED_PERMANENTLY).location(secureLocation);
        addCookiesFromRequest(response, requestContext);
        requestContext.abortWith(response.build());
    }

    private void addCookiesFromRequest(ResponseBuilder response, ContainerRequestContext requestContext) {
        Cookie[] requestCookies = servletRequest.getCookies();
        if (requestCookies == null) {
            return;
        }

        List<Cookie> allCookies = Arrays.asList(requestCookies);

        Optional<Cookie> requestAccessTokenCookieOptional = allCookies.stream()
            .filter(cookie -> ExtoleCookieType.ACCESS_TOKEN.getCookieName().equals(cookie.getName()))
            .findFirst();

        if (requestAccessTokenCookieOptional.isEmpty() && isDeprecatedAccessTokenCookieAllowed(requestContext)) {
            requestAccessTokenCookieOptional = allCookies.stream()
                .filter(cookie -> ExtoleCookieType.DEPRECATED_ACCESS_TOKEN.getCookieName().equals(cookie.getName()))
                .findFirst();
        }

        if (requestAccessTokenCookieOptional.isPresent()) {
            Cookie requestAccessTokenCookie = requestAccessTokenCookieOptional.get();
            new ExtoleCookie(ExtoleCookieType.ACCESS_TOKEN.getCookieName(),
                requestAccessTokenCookie.getValue(), requestAccessTokenCookie.getPath(),
                requestAccessTokenCookie.getDomain(), requestAccessTokenCookie.getComment(),
                requestAccessTokenCookie.getMaxAge()).addCookieToResponse(response);
        }

        Optional<Cookie> requestBrowserIdCookieOptional = allCookies.stream()
            .filter(cookie -> ExtoleCookieType.BROWSER_ID.getCookieName().equals(cookie.getName())).findFirst();

        if (requestBrowserIdCookieOptional.isPresent()) {
            Cookie requestBrowserIdCookie = requestBrowserIdCookieOptional.get();
            new ExtoleCookie(ExtoleCookieType.BROWSER_ID.getCookieName(),
                requestBrowserIdCookie.getValue(), requestBrowserIdCookie.getPath(),
                requestBrowserIdCookie.getDomain(), requestBrowserIdCookie.getComment(),
                requestBrowserIdCookie.getMaxAge()).addCookieToResponse(response);
        }
    }

    private PublicProgram digestProgram(ContainerRequestContext requestContext) {
        try {
            InternetDomainName internetDomainName = null;
            Optional<String> firstHeaderValue =
                Optional.ofNullable(requestContext.getHeaders().getFirst(HEADER_X_EXTOLE_INCOMING_URL));
            if (!firstHeaderValue.isPresent()) {
                throw new IllegalArgumentException(
                    "Header: " + HEADER_X_EXTOLE_INCOMING_URL + " is required and is not present in request");
            }

            Optional<String> host =
                Optional.ofNullable(UriComponentsBuilder.fromUriString(firstHeaderValue.get()).build().getHost());
            if (!host.isPresent()) {
                throw new IllegalArgumentException(
                    "Header: " + HEADER_X_EXTOLE_INCOMING_URL + " contains an URI with no host: "
                        + firstHeaderValue.get());
            }

            String hostWithoutPort = HostAndPort.fromString(host.get()).getHost();
            internetDomainName = InternetDomainName.from(hostWithoutPort);
            return programCache.getForwardedByProgramDomain(internetDomainName);
        } catch (IllegalArgumentException | ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ExtoleAuthorizationRestException.class)
                .withErrorCode(ExtoleAuthorizationRestException.INVALID_PROGRAM_DOMAIN)
                .withCause(e)
                .build();
        }
    }

    @Nullable
    private String resolveOriginUriClient(UriInfo uriInfo) {
        String path = uriInfo.getPath();
        Matcher clientIdMatcher = Pattern.compile("^(?:origin/)?(\\d+)/core\\.js$").matcher(path);
        if (clientIdMatcher.find()) {
            String clientIdString = clientIdMatcher.group(1);
            try {
                return clientCache.getById(Id.valueOf(clientIdString)).getId().getValue();
            } catch (ClientNotFoundException e) {
                LOG.debug("No client found for the incoming request {} clientId match {}", uriInfo, clientIdString, e);
                return null;
            }
        }

        Matcher clientNameMatcher = Pattern.compile("^(?:origin/)?(.+)/core\\.js$").matcher(path);
        if (clientNameMatcher.find()) {
            String clientNameString = clientNameMatcher.group(1);
            try {
                return clientCache.getByShortName(clientNameString).getId().getValue();
            } catch (ClientNotFoundException e) {
                LOG.debug("No client found for the incoming request {} clientName match {}",
                    uriInfo, clientNameString, e);
                return null;
            }
        }
        return null;
    }

    private boolean isDeprecatedAccessTokenCookieAllowed(ContainerRequestContext requestContext) {
        return Boolean.TRUE.equals(requestContext
            .getProperty(RequestContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName()));
    }
}
