package com.extole.client.rest.prehandler.condition.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.common.lang.MultimapUtils;

@Schema(description = "Condition that checks the event HTTP headers.")
public class HttpHeaderMatchPrehandlerConditionRequest extends PrehandlerConditionRequest {
    static final String TYPE = "HTTP_HEADER_MATCH";

    private static final String JSON_HTTP_HEADERS = "http_headers";
    private static final String JSON_HTTP_HEADER_NAMES = "http_header_names";

    private final Map<String, List<String>> httpHeaders;
    private final Set<String> httpHeaderNames;

    @JsonCreator
    public HttpHeaderMatchPrehandlerConditionRequest(
        @JsonProperty(JSON_HTTP_HEADERS) Map<String, List<String>> httpHeaders,
        @JsonProperty(JSON_HTTP_HEADER_NAMES) Set<String> httpHeaderNames) {
        super(PrehandlerConditionType.HTTP_HEADER_MATCH);
        this.httpHeaders = httpHeaders != null ? Collections.unmodifiableMap(MultimapUtils.copyToNew(httpHeaders))
            : Collections.emptyMap();
        this.httpHeaderNames = httpHeaderNames != null ? Set.copyOf(httpHeaderNames) : Collections.emptySet();
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_HTTP_HEADERS)
    @Schema(nullable = true,
        description = "Condition evaluates to true if any of these HTTP headers are present in the request."
            + " At least one HTTP header value should match. HTTP header names are not case sensitive.")
    public Map<String, List<String>> getHttpHeaders() {
        return this.httpHeaders;
    }

    @JsonProperty(JSON_HTTP_HEADER_NAMES)
    @Schema(nullable = true,
        description = "Condition evaluates to true if any of these HTTP headers are present in the request."
            + " HTTP header names are not case sensitive.")
    public Set<String> getHttpHeaderNames() {
        return this.httpHeaderNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, List<String>> httpHeaders;
        private Set<String> httpHeaderNames;

        public Builder withHttpHeaders(Map<String, List<String>> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder withHttpHeaderNames(Set<String> httpHeaderNames) {
            this.httpHeaderNames = httpHeaderNames;
            return this;
        }

        public HttpHeaderMatchPrehandlerConditionRequest build() {
            return new HttpHeaderMatchPrehandlerConditionRequest(httpHeaders, httpHeaderNames);
        }
    }
}
