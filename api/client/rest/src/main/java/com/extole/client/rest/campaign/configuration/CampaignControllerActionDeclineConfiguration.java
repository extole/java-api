package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionDeclineConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_LEGACY_ACTION_ID = "legacy_action_id";
    private static final String JSON_PARTNER_EVENT_ID = "partner_event_id";
    private static final String JSON_EVENT_TYPE = "event_type";
    private static final String JSON_NOTE = "note";
    private static final String JSON_CAUSE_TYPE = "cause_type";
    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_POLLING_NAME = "polling_name";

    private final Optional<String> legacyActionId;
    private final String partnerEventId;
    private final String eventType;
    private final Optional<String> note;
    private final String causeType;
    private final Optional<String> pollingId;
    private final String pollingName;

    @JsonCreator
    public CampaignControllerActionDeclineConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_LEGACY_ACTION_ID) Optional<String> legacyActionId,
        @JsonProperty(JSON_PARTNER_EVENT_ID) String partnerEventId,
        @JsonProperty(JSON_EVENT_TYPE) String eventType,
        @JsonProperty(JSON_NOTE) Optional<String> note,
        @JsonProperty(JSON_CAUSE_TYPE) String causeType,
        @JsonProperty(JSON_POLLING_ID) Optional<String> pollingId,
        @JsonProperty(JSON_POLLING_NAME) String pollingName,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.DECLINE, quality, enabled, componentReferences);
        this.legacyActionId = legacyActionId;
        this.partnerEventId = partnerEventId;
        this.eventType = eventType;
        this.note = note;
        this.causeType = causeType;
        this.pollingId = pollingId;
        this.pollingName = pollingName;
    }

    @JsonProperty(JSON_LEGACY_ACTION_ID)
    public Optional<String> getLegacyActionId() {
        return legacyActionId;
    }

    @JsonProperty(JSON_PARTNER_EVENT_ID)
    public String getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(JSON_EVENT_TYPE)
    public String getEventType() {
        return eventType;
    }

    @JsonProperty(JSON_NOTE)
    public Optional<String> getNote() {
        return note;
    }

    @JsonProperty(JSON_CAUSE_TYPE)
    public String getCauseType() {
        return causeType;
    }

    @JsonProperty(JSON_POLLING_ID)
    public Optional<String> getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_POLLING_NAME)
    public String getPollingName() {
        return pollingName;
    }

}
