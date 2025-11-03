package com.extole.client.rest.campaign.controller.trigger.referred.by;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerReferredByEventResponse extends CampaignControllerTriggerResponse {
    private static final String REFERRAL_ORIGINATOR = "referral_originator";

    private final CampaignControllerTriggerReferralOriginator referralOriginator;

    public CampaignControllerTriggerReferredByEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @Nullable @JsonProperty(REFERRAL_ORIGINATOR) CampaignControllerTriggerReferralOriginator referralOriginator,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.REFERRED_BY_EVENT, triggerPhase, name, parentTriggerGroupName,
            description, enabled, negated, componentIds, componentReferences);
        this.referralOriginator = referralOriginator;
    }

    @Nullable
    @JsonProperty(REFERRAL_ORIGINATOR)
    public CampaignControllerTriggerReferralOriginator getReferralOriginator() {
        return referralOriginator;
    }

}
