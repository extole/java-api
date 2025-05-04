package com.extole.client.rest.campaign.controller.action.decline;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionDeclineCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_LEGACY_ACTION_ID = "legacy_action_id";
    private static final String JSON_PARTNER_EVENT_ID = "partner_event_id";
    private static final String JSON_EVENT_TYPE = "event_type";
    private static final String JSON_NOTE = "note";
    private static final String JSON_CAUSE_TYPE = "cause_type";
    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_POLLING_NAME = "polling_name";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<Optional<String>> legacyActionId;
    private final Omissible<String> partnerEventId;
    private final Omissible<String> eventType;
    private final Omissible<Optional<String>> note;
    private final Omissible<String> causeType;
    private final Omissible<Optional<String>> pollingId;
    private final Omissible<String> pollingName;

    @JsonCreator
    private CampaignControllerActionDeclineCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_LEGACY_ACTION_ID) Omissible<Optional<String>> legacyActionId,
        @JsonProperty(JSON_PARTNER_EVENT_ID) Omissible<String> partnerEventId,
        @JsonProperty(JSON_EVENT_TYPE) Omissible<String> eventType,
        @JsonProperty(JSON_NOTE) Omissible<Optional<String>> note,
        @JsonProperty(JSON_CAUSE_TYPE) Omissible<String> causeType,
        @JsonProperty(JSON_POLLING_ID) Omissible<Optional<String>> pollingId,
        @JsonProperty(JSON_POLLING_NAME) Omissible<String> pollingName) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.legacyActionId = legacyActionId;
        this.partnerEventId = partnerEventId;
        this.eventType = eventType;
        this.note = note;
        this.causeType = causeType;
        this.pollingId = pollingId;
        this.pollingName = pollingName;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_LEGACY_ACTION_ID)
    public Omissible<Optional<String>> getLegacyActionId() {
        return legacyActionId;
    }

    @JsonProperty(JSON_PARTNER_EVENT_ID)
    public Omissible<String> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(JSON_EVENT_TYPE)
    public Omissible<String> getEventType() {
        return eventType;
    }

    @JsonProperty(JSON_NOTE)
    public Omissible<Optional<String>> getNote() {
        return note;
    }

    @JsonProperty(JSON_CAUSE_TYPE)
    public Omissible<String> getCauseType() {
        return causeType;
    }

    @JsonProperty(JSON_POLLING_ID)
    public Omissible<Optional<String>> getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_POLLING_NAME)
    public Omissible<String> getPollingName() {
        return pollingName;
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
        private Omissible<Optional<String>> legacyActionId = Omissible.omitted();
        private Omissible<String> partnerEventId = Omissible.omitted();
        private Omissible<String> eventType = Omissible.omitted();
        private Omissible<Optional<String>> note = Omissible.omitted();
        private Omissible<String> causeType = Omissible.omitted();
        private Omissible<Optional<String>> pollingId = Omissible.omitted();
        private Omissible<String> pollingName = Omissible.omitted();

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

        public Builder withLegacyActionId(String legacyActionId) {
            this.legacyActionId = Omissible.of(Optional.ofNullable(legacyActionId));
            return this;
        }

        public Builder withPartnerEventId(String partnerEventId) {
            this.partnerEventId = Omissible.of(partnerEventId);
            return this;
        }

        public Builder withEventType(String eventType) {
            this.eventType = Omissible.of(eventType);
            return this;
        }

        public Builder withNote(String note) {
            this.note = Omissible.of(Optional.ofNullable(note));
            return this;
        }

        public Builder withCauseType(String causeType) {
            this.causeType = Omissible.of(causeType);
            return this;
        }

        public Builder withPollingId(String pollingId) {
            this.pollingId = Omissible.of(Optional.ofNullable(pollingId));
            return this;
        }

        public Builder withPollingName(String pollingName) {
            this.pollingName = Omissible.of(pollingName);
            return this;
        }

        public CampaignControllerActionDeclineCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionDeclineCreateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                legacyActionId,
                partnerEventId,
                eventType,
                note,
                causeType,
                pollingId,
                pollingName);
        }

    }

}
