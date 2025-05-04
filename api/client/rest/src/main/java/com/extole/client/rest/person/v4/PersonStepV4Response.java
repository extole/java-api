package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.client.rest.person.PartnerEventIdResponse;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.common.lang.ToString;

public class PersonStepV4Response {

    private static final String ID = "id";
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
    private static final String SCOPE = "scope";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String ROOT_EVENT_ID = "root_event_id";

    private final String id;
    private final String campaignId;
    private final String programLabel;
    private final String container;
    private final String stepName;
    private final String eventId;
    private final ZonedDateTime eventDate;
    private final String value;
    private final PartnerEventIdResponse partnerEventId;
    private final StepQuality quality;
    private final List<PersonDataV4Response> data;
    private final String journeyName;
    private final StepScope scope;
    private final String causeEventId;
    private final String rootEventId;

    @JsonCreator
    public PersonStepV4Response(
        @JsonProperty(ID) String id,
        @Nullable @JsonProperty(CAMPAIGN_ID) String campaignId,
        @Nullable @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(STEP_NAME) String stepName,
        @JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(EVENT_DATE) ZonedDateTime eventDate,
        @Nullable @JsonProperty(VALUE) String value,
        @Nullable @JsonProperty(PARTNER_EVENT_ID) PartnerEventIdResponse partnerEventId,
        @JsonProperty(QUALITY) StepQuality quality,
        @JsonProperty(DATA) List<PersonDataV4Response> data,
        @Nullable @JsonProperty(JOURNEY_NAME) String journeyName,
        @JsonProperty(SCOPE) StepScope scope,
        @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(ROOT_EVENT_ID) String rootEventId) {
        this.id = id;
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.container = container;
        this.stepName = stepName;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.value = value;
        this.partnerEventId = partnerEventId;
        this.quality = quality;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
        this.journeyName = journeyName;
        this.scope = scope;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
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
    public ZonedDateTime getEventDate() {
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
    public List<PersonDataV4Response> getData() {
        return data;
    }

    @Nullable
    @JsonProperty(JOURNEY_NAME)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(SCOPE)
    public StepScope getScope() {
        return scope;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
