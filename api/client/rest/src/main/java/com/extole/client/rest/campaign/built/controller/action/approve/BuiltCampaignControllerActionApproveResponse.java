package com.extole.client.rest.campaign.built.controller.action.approve;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.action.approve.ApproveActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionApproveResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_LEGACY_ACTION_ID = "legacy_action_id";
    private static final String JSON_PARTNER_EVENT_ID = "partner_event_id";
    private static final String JSON_EVENT_TYPE = "event_type";
    private static final String JSON_FORCE = "force";
    private static final String JSON_NOTE = "note";
    private static final String JSON_CAUSE_TYPE = "cause_type";
    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_POLLING_NAME = "polling_name";
    private static final String JSON_REWARD_TAGS = "reward_tags";

    private final Optional<String> legacyActionId;
    private final String partnerEventId;
    private final String eventType;
    private final String force;
    private final Optional<String> note;
    private final String causeType;
    private final Optional<String> pollingId;
    private final String pollingName;
    private final RuntimeEvaluatable<ApproveActionContext, Set<String>> rewardTags;

    @JsonCreator
    public BuiltCampaignControllerActionApproveResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_LEGACY_ACTION_ID) Optional<String> legacyActionId,
        @JsonProperty(JSON_PARTNER_EVENT_ID) String partnerEventId,
        @JsonProperty(JSON_EVENT_TYPE) String eventType,
        @JsonProperty(JSON_FORCE) String force,
        @JsonProperty(JSON_NOTE) Optional<String> note,
        @JsonProperty(JSON_CAUSE_TYPE) String causeType,
        @JsonProperty(JSON_POLLING_ID) Optional<String> pollingId,
        @JsonProperty(JSON_POLLING_NAME) String pollingName,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_REWARD_TAGS) RuntimeEvaluatable<ApproveActionContext, Set<String>> rewardTags) {
        super(actionId, CampaignControllerActionType.APPROVE, quality, enabled, componentIds, componentReferences);
        this.legacyActionId = legacyActionId;
        this.partnerEventId = partnerEventId;
        this.eventType = eventType;
        this.force = force;
        this.note = note;
        this.causeType = causeType;
        this.pollingId = pollingId;
        this.pollingName = pollingName;
        this.rewardTags = rewardTags;
    }

    @JsonProperty(JSON_LEGACY_ACTION_ID)
    public Optional<String> getLegacyActionId() {
        return legacyActionId;
    }

    @JsonProperty(JSON_PARTNER_EVENT_ID)
    public String getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(JSON_EVENT_TYPE)
    public String getEventType() {
        return eventType;
    }

    @JsonProperty(JSON_FORCE)
    public String getForce() {
        return force;
    }

    @JsonProperty(JSON_NOTE)
    public Optional<String> getNote() {
        return note;
    }

    @JsonProperty(JSON_CAUSE_TYPE)
    public String getCauseType() {
        return causeType;
    }

    @JsonProperty(JSON_POLLING_ID)
    public Optional<String> getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_POLLING_NAME)
    public String getPollingName() {
        return pollingName;
    }

    @JsonProperty(JSON_REWARD_TAGS)
    public RuntimeEvaluatable<ApproveActionContext, Set<String>> getRewardTags() {
        return rewardTags;
    }

}
