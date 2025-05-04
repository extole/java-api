package com.extole.api.client.security.key;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(as = OAuthRequestImpl.class)
@Schema
public interface OAuthRequest {

    String getUrl();

    Map<String, List<String>> getHeaders();

    @Nullable
    String getBody();

}
