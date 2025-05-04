package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerReferredByEventConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String REFERRAL_ORIGINATOR = "referral_originator";

    private final CampaignControllerTriggerReferralOriginator referralOriginator;

    public CampaignControllerTriggerReferredByEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @Nullable @JsonProperty(REFERRAL_ORIGINATOR) CampaignControllerTriggerReferralOriginator referralOriginator,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.REFERRED_BY_EVENT, triggerPhase, name, description, enabled,
            negated, componentReferences);
        this.referralOriginator = referralOriginator;
    }

    @Nullable
    @JsonProperty(REFERRAL_ORIGINATOR)
    public CampaignControllerTriggerReferralOriginator getReferralOriginator() {
        return referralOriginator;
    }

}
