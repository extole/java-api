package com.extole.common.rest.support.filter;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.id.Id;

public class CorsHeaderInjector {
    @Deprecated // TODO REMOVE ZOOM specific hacks in T3-1077-2
    private static final Id<ClientHandle> ZOOM_CLIENT_ID = Id.valueOf("841133454");
    private static final Logger LOG = LoggerFactory.getLogger(CorsHeaderInjector.class);

    private static final String COMMA = ",";
    private static final String HEADER_EXTOLE_PREFIX = "x-extole";
    @Deprecated // TODO REMOVE ZOOM specific hacks in T3-1077-2
    private static final String EXTOLE_USER_AGENT_HEADER_PREFIX = "Extole/2.0";

    private final ContainerRequestContext requestContext;
    private final RequestOriginValidator requestOriginValidator;
    private final Supplier<Optional<String>> rejectionHeaderProvider;
    private final boolean varyOrigin;
    private final boolean testMode;
    private final String logPrefix;
    private final Optional<Id<ClientHandle>> clientId;
    private final boolean zoomEmptyResponseOnFailure;

    public CorsHeaderInjector(ContainerRequestContext requestContext,
        RequestOriginValidator requestOriginValidator,
        Supplier<Optional<String>> rejectionHeaderProvider,
        boolean varyOrigin, boolean testMode,
        @Nullable String logPrefix, Optional<Id<ClientHandle>> clientId, boolean zoomEmptyResponseOnFailure) {

        this.requestContext = requestContext;
        this.requestOriginValidator = requestOriginValidator;
        this.rejectionHeaderProvider = rejectionHeaderProvider;
        this.varyOrigin = varyOrigin;
        this.logPrefix = logPrefix;
        this.testMode = testMode;
        this.clientId = clientId;
        this.zoomEmptyResponseOnFailure = zoomEmptyResponseOnFailure;
    }

