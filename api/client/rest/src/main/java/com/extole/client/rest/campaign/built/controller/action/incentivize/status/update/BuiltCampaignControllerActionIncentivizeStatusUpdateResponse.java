package com.extole.client.rest.campaign.built.controller.action.incentivize.status.update;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.step.action.incentivize.status.update.IncentivizeStatusUpdateActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.IncentivizeActionType;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionIncentivizeStatusUpdateResponse
    extends BuiltCampaignControllerActionResponse {

    private static final String JSON_LEGACY_ACTION_ID = "legacy_action_id";
    private static final String JSON_PARTNER_EVENT_ID = "partner_event_id";
    private static final String JSON_EVENT_TYPE = "event_type";
    private static final String JSON_REVIEW_STATUS = "review_status";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_MOVE_TO_PENDING = "move_to_pending";
    private static final String JSON_DATA = "data";

    private final RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> legacyActionId;
    private final RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>> eventType;
    private final RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> partnerEventId;
    private final RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String> reviewStatus;
    private final RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> message;
    private final Boolean moveToPending;
    private final Map<String, RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>> data;

    public BuiltCampaignControllerActionIncentivizeStatusUpdateResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_LEGACY_ACTION_ID) RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<String>> legacyActionId,
        @JsonProperty(JSON_EVENT_TYPE) RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<IncentivizeActionType>> eventType,
        @JsonProperty(JSON_PARTNER_EVENT_ID) RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<String>> partnerEventId,
        @JsonProperty(JSON_REVIEW_STATUS) RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String> reviewStatus,
        @JsonProperty(JSON_MESSAGE) RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> message,
        @JsonProperty(JSON_MOVE_TO_PENDING) Boolean moveToPending,
        @JsonProperty(JSON_DATA) Map<String,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>> data) {
        super(actionId, CampaignControllerActionType.INCENTIVIZE_STATUS_UPDATE, quality, enabled, componentIds,
            componentReferences);
        this.legacyActionId = legacyActionId;
        this.eventType = eventType;
        this.partnerEventId = partnerEventId;
        this.reviewStatus = reviewStatus;
        this.message = message;
        this.moveToPending = moveToPending;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_LEGACY_ACTION_ID)
    public RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> getLegacyActionId() {
        return legacyActionId;
    }

    @JsonProperty(JSON_EVENT_TYPE)
    public RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>> getEventType() {
        return eventType;
    }

    @JsonProperty(JSON_PARTNER_EVENT_ID)
    public RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(JSON_REVIEW_STATUS)
    public RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String> getReviewStatus() {
        return reviewStatus;
    }

    @JsonProperty(JSON_MESSAGE)
    public RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>> getMessage() {
        return message;
    }

    @JsonProperty(JSON_MOVE_TO_PENDING)
    public Boolean isMoveToPending() {
        return moveToPending;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>> getData() {
        return data;
    }

}
