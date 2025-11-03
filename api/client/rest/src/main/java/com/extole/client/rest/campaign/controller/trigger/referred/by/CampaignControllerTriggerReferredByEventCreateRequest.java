package com.extole.client.rest.campaign.controller.trigger.referred.by;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerReferredByEventCreateRequest extends CampaignControllerTriggerRequest {
    private static final String REFERRAL_ORIGINATOR = "referral_originator";

    private final CampaignControllerTriggerReferralOriginator referralOriginator;

    public CampaignControllerTriggerReferredByEventCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @Nullable @JsonProperty(REFERRAL_ORIGINATOR) CampaignControllerTriggerReferralOriginator referralOriginator,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated,
            componentIds, componentReferences);
        this.referralOriginator = referralOriginator;
    }

    @Nullable
    @JsonProperty(REFERRAL_ORIGINATOR)
    public CampaignControllerTriggerReferralOriginator getReferralOriginator() {
        return referralOriginator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {
        private CampaignControllerTriggerReferralOriginator referralOriginator;

        private Builder() {
        }

        public Builder withReferralOriginator(CampaignControllerTriggerReferralOriginator referralOriginator) {
            this.referralOriginator = referralOriginator;
            return this;
        }

        public CampaignControllerTriggerReferredByEventCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerReferredByEventCreateRequest(
                triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                referralOriginator,
                componentIds,
                componentReferences);
        }
    }
}
