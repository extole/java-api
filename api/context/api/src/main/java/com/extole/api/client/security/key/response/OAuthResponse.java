package com.extole.api.client.security.key.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(as = OAuthResponseImpl.class)
@Schema
public interface OAuthResponse {

    String getAccessToken();

    Long getExpiresIn();

}
