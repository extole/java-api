package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.display.ApiResponse;
import com.extole.api.step.action.display.DisplayActionContext;
import com.extole.api.step.action.display.DisplayActionResponseContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionDisplayConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_BODY = "body";
    private static final String JSON_HEADERS = "headers";
    private static final String JSON_RESPONSE = "response";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, String>> body;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, Map<String, String>>> headers;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>> response;

    @JsonCreator
    public CampaignControllerActionDisplayConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(JSON_BODY) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, String>> body,
        @JsonProperty(JSON_HEADERS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, Map<String, String>>> headers,
        @JsonProperty(JSON_RESPONSE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>> response) {
        super(actionId, CampaignControllerActionType.DISPLAY, quality, enabled, componentReferences);
        this.body = body;
        this.headers = headers;
        this.response = response;
    }

    @JsonProperty(JSON_BODY)
    public BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, String>> getBody() {
        return body;
    }

    @JsonProperty(JSON_HEADERS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, Map<String, String>>> getHeaders() {
        return headers;
    }

    @JsonProperty(JSON_RESPONSE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>> getResponse() {
        return response;
    }

}
