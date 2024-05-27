package com.extole.api.webhook;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(as = WebhookRequestImpl.class)
@Schema
public interface WebhookRequest {

    String getUrl();

    String getMethod();

    Map<String, List<String>> getHeaders();

    @Nullable
    String getBody();

    Map<String, String> getUrlTemplateParameters();

}
