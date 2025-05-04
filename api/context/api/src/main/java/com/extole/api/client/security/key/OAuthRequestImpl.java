package com.extole.api.client.security.key;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

public class OAuthRequestImpl implements OAuthRequest {

    private static final String JSON_URL = "url";
    private static final String JSON_HEADERS = "headers";
    private static final String JSON_BODY = "body";

    private final String url;
    private final Map<String, List<String>> headers;
    private final Optional<String> body;

    @JsonCreator
    public OAuthRequestImpl(@JsonProperty(JSON_URL) String url,
        @JsonProperty(JSON_HEADERS) Map<String, List<String>> headers,
        @JsonProperty(JSON_BODY) Optional<String> body) {
        this.url = url;
        this.headers = ImmutableMap.copyOf(headers);
        this.body = body;
    }

    @Override
    @JsonProperty(JSON_URL)
    public String getUrl() {
        return url;
    }

    @Override
    @JsonProperty(JSON_HEADERS)
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    @Nullable
    @JsonProperty(JSON_BODY)
    public String getBody() {
        return body.orElse(null);
    }
}
