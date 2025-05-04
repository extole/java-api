package com.extole.client.rest.campaign.controller.action.incentivize;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.incentivize.IncentivizeActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionIncentivizeUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_INCENTIVIZE_ACTION_TYPE = "incentivize_action_type";
    private static final String JSON_OVERRIDES = "overrides";
    private static final String JSON_ACTION_NAME = "action_name";
    private static final String JSON_DATA = "data";
    private static final String JSON_REVIEW_STATUS = "review_status";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        IncentivizeActionType>> incentivizeActionType;
    private final Omissible<Map<IncentivizeActionOverrideType, String>> overrides;
    private final Omissible<Optional<String>> actionName;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>>> data;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus>> reviewStatus;

    public CampaignControllerActionIncentivizeUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_INCENTIVIZE_ACTION_TYPE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, IncentivizeActionType>> incentivizeActionType,
        @JsonProperty(JSON_OVERRIDES) Omissible<Map<IncentivizeActionOverrideType, String>> overrides,
        @JsonProperty(JSON_ACTION_NAME) Omissible<Optional<String>> actionName,
        @JsonProperty(JSON_DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>>> data,
        @JsonProperty(JSON_REVIEW_STATUS) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus>> reviewStatus) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.incentivizeActionType = incentivizeActionType;
        this.overrides = overrides;
        this.actionName = actionName;
        this.data = data;
        this.reviewStatus = reviewStatus;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_INCENTIVIZE_ACTION_TYPE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, IncentivizeActionType>>
        getIncentivizeActionType() {
        return incentivizeActionType;
    }

    @JsonProperty(JSON_OVERRIDES)
    public Omissible<Map<IncentivizeActionOverrideType, String>> getOverrides() {
        return overrides;
    }

    @JsonProperty(JSON_ACTION_NAME)
    public Omissible<Optional<String>> getActionName() {
        return actionName;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>>> getData() {
        return data;
    }

    @JsonProperty(JSON_REVIEW_STATUS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus>> getReviewStatus() {
        return reviewStatus;
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
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            IncentivizeActionType>> incentivizeActionType = Omissible.omitted();
        private Omissible<Map<IncentivizeActionOverrideType, String>> overrides = Omissible.omitted();
        private Omissible<Optional<String>> actionName = Omissible.omitted();
        private Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>>> data = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus>> reviewStatus =
            Omissible.omitted();

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

        public Builder withIncentivizeActionType(
            BuildtimeEvaluatable<ControllerBuildtimeContext, IncentivizeActionType> incentivizeActionType) {
            this.incentivizeActionType = Omissible.of(incentivizeActionType);
            return this;
        }

        public Builder withOverrides(Map<IncentivizeActionOverrideType, String> overrides) {
            this.overrides = Omissible.of(overrides);
            return this;
        }

        public Builder withActionName(String actionName) {
            this.actionName = Omissible.of(Optional.ofNullable(actionName));
            return this;
        }

        public Builder withData(Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withReviewStatus(BuildtimeEvaluatable<ControllerBuildtimeContext, ReviewStatus> reviewStatus) {
            this.reviewStatus = Omissible.of(reviewStatus);
            return this;
        }

        public CampaignControllerActionIncentivizeUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionIncentivizeUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                incentivizeActionType,
                overrides,
                actionName,
                data,
                reviewStatus);
        }

    }

}
