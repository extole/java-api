package com.extole.consumer.rest.zones.api;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class ZoneResponse {

    private static final String EVENT_ID = "event_id";
    private static final String DATA = "data";
    private static final String CAMPAIGN_ID = "campaign_id";

    private final String eventId;
    private final Map data;
    private final Optional<Id<?>> campaignId;

    @JsonCreator
    public ZoneResponse(@JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(DATA) Map data,
        @JsonProperty(CAMPAIGN_ID) Optional<Id<?>> campaignId) {
        this.eventId = eventId;
        this.data = data;
        this.campaignId = campaignId;
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    // Open ended Json as defined by creative
    @JsonProperty(DATA)
    public Map getData() {
        return data;
    }

    @JsonProperty(CAMPAIGN_ID)
    public Optional<Id<?>> getCampaignId() {
        return campaignId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
