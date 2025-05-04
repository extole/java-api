package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.referred.by.CampaignControllerTriggerReferralOriginator;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerReferredByEventResponse extends BuiltCampaignControllerTriggerResponse {
    private static final String REFERRAL_ORIGINATOR = "referral_originator";

    private final CampaignControllerTriggerReferralOriginator referralOriginator;

    public BuiltCampaignControllerTriggerReferredByEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @Nullable @JsonProperty(REFERRAL_ORIGINATOR) CampaignControllerTriggerReferralOriginator referralOriginator,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.REFERRED_BY_EVENT, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.referralOriginator = referralOriginator;
    }

    @Nullable
    @JsonProperty(REFERRAL_ORIGINATOR)
    public CampaignControllerTriggerReferralOriginator getReferralOriginator() {
        return referralOriginator;
    }

}
