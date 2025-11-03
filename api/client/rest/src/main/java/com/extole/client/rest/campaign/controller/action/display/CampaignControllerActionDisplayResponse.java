package com.extole.client.rest.campaign.controller.action.display;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.display.ApiResponse;
import com.extole.api.step.action.display.DisplayActionContext;
import com.extole.api.step.action.display.DisplayActionResponseContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionDisplayResponse extends CampaignControllerActionResponse {

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
    public CampaignControllerActionDisplayResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_BODY) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, String>> body,
        @JsonProperty(JSON_HEADERS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, Map<String, String>>> headers,
        @JsonProperty(JSON_RESPONSE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>> response) {
        super(actionId, CampaignControllerActionType.DISPLAY, quality, enabled, componentIds, componentReferences);
        this.body = body;
        this.headers = headers;
        this.response = response;
    }

    @JsonProperty(JSON_BODY)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<DisplayActionContext, String>>
        getBody() {
        return body;
    }

    @JsonProperty(JSON_HEADERS)
    public
        BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<DisplayActionContext, Map<String, String>>>
        getHeaders() {
        return headers;
    }

    @JsonProperty(JSON_RESPONSE)
    public
        BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>>
        getResponse() {
        return response;
    }

}
