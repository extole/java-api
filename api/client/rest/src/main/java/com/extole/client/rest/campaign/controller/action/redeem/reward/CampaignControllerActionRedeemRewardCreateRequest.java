package com.extole.client.rest.campaign.controller.action.redeem.reward;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionRedeemRewardCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_REWARD_ID = "reward_id";
    private static final String JSON_DATA = "data";
    private static final String JSON_PARTNER_EVENT_ID = "partner_event_id";
    private static final String JSON_EVENT_TIME = "event_time";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<String> rewardId;
    private final Omissible<Map<String, String>> data;
    private final Omissible<Optional<String>> partnerEventId;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime;

    public CampaignControllerActionRedeemRewardCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_REWARD_ID) Omissible<String> rewardId,
        @JsonProperty(JSON_DATA) Omissible<Map<String, String>> data,
        @JsonProperty(JSON_PARTNER_EVENT_ID) Omissible<Optional<String>> partnerEventId,
        @JsonProperty(JSON_EVENT_TIME) Omissible<
            RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.rewardId = rewardId;
        this.data = data;
        this.partnerEventId = partnerEventId;
        this.eventTime = eventTime;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_REWARD_ID)
    public Omissible<String> getRewardId() {
        return rewardId;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String, String>> getData() {
        return data;
    }

    @JsonProperty(JSON_PARTNER_EVENT_ID)
    public Omissible<Optional<String>> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(JSON_EVENT_TIME)
    public Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<String> rewardId = Omissible.omitted();
        private Omissible<Optional<String>> partnerEventId = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime = Omissible.omitted();

        private Builder() {
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withRewardId(String rewardId) {
            this.rewardId = Omissible.of(rewardId);
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withPartnerEventId(String partnerEventId) {
            this.partnerEventId = Omissible.of(Optional.ofNullable(partnerEventId));
            return this;
        }

        public Builder withEventTime(RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
            this.eventTime = Omissible.of(eventTime);
            return this;
        }

        public CampaignControllerActionRedeemRewardCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionRedeemRewardCreateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                rewardId,
                data,
                partnerEventId,
                eventTime);
        }
    }
}
