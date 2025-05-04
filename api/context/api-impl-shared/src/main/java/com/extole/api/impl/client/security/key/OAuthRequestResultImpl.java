package com.extole.api.impl.client.security.key;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.client.security.key.response.OAuthRequestResult;
import com.extole.common.lang.ToString;

public class OAuthRequestResultImpl implements OAuthRequestResult {

    private final int responseStatusCode;
    private final String responseBody;
    private final Map<String, List<String>> responseHeaders;

    public OAuthRequestResultImpl(
        int responseStatusCode,
        String responseBody,
        Map<String, List<String>> responseHeaders) {
        this.responseStatusCode = responseStatusCode;
        this.responseBody = responseBody;
        this.responseHeaders = ImmutableMap.copyOf(responseHeaders);
    }

    @Override
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    @Nullable
    @Override
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
