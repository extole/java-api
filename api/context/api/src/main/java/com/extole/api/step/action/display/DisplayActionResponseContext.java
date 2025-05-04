package com.extole.api.step.action.display;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface DisplayActionResponseContext extends DisplayActionContext {

    String getBody();

    Map<String, String> getHeaders();

    ApiResponseBuilder getResponseBuilder();

}
