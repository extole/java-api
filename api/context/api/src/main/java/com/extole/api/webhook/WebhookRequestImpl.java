package com.extole.api.webhook;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.KeyCaseInsensitiveMap;

public class WebhookRequestImpl implements WebhookRequest {

    protected static final String JSON_URL = "url";
    protected static final String JSON_METHOD = "method";
    protected static final String JSON_HEADERS = "headers";
    protected static final String JSON_BODY = "body";
    protected static final String JSON_URL_TEMPLATE_PARAMETERS = "url_template_parameters";

    private final String url;
    private final String method;
    private final Map<String, List<String>> headers;
    private final Optional<String> body;
    private final Map<String, String> urlTemplateParameters;

    @JsonCreator
    public WebhookRequestImpl(
        @JsonProperty(JSON_URL) String url,
        @JsonProperty(JSON_METHOD) String method,
        @JsonProperty(JSON_HEADERS) Map<String, List<String>> headers,
        @JsonProperty(JSON_BODY) Optional<String> body,
        @JsonProperty(JSON_URL_TEMPLATE_PARAMETERS) Map<String, String> urlTemplateParameters) {
        this.url = url;
        this.method = method;
        this.headers = Collections.unmodifiableMap(KeyCaseInsensitiveMap.create(headers));
        this.body = body;
        this.urlTemplateParameters = ImmutableMap.copyOf(urlTemplateParameters);
    }

    @JsonProperty(JSON_URL)
    public String getUrl() {
        return url;
    }

    @JsonProperty(JSON_METHOD)
    public String getMethod() {
        return method;
    }

    @JsonProperty(JSON_HEADERS)
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Nullable
    @JsonProperty(JSON_BODY)
    public String getBody() {
        return body.orElse(null);
    }

    @JsonProperty(JSON_URL_TEMPLATE_PARAMETERS)
    public Map<String, String> getUrlTemplateParameters() {
        return urlTemplateParameters;
    }
}
