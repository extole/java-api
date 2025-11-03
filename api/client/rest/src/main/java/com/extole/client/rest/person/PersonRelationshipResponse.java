package com.extole.client.rest.person;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;

public class PersonRelationshipResponse {

    public static final String DATA_NAME_SHAREABLE_CODE = "shareable_code";
    public static final String DATA_NAME_SHARE_ID = "share_id";
    public static final String DATA_NAME_EVENT_NAME = "event_name";
    public static final String DATA_NAME_COUPON_CODE = "coupon_code";
    public static final String DATA_NAME_ADVOCATE_PARTNER_USER_ID = "advocate_partner_user_id";
    public static final String DATA_NAME_CHANNEL = "channel";
    public static final String DATA_NAME_PARTNER_EVENT_ID_NAME = "partner_event_id_name";
    public static final String DATA_NAME_PARTNER_EVENT_ID_VALUE = "partner_event_id_value";
    public static final String DATA_NAME_REASON = "reason";

    private static final String JSON_ID = "id";
    private static final String JSON_MY_ROLE = "my_role";
    private static final String JSON_OTHER_PERSON_ID = "other_person_id";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_PROGRAM = "program";
    private static final String JSON_CAUSE_EVENT_ID = "cause_event_id";
    private static final String JSON_ROOT_EVENT_ID = "root_event_id";
    private static final String JSON_DATA = "data";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";

    private final String id;
    private final PersonReferralRole myRole;
    private final String otherPersonId;
    private final String container;
    private final Optional<String> program;
    private final Optional<String> campaignId;
    private final String rootEventId;
    private final String causeEventId;
    private final Map<String, PersonRelationshipDataResponse> data;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public PersonRelationshipResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_MY_ROLE) PersonReferralRole myRole,
        @JsonProperty(JSON_OTHER_PERSON_ID) String otherPersonId,
        @JsonProperty(JSON_CONTAINER) String container,
        @JsonProperty(JSON_PROGRAM) Optional<String> program,
        @JsonProperty(JSON_CAMPAIGN_ID) Optional<String> campaignId,
        @JsonProperty(JSON_ROOT_EVENT_ID) String rootEventId,
        @JsonProperty(JSON_CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(JSON_DATA) Map<String, PersonRelationshipDataResponse> data,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate) {
        this.id = id;
        this.myRole = myRole;
        this.otherPersonId = otherPersonId;
        this.container = container;
        this.program = program;
        this.campaignId = campaignId;
        this.rootEventId = rootEventId;
        this.causeEventId = causeEventId;
        this.data = ImmutableMap.copyOf(data);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_MY_ROLE)
    public PersonReferralRole getMyRole() {
        return myRole;
    }

    @JsonProperty(JSON_OTHER_PERSON_ID)
    public String getOtherPersonId() {
        return otherPersonId;
    }

    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(JSON_PROGRAM)
    public Optional<String> getProgram() {
        return program;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @JsonProperty(JSON_CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, PersonRelationshipDataResponse> getData() {
        return data;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id;
        private PersonReferralRole myRole;
        private String otherPersonId;
        private String container;
        private Optional<String> program = Optional.empty();
        private Optional<String> campaignId = Optional.empty();
        private String rootEventId;
        private String causeEventId;
        private Map<String, PersonRelationshipDataResponse> data = Maps.newHashMap();
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withMyRole(PersonReferralRole myRole) {
            this.myRole = myRole;
            return this;
        }

        public Builder withOtherPersonId(String otherPersonId) {
            this.otherPersonId = otherPersonId;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withProgram(Optional<String> program) {
            this.program = program;
            return this;
        }

        public Builder withCampaignId(Optional<String> campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withRootEventId(String rootEventId) {
            this.rootEventId = rootEventId;
            return this;
        }

        public Builder withCauseEventId(String causeEventId) {
            this.causeEventId = causeEventId;
            return this;
        }

        public Builder withData(Map<String, PersonRelationshipDataResponse> data) {
            this.data = Maps.newHashMap(data);
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

        public PersonRelationshipResponse build() {
            return new PersonRelationshipResponse(id, myRole, otherPersonId, container,
                program, campaignId, rootEventId, causeEventId, data, createdDate, updatedDate);
        }

    }

}
