package com.extole.client.rest.campaign.controller.action.fulfill.reward;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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

public class CampaignControllerActionFulfillRewardCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_REWARD_ID = "reward_id";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_PARTNER_REWARD_ID = "partner_reward_id";
    private static final String JSON_EVENT_TIME = "event_time";
    private static final String JSON_AMOUNT = "amount";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<String> rewardId;
    private final Omissible<Optional<String>> message;
    private final Omissible<Optional<String>> success;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Optional<String>>> partnerRewardId;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Optional<BigDecimal>>> amount;

    public CampaignControllerActionFulfillRewardCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_REWARD_ID) Omissible<String> rewardId,
        @JsonProperty(JSON_MESSAGE) Omissible<Optional<String>> message,
        @JsonProperty(JSON_SUCCESS) Omissible<Optional<String>> success,
        @JsonProperty(JSON_PARTNER_REWARD_ID) Omissible<
            RuntimeEvaluatable<RewardActionContext, Optional<String>>> partnerRewardId,
        @JsonProperty(JSON_AMOUNT) Omissible<RuntimeEvaluatable<RewardActionContext, Optional<BigDecimal>>> amount,
        @JsonProperty(JSON_EVENT_TIME) Omissible<
            RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.rewardId = rewardId;
        this.message = message;
        this.success = success;
        this.partnerRewardId = partnerRewardId;
        this.eventTime = eventTime;
        this.amount = amount;
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

    @JsonProperty(JSON_MESSAGE)
    public Omissible<Optional<String>> getMessage() {
        return message;
    }

    @JsonProperty(JSON_SUCCESS)
    public Omissible<Optional<String>> getSuccess() {
        return success;
    }

    @JsonProperty(JSON_PARTNER_REWARD_ID)
    public Omissible<RuntimeEvaluatable<RewardActionContext, Optional<String>>> getPartnerRewardId() {
        return partnerRewardId;
    }

    @JsonProperty(JSON_AMOUNT)
    public Omissible<RuntimeEvaluatable<RewardActionContext, Optional<BigDecimal>>> getAmount() {
        return amount;
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
        private Omissible<Optional<String>> message = Omissible.omitted();
        private Omissible<Optional<String>> success = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Optional<String>>> partnerRewardId =
            Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Optional<BigDecimal>>> amount = Omissible.omitted();

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

        public Builder withMessage(String message) {
            this.message = Omissible.of(Optional.ofNullable(message));
            return this;
        }

        public Builder withSuccess(String success) {
            this.success = Omissible.of(Optional.ofNullable(success));
            return this;
        }

        public Builder withPartnerRewardId(RuntimeEvaluatable<RewardActionContext, Optional<String>> partnerRewardId) {
            this.partnerRewardId = Omissible.of(partnerRewardId);
            return this;
        }

        public Builder withEventTime(RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
            this.eventTime = Omissible.of(eventTime);
            return this;
        }

        public Builder withAmount(RuntimeEvaluatable<RewardActionContext, Optional<BigDecimal>> amount) {
            this.amount = Omissible.of(amount);
            return this;
        }

        public CampaignControllerActionFulfillRewardCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionFulfillRewardCreateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                rewardId,
                message,
                success,
                partnerRewardId,
                amount,
                eventTime);
        }
    }
}
