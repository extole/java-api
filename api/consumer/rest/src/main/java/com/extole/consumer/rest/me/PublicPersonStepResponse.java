package com.extole.consumer.rest.me;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PublicPersonStepResponse {

    private static final String STEP_NAME = "step_name";
    private static final String EVENT_DATE = "event_date";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String DATA = "data";
    private static final String JOURNEY_NAME = "journey_name";

    private final String stepName;
    private final String eventDate;
    private final String programLabel;
    private final String campaignId;
    private final Map<String, Object> data;
    private final String journeyName;

    public PublicPersonStepResponse(
        @JsonProperty(STEP_NAME) String stepName,
        @JsonProperty(EVENT_DATE) String eventDate,
        @Nullable @JsonProperty(PROGRAM_LABEL) String programLabel,
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(DATA) Map<String, Object> data,
        @Nullable @JsonProperty(JOURNEY_NAME) String journeyName) {
        this.stepName = stepName;
        this.eventDate = eventDate;
        this.programLabel = programLabel;
        this.campaignId = campaignId;
        this.data = data;
        this.journeyName = journeyName;
    }

    @JsonProperty(STEP_NAME)
    public String getStepName() {
        return stepName;
    }

    @JsonProperty(EVENT_DATE)
    public String getEventDate() {
        return eventDate;
    }

    @Nullable
    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @Nullable
    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
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
