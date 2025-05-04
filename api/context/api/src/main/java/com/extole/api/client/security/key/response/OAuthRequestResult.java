package com.extole.api.client.security.key.response;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface OAuthRequestResult {

    int getResponseStatusCode();

    @Nullable
    String getResponseBody();

    Map<String, List<String>> getResponseHeaders();

}
