package com.extole.client.rest.campaign.controller.action.incentivize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.incentivize.IncentivizeActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionIncentivizeResponse extends CampaignControllerActionResponse {

    private static final String JSON_INCENTIVIZE_ACTION_TYPE = "incentivize_action_type";
    private static final String JSON_OVERRIDES = "overrides";
    private static final String JSON_ACTION_NAME = "action_name";
    private static final String JSON_DATA = "data";
    private static final String JSON_REVIEW_STATUS = "review_status";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, IncentivizeActionType> incentivizeActionType;
    private final Map<IncentivizeActionOverrideType, String> overrides;
    private final Optional<String> actionName;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>> data;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus> reviewStatus;

    public CampaignControllerActionIncentivizeResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_INCENTIVIZE_ACTION_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            IncentivizeActionType> incentivizeActionType,
        @JsonProperty(JSON_OVERRIDES) Map<IncentivizeActionOverrideType, String> overrides,
        @JsonProperty(JSON_ACTION_NAME) Optional<String> actionName,
        @JsonProperty(JSON_DATA) Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_REVIEW_STATUS) BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus> reviewStatus) {
        super(actionId, CampaignControllerActionType.INCENTIVIZE, quality, enabled, componentIds, componentReferences);
        this.incentivizeActionType = incentivizeActionType;
        this.overrides = overrides;
        this.actionName = actionName;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
        this.reviewStatus = reviewStatus;
    }

    @JsonProperty(JSON_INCENTIVIZE_ACTION_TYPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, IncentivizeActionType> getIncentivizeActionType() {
        return incentivizeActionType;
    }

    @JsonProperty(JSON_OVERRIDES)
    public Map<IncentivizeActionOverrideType, String> getOverrides() {
        return overrides;
    }

    @JsonProperty(JSON_ACTION_NAME)
    public Optional<String> getActionName() {
        return actionName;
    }

    @JsonProperty(JSON_DATA)
    public
        Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

    @JsonProperty(JSON_REVIEW_STATUS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus> getReviewStatus() {
        return reviewStatus;
    }

}
