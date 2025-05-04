package com.extole.client.rest.campaign.controller.action.incentivize.status.update;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.incentivize.status.update.IncentivizeStatusUpdateActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.IncentivizeActionType;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionIncentivizeStatusUpdateUpdateRequest extends ComponentElementRequest {

    private static final String QUALITY = "quality";
    private static final String ENABLED = "enabled";
    private static final String LEGACY_ACTION_ID = "legacy_action_id";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String REVIEW_STATUS = "review_status";
    private static final String MESSAGE = "message";
    private static final String MOVE_TO_PENDING = "move_to_pending";
    private static final String DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> legacyActionId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>>>> eventType;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> partnerEventId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String>>> reviewStatus;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> message;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> moveToPending;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>>>> data;

    @JsonCreator
    public CampaignControllerActionIncentivizeStatusUpdateUpdateRequest(
        @JsonProperty(QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(LEGACY_ACTION_ID) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> legacyActionId,
        @JsonProperty(EVENT_TYPE) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>>>> eventType,
        @JsonProperty(PARTNER_EVENT_ID) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> partnerEventId,
        @JsonProperty(REVIEW_STATUS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String>>> reviewStatus,
        @JsonProperty(MESSAGE) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> message,
        @JsonProperty(MOVE_TO_PENDING) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> moveToPending,
        @JsonProperty(DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>>>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.legacyActionId = legacyActionId;
        this.partnerEventId = partnerEventId;
        this.eventType = eventType;
        this.reviewStatus = reviewStatus;
        this.message = message;
        this.moveToPending = moveToPending;
        this.data = data;
    }

    @JsonProperty(QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(LEGACY_ACTION_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<String>>>>
        getLegacyActionId() {
        return legacyActionId;
    }

    @JsonProperty(EVENT_TYPE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<IncentivizeActionType>>>>
        getEventType() {
        return eventType;
    }

    @JsonProperty(PARTNER_EVENT_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext,
            Optional<String>>>>
        getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(REVIEW_STATUS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String>>>
        getReviewStatus() {
        return reviewStatus;
    }

    @JsonProperty(MESSAGE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>>
        getMessage() {
        return message;
    }

    @JsonProperty(MOVE_TO_PENDING)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> isMoveToPending() {
        return moveToPending;
    }

    @JsonProperty(DATA)
    public Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>>>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static CampaignControllerActionIncentivizeStatusUpdateUpdateRequest.Builder builder() {
        return new CampaignControllerActionIncentivizeStatusUpdateUpdateRequest.Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> legacyActionId =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>>>> eventType =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> partnerEventId =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String>>> reviewStatus =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>>> message =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> moveToPending =
            Omissible.omitted();
        private Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>>>> data = Omissible.omitted();

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

        public Builder withLegacyActionId(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>> legacyActionId) {
            this.legacyActionId = Omissible.of(legacyActionId);
            return this;
        }

        public Builder withEventType(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<IncentivizeActionType>>> eventType) {
            this.eventType = Omissible.of(eventType);
            return this;
        }

        public Builder withPartnerEventId(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>> partnerEventId) {
            this.partnerEventId = Omissible.of(partnerEventId);
            return this;
        }

        public Builder withReviewStatus(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, String>> reviewStatus) {
            this.reviewStatus = Omissible.of(reviewStatus);
            return this;
        }

        public Builder withMessage(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<String>>> message) {
            this.message = Omissible.of(message);
            return this;
        }

        public Builder
            withMoveToPending(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> moveToPending) {
            this.moveToPending = Omissible.of(moveToPending);
            return this;
        }

        public Builder withData(Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<IncentivizeStatusUpdateActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        @Override
        public CampaignControllerActionIncentivizeStatusUpdateUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionIncentivizeStatusUpdateUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                legacyActionId,
                eventType,
                partnerEventId,
                reviewStatus,
                message,
                moveToPending,
                data);
        }

    }

}