    public void inject(ContainerResponseContext responseContext) {
        // TODO REMOVE ZOOM specific hacks in T3-1077-2
        Optional<String> userAgentHeader =
            Optional.ofNullable(requestContext.getHeaders().getFirst(HttpHeaders.USER_AGENT));
        if (userAgentHeader.isPresent() && userAgentHeader.get().startsWith(EXTOLE_USER_AGENT_HEADER_PREFIX)) {
            return;
        }
        MultivaluedMap<String, Object> responseHeaders = responseContext.getHeaders();
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, PUT, POST, DELETE, OPTIONS");
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "DNT,X-Mx-ReqToken,Keep-Alive,User-Agent," +
            "X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authentication,Authorization,X-CSRF-TOKEN," +
            "X-NONCE" + getXExtoleAppHeaders());
        StringJoiner headersToExpose = new StringJoiner(COMMA);
        headersToExpose.add(ExtoleHeaderType.TOKEN.getHeaderName());
        headersToExpose.add(ExtoleHeaderType.LOG.getHeaderName());
        if (responseHeaders.containsKey(ExtoleHeaderType.COOKIE_CONSENT.getHeaderName())) {
            headersToExpose.add(ExtoleHeaderType.COOKIE_CONSENT.getHeaderName());
        }
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, headersToExpose.toString());
        if (varyOrigin) {
            responseHeaders.add(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        }

        Optional<String> requestOriginHeaderValue = getRequestOriginHeaderValue(requestContext);
        if (requestOriginHeaderValue.isEmpty() && clientId.isPresent() && ZOOM_CLIENT_ID.equals(clientId.get())) {
            requestOriginHeaderValue = Optional.ofNullable(requestContext.getHeaders().getFirst(HttpHeaders.REFERER));
            if (requestOriginHeaderValue.isEmpty() && zoomEmptyResponseOnFailure && isNotTextHtml(responseHeaders)) {
                LOG.warn("T3-1077 Removing response body from potential XSS request {} for zoom",
                    requestContext.getUriInfo().getRequestUri());
                responseContext.setEntity("");
            }
        }
        Optional<String> rejectionAccessControlAllowOriginHeaderValue = rejectionHeaderProvider.get();

        if (requestOriginHeaderValue.isPresent()) {
            if (requestOriginValidator.isValid(
                requestContext.getUriInfo().getBaseUri().getHost(),
                requestOriginHeaderValue.get())) {
                responseHeaders.putSingle(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                    requestOriginHeaderValue.get());
            } else {
                String message = Strings.isNullOrEmpty(logPrefix) ? "" : logPrefix + " - ";
                message += "Unsupported Origin: " + requestOriginHeaderValue.get() + ", for request: "
                    + requestContext.getUriInfo().getRequestUri().toString();
                if (rejectionAccessControlAllowOriginHeaderValue.isPresent()) {
                    responseHeaders.putSingle(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        rejectionAccessControlAllowOriginHeaderValue.get());
                    message += " , responding with Access-Control-Allow-Origin: "
                        + rejectionAccessControlAllowOriginHeaderValue.get();
                } else {
                    message +=
                        ", no rejection Access-Control-Allow-Origin value is available to include in response";
                }

                if (clientId.isEmpty()) {
                    LOG.info(message + ", possible ex-customer");
                } else if (ZOOM_CLIENT_ID.equals(clientId.get()) && zoomEmptyResponseOnFailure
                    && isNotTextHtml(responseHeaders)) {
                    LOG.warn("T3-1077 Removing response body from potential XSS request {} from {} for zoom",
                        requestContext.getUriInfo().getRequestUri(), requestOriginHeaderValue.get());
                    responseContext.setEntity("");
                } else {
                    LOG.warn(message + ", our customer {} is having issues with CORS", clientId.get());
                }
            }
        }
        if (testMode) {
            responseHeaders.putSingle(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestOriginHeaderValue.orElse("*"));
        }
    }

    private boolean isNotTextHtml(MultivaluedMap<String, Object> responseHeaders) {
        Optional<Object> contentTypeHeaderValue =
            Optional.ofNullable(responseHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
        return contentTypeHeaderValue.isEmpty() || !contentTypeHeaderValue.get().toString().contains("html");
    }

    private String getXExtoleAppHeaders() {
        String requestHeaders = requestContext.getHeaderString(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        if (requestHeaders == null) {
            return "";
        }
        String headers = Arrays.stream(requestHeaders.split(COMMA))
            .map(String::trim)
            .filter(header -> header.toLowerCase().startsWith(HEADER_EXTOLE_PREFIX))
            .collect(Collectors.joining(COMMA));
        return Strings.isNullOrEmpty(headers) ? headers : COMMA + headers;
    }

    private Optional<String> getRequestOriginHeaderValue(ContainerRequestContext requestContext) {
        return Optional.ofNullable(requestContext.getHeaders().getFirst(HttpHeaders.ORIGIN));
    }

    public static CorsHeaderInjector.Builder builder(
        ContainerRequestContext requestContext, Optional<Id<ClientHandle>> clientId) {
        return new CorsHeaderInjector.Builder(requestContext, clientId);
    }

    public static class Builder {
        private final ContainerRequestContext requestContext;
        private RequestOriginValidator requestOriginValidator;
        private Supplier<Optional<String>> rejectionHeaderProvider;
        private boolean varyOrigin = true;
        private String logPrefix;
        private boolean testMode = false;
        private boolean zoomEmptyResponseOnFailure = false;
        private final Optional<Id<ClientHandle>> clientId;

        public Builder(ContainerRequestContext requestContext, Optional<Id<ClientHandle>> clientId) {
            this.requestContext = requestContext;
            this.clientId = clientId;
        }

        public CorsHeaderInjector.Builder withRequestOriginValidator(RequestOriginValidator requestOriginValidator) {
            this.requestOriginValidator = requestOriginValidator;
            return this;
        }

        public CorsHeaderInjector.Builder withRejectionHeaderProvider(
            Supplier<Optional<String>> defaultAccessControlAllowOriginProvider) {
            this.rejectionHeaderProvider = defaultAccessControlAllowOriginProvider;
            return this;
        }

        @Deprecated // TODO REMOVE ZOOM specific hacks in T3-1077-2
        public CorsHeaderInjector.Builder withZoomEmptyResponseOnFailure() {
            this.zoomEmptyResponseOnFailure = true;
            return this;
        }

        public CorsHeaderInjector.Builder withNoVary() {
            this.varyOrigin = false;
            return this;
        }

        public CorsHeaderInjector.Builder withTestMode() {
            this.testMode = true;
            return this;
        }

        public CorsHeaderInjector.Builder withContext(String logPrefix) {
            this.logPrefix = logPrefix;
            return this;
        }

        public CorsHeaderInjector build() {
            return new CorsHeaderInjector(requestContext, requestOriginValidator, rejectionHeaderProvider, varyOrigin,
                testMode, logPrefix, clientId, zoomEmptyResponseOnFailure);
        }

    }
}
