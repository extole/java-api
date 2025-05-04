package com.extole.api.step.action.display;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ApiResponseImpl.class)
public interface ApiResponse {

    String getBody();

    // Location Header with 302 status will internally forward zones when prefixed with "forward:"
    Map<String, String> getHeaders();

    int getStatusCode();
}
