package com.extole.client.rest.campaign.controller.action.earn.reward;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public final class CampaignControllerActionEarnRewardUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_REWARD_NAME = "reward_name";
    private static final String JSON_REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_SLOTS = "slots";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_DATA = "data";
    private static final String JSON_PROGRAM_DOMAIN_ID = "program_domain_id";
    private static final String JSON_SANDBOX = "sandbox";
    private static final String JSON_VALUE_OF_EVENT_BEING_REWARDED = "value_of_event_being_rewarded";
    private static final String JSON_EVENT_TIME = "event_time";
    private static final String JSON_REWARD_ACTION_ID = "reward_action_id";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> rewardName;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> rewardSupplierId;
    private final Omissible<String> pollingId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Set<String>>>> tags;
    private final Omissible<Map<String,
        BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>>>> data;
    private final Omissible<String> programDomainId;
    private final Omissible<String> sandbox;
    private final Omissible<BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> valueOfEventBeingRewarded;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime;
    private final Omissible<RuntimeEvaluatable<RewardActionContext, Id<?>>> rewardActionId;

    private CampaignControllerActionEarnRewardUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_REWARD_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> rewardName,
        @JsonProperty(JSON_REWARD_SUPPLIER_ID) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> rewardSupplierId,
        @JsonProperty(JSON_POLLING_ID) Omissible<String> pollingId,
        @JsonProperty(JSON_SLOTS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Set<String>>>> slots,
        @JsonProperty(JSON_TAGS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Set<String>>>> tags,
        @JsonProperty(JSON_DATA) Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<RewardActionContext, Optional<Object>>>>> data,
        @JsonProperty(JSON_PROGRAM_DOMAIN_ID) Omissible<String> programDomainId,
        @JsonProperty(JSON_SANDBOX) Omissible<String> sandbox,
        @JsonProperty(JSON_VALUE_OF_EVENT_BEING_REWARDED) Omissible<BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> valueOfEventBeingRewarded,
        @JsonProperty(JSON_EVENT_TIME) Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime,
        @JsonProperty(JSON_REWARD_ACTION_ID) Omissible<RuntimeEvaluatable<RewardActionContext, Id<?>>> rewardActionId) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.rewardName = rewardName;
        this.rewardSupplierId = rewardSupplierId;
        this.pollingId = pollingId;
        this.tags = !tags.isOmitted() ? tags : slots;
        this.data = data;
        this.programDomainId = programDomainId;
        this.sandbox = sandbox;
        this.valueOfEventBeingRewarded = valueOfEventBeingRewarded;
        this.eventTime = eventTime;
        this.rewardActionId = rewardActionId;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_REWARD_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getRewardName() {
        return rewardName;
    }

    @JsonProperty(JSON_REWARD_SUPPLIER_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(JSON_POLLING_ID)
    public Omissible<String> getPollingId() {
        return pollingId;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(JSON_SLOTS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Set<String>>>> getSlots() {
        return tags;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Set<String>>>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String,
        BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>>>>
        getData() {
        return data;
    }

    @JsonProperty(JSON_PROGRAM_DOMAIN_ID)
    public Omissible<String> getProgramDomainId() {
        return programDomainId;
    }

    @JsonProperty(JSON_SANDBOX)
    public Omissible<String> getSandbox() {
        return sandbox;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(JSON_VALUE_OF_EVENT_BEING_REWARDED)
    public Omissible<BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> getValueOfEventBeingRewarded() {
        return valueOfEventBeingRewarded;
    }

    @JsonProperty(JSON_EVENT_TIME)
    public Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> getEventTime() {
        return eventTime;
    }

    @JsonProperty(JSON_REWARD_ACTION_ID)
    public Omissible<RuntimeEvaluatable<RewardActionContext, Id<?>>> getRewardActionId() {
        return rewardActionId;
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
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> rewardName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>>> rewardSupplierId =
            Omissible.omitted();
        private Omissible<String> pollingId = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Set<String>>>> tags = Omissible.omitted();
        private Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<RewardActionContext, Optional<Object>>>>> data = Omissible.omitted();
        private Omissible<String> programDomainId = Omissible.omitted();
        private Omissible<String> sandbox = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> valueOfEventBeingRewarded = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Optional<Instant>>> eventTime = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<RewardActionContext, Id<?>>> rewardActionId = Omissible.omitted();

        private Builder() {
        }

        public Builder withRewardName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> rewardName) {
            this.rewardName = Omissible.of(rewardName);
            return this;
        }

        public Builder
            withRewardSupplierId(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> rewardSupplierId) {
            this.rewardSupplierId = Omissible.of(rewardSupplierId);
            return this;
        }

        public Builder clearRewardSupplierId() {
            this.rewardSupplierId = Omissible.of(Provided.optionalEmpty());
            return this;
        }

        public Builder withPollingId(String pollingId) {
            this.pollingId = Omissible.of(pollingId);
            return this;
        }

        public Builder withTags(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Set<String>>> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(Provided.nestedOf(tags));
            return this;
        }

        public Builder withData(
            Map<String,
                BuildtimeEvaluatable<ControllerBuildtimeContext,
                    RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withProgramDomainId(String programDomainId) {
            this.programDomainId = Omissible.of(programDomainId);
            return this;
        }

        public Builder withSandbox(String sandbox) {
            this.sandbox = Omissible.of(sandbox);
            return this;
        }

        public Builder withValueOfEventBeingRewarded(
            BuildtimeEvaluatable<
                ControllerBuildtimeContext,
                RuntimeEvaluatable<RewardActionContext, Optional<Object>>> valueOfEventBeingRewarded) {
            this.valueOfEventBeingRewarded = Omissible.of(valueOfEventBeingRewarded);
            return this;
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withEventTime(RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
            this.eventTime = Omissible.of(eventTime);
            return this;
        }

        public Builder withRewardActionId(RuntimeEvaluatable<RewardActionContext, Id<?>> rewardActionId) {
            this.rewardActionId = Omissible.of(rewardActionId);
            return this;
        }

        public CampaignControllerActionEarnRewardUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionEarnRewardUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                rewardName,
                rewardSupplierId,
                pollingId,
                tags,
                tags,
                data,
                programDomainId,
                sandbox,
                valueOfEventBeingRewarded,
                eventTime,
                rewardActionId);
        }

    }

}
