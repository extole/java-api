package com.extole.consumer.rest.me;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class StepResponse {

    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String CONTAINER = "container";
    private static final String STEP_NAME = "step_name";
    private static final String EVENT_ID = "event_id";
    private static final String EVENT_DATE = "event_date";
    private static final String VALUE = "value";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String QUALITY = "quality";
    private static final String DATA = "data";
    private static final String JOURNEY_NAME = "journey_name";

    private final String campaignId;
    private final String programLabel;
    private final String container;
    private final String stepName;
    private final String eventId;
    private final String eventDate;
    private final String value;
    private final PartnerEventIdResponse partnerEventId;
    private final StepQuality quality;
    private final Map<String, Object> data;
    private final String journeyName;

    @JsonCreator
    public StepResponse(@Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @Nullable @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(STEP_NAME) String stepName,
        @JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(EVENT_DATE) String eventDate,
        @Nullable @JsonProperty(VALUE) String value,
        @Nullable @JsonProperty(PARTNER_EVENT_ID) PartnerEventIdResponse partnerEventId,
        @JsonProperty(QUALITY) StepQuality quality,
        @JsonProperty(DATA) Map<String, Object> data,
        @Nullable @JsonProperty(JOURNEY_NAME) String journeyName) {
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.container = container;
        this.stepName = stepName;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.value = value;
        this.partnerEventId = partnerEventId;
        this.quality = quality;
        this.data = data;
        this.journeyName = journeyName;
    }

    @Nullable
    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @Nullable
    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(STEP_NAME)
    public String getStepName() {
        return stepName;
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(EVENT_DATE)
    public String getEventDate() {
        return eventDate;
    }

    @Nullable
    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @Nullable
    @JsonProperty(PARTNER_EVENT_ID)
    public PartnerEventIdResponse getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(QUALITY)
    public StepQuality getQuality() {
        return quality;
    }

    @JsonProperty(DATA)
    public Map<String, Object> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(JOURNEY_NAME)
    public String getJourneyName() {
        return journeyName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
