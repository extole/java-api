package com.extole.client.rest.person;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class PersonJourneyResponse {

    public static final String DATA_NAME_ENTRY_REASON = "entry_reason";
    public static final String DATA_NAME_ENTRY_ZONE = "entry_zone";
    public static final String DATA_NAME_LAST_ZONE = "last_zone";
    public static final String DATA_NAME_ENTRY_SHARE_ID = "entry_share_id";
    public static final String DATA_NAME_LAST_SHARE_ID = "last_share_id";
    public static final String DATA_NAME_ENTRY_SHAREABLE_ID = "entry_shareable_id";
    public static final String DATA_NAME_LAST_SHAREABLE_ID = "last_shareable_id";
    public static final String DATA_NAME_ENTRY_ADVOCATE_CODE = "entry_advocate_code";
    public static final String DATA_NAME_LAST_ADVOCATE_CODE = "last_advocate_code";
    public static final String DATA_NAME_ENTRY_PROMOTABLE_CODE = "entry_promotable_code";
    public static final String DATA_NAME_LAST_PROMOTABLE_CODE = "last_promotable_code";
    public static final String DATA_NAME_ENTRY_CONSUMER_EVENT_ID = "entry_consumer_event_id";
    public static final String DATA_NAME_LAST_CONSUMER_EVENT_ID = "last_consumer_event_id";
    public static final String DATA_NAME_ENTRY_PROFILE_ID = "entry_profile_id";
    public static final String DATA_NAME_LAST_PROFILE_ID = "last_profile_id";
    public static final String DATA_NAME_ENTRY_ADVOCATE_PARTNER_ID = "entry_advocate_partner_id";
    public static final String DATA_NAME_LAST_ADVOCATE_PARTNER_ID = "last_advocate_partner_id";
    public static final String DATA_NAME_ENTRY_COUPON_CODE = "entry_coupon_code";
    public static final String DATA_NAME_LAST_COUPON_CODE = "last_coupon_code";
    public static final String DATA_NAME_ENTRY_REFERRAL_REASON = "entry_referral_reason";
    public static final String DATA_NAME_LAST_REFERRAL_REASON = "last_referral_reason";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PROGRAM = "program";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String CONTAINER = "container";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String DATA = "data";
    private static final String KEY = "key";

    private final String id;
    private final String name;
    private final String program;
    private final String campaignId;
    private final String container;
    private final Map<String, PersonJourneyDataResponse> data;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Optional<JourneyKey> key;

    public PersonJourneyResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(PROGRAM) String program,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(DATA) Map<String, PersonJourneyDataResponse> data,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(KEY) Optional<JourneyKey> key) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.campaignId = campaignId;
        this.container = container;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.key = key;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PROGRAM)
    public String getProgram() {
        return program;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(DATA)
    public Map<String, PersonJourneyDataResponse> getData() {
        return data;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(KEY)
    public Optional<JourneyKey> getKey() {
        return key;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
