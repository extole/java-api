package com.extole.client.rest.person.step;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PersonStepDataResponse;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.ToString;

public class PersonStepResponse {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EVENT_ID = "event_id";
    private static final String QUALITY = "quality";
    private static final String DATA = "data";
    private static final String SCOPE = "scope";
    private static final String EVENT_DATE = "event_date";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String PROGRAM = "program";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String JOURNEY_NAME = "journey_name";
    private static final String CONTAINER = "container";
    private static final String IS_PRIMARY = "is_primary";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String ROOT_EVENT_ID = "root_event_id";
    private static final String JOURNEY_KEY = "journey_key";

    private final String id;
    private final String name;
    private final String eventId;
    private final StepQuality quality;
    private final Map<String, PersonStepDataResponse> data;
    private final StepScope scope;
    private final ZonedDateTime eventDate;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Optional<String> program;
    private final Optional<String> campaignId;
    private final Optional<String> journeyName;
    private final String container;
    private final boolean isPrimary;
    private final String causeEventId;
    private final String rootEventId;
    private final Optional<JourneyKey> journeyKey;

    @JsonCreator
    public PersonStepResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(QUALITY) StepQuality quality,
        @JsonProperty(DATA) Map<String, PersonStepDataResponse> data,
        @JsonProperty(SCOPE) StepScope scope,
        @JsonProperty(EVENT_DATE) ZonedDateTime eventDate,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(PROGRAM) Optional<String> program,
        @JsonProperty(CAMPAIGN_ID) Optional<String> campaignId,
        @JsonProperty(JOURNEY_NAME) Optional<String> journeyName,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(IS_PRIMARY) boolean isPrimary,
        @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(ROOT_EVENT_ID) String rootEventId,
        @JsonProperty(JOURNEY_KEY) Optional<JourneyKey> journeyKey) {
        this.id = id;
        this.name = name;
        this.eventId = eventId;
        this.quality = quality;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
        this.scope = scope;
        this.eventDate = eventDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.program = program;
        this.campaignId = campaignId;
        this.journeyName = journeyName;
        this.container = container;
        this.isPrimary = isPrimary;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.journeyKey = journeyKey;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(EVENT_ID)
    public String getEventId() {
        return eventId;
    }

    @JsonProperty(QUALITY)
    public StepQuality getQuality() {
        return quality;
    }

    @JsonProperty(DATA)
    public Map<String, PersonStepDataResponse> getData() {
        return data;
    }

    @JsonProperty(SCOPE)
    public StepScope getScope() {
        return scope;
    }

    @JsonProperty(EVENT_DATE)
    public ZonedDateTime getEventDate() {
        return eventDate;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(PROGRAM)
    public Optional<String> getProgram() {
        return program;
    }

    @JsonProperty(CAMPAIGN_ID)
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JOURNEY_NAME)
    public Optional<String> getJourneyName() {
        return journeyName;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(IS_PRIMARY)
    public boolean isPrimary() {
        return isPrimary;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @JsonProperty(JOURNEY_KEY)
    public Optional<JourneyKey> getJourneyKey() {
        return journeyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private String id;
        private String name;
        private String eventId;
        private StepQuality quality;
        private final Map<String, PersonStepDataResponse> data = KeyCaseInsensitiveMap.create();
        private StepScope scope;
        private ZonedDateTime eventDate;
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;
        private Optional<String> program = Optional.empty();
        private Optional<String> campaignId = Optional.empty();
        private Optional<String> journeyName = Optional.empty();
        private String container;
        private boolean isPrimary;
        private String causeEventId;
        private String rootEventId;
        private Optional<JourneyKey> journeyKey = Optional.empty();

        private Builder() {

        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withQuality(StepQuality quality) {
            this.quality = quality;
            return this;
        }

        public Builder withData(Map<String, PersonStepDataResponse> data) {
            this.data.clear();
            this.data.putAll(data);
            return this;
        }

        public Builder withScope(StepScope scope) {
            this.scope = scope;
            return this;
        }

        public Builder withEventDate(ZonedDateTime eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder withProgram(String program) {
            this.program = Optional.ofNullable(program);
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = Optional.ofNullable(campaignId);
            return this;
        }

        public Builder withJourneyName(String journeyName) {
            this.journeyName = Optional.ofNullable(journeyName);
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withIsPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public Builder withCauseEventId(String causeEventId) {
            this.causeEventId = causeEventId;
            return this;
        }

        public Builder withRootEventId(String rootEventId) {
            this.rootEventId = rootEventId;
            return this;
        }

        public Builder withJourneyKey(JourneyKey journeyKey) {
            this.journeyKey = Optional.of(journeyKey);
            return this;
        }

        public PersonStepResponse build() {
            return new PersonStepResponse(id, name, eventId, quality, data, scope, eventDate, createdDate, updatedDate,
                program, campaignId, journeyName, container, isPrimary, causeEventId, rootEventId, journeyKey);
        }

    }

}
