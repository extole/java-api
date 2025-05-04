package com.extole.client.rest.person.v4;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;

public class PersonRelationshipV4Response {

    public static final String DATA_NAME_SHAREABLE_CODE = "shareable_code";
    public static final String DATA_NAME_SHARE_ID = "share_id";
    public static final String DATA_NAME_EVENT_NAME = "event_name";
    public static final String DATA_NAME_COUPON_CODE = "coupon_code";
    public static final String DATA_NAME_ADVOCATE_PARTNER_USER_ID = "advocate_partner_user_id";
    public static final String DATA_NAME_CHANNEL = "channel";
    public static final String DATA_NAME_PARTNER_EVENT_ID_NAME = "partner_event_id_name";
    public static final String DATA_NAME_PARTNER_EVENT_ID_VALUE = "partner_event_id_value";

    private static final String JSON_ROLE = "role";
    private static final String JSON_IS_PARENT = "is_parent";
    private static final String JSON_REASON = "reason";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_UPDATED_AT = "updated_at";
    private static final String JSON_OTHER_PERSON_ID = "other_person_id";
    private static final String JSON_DATA = "data";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_PROGRAM_LABEL = "program_label";
    private static final String JSON_CAUSE_EVENT_ID = "cause_event_id";
    private static final String JSON_ROOT_EVENT_ID = "root_event_id";

    private final String role;
    private final boolean isParent;
    private final String reason;
    private final String container;
    private final ZonedDateTime updatedAt;
    private final String otherPersonId;
    private final Map<String, Object> data;
    private final Optional<String> campaignId;
    private final Optional<String> programLabel;
    private final String causeEventId;
    private final String rootEventId;

    public PersonRelationshipV4Response(
        @JsonProperty(JSON_ROLE) String role,
        @JsonProperty(JSON_IS_PARENT) boolean isParent,
        @JsonProperty(JSON_REASON) String reason,
        @JsonProperty(JSON_CONTAINER) String container,
        @JsonProperty(JSON_UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(JSON_OTHER_PERSON_ID) String otherPersonId,
        @JsonProperty(JSON_DATA) Map<String, Object> data,
        @JsonProperty(JSON_CAMPAIGN_ID) Optional<String> campaignId,
        @JsonProperty(JSON_PROGRAM_LABEL) Optional<String> programLabel,
        @JsonProperty(JSON_CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(JSON_ROOT_EVENT_ID) String rootEventId) {
        this.role = role;
        this.isParent = isParent;
        this.reason = reason;
        this.container = container;
        this.updatedAt = updatedAt;
        this.otherPersonId = otherPersonId;
        this.data = ImmutableMap.copyOf(data);
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
    }

    @JsonProperty(JSON_ROLE)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_IS_PARENT)
    public boolean isParent() {
        return isParent;
    }

    @JsonProperty(JSON_REASON)
    public String getReason() {
        return reason;
    }

    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(JSON_UPDATED_AT)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty(JSON_OTHER_PERSON_ID)
    public String getOtherPersonId() {
        return otherPersonId;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, Object> getData() {
        return data;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(JSON_CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(JSON_ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String role;
        private boolean isParent;
        private String reason;
        private String container;
        private ZonedDateTime updatedAt;
        private String otherPersonId;
        private Map<String, Object> data = Maps.newHashMap();
        private Optional<String> campaignId = Optional.empty();
        private Optional<String> programLabel = Optional.empty();
        private String causeEventId;
        private String rootEventId;

        private Builder() {
        }

        public Builder withRole(String role) {
            this.role = role;
            return this;
        }

        public Builder withIsParent(boolean isParent) {
            this.isParent = isParent;
            return this;
        }

        public Builder withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withUpdatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withOtherPersonId(String otherPersonId) {
            this.otherPersonId = otherPersonId;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = Maps.newHashMap(data);
            return this;
        }

        public Builder withCampaignId(Optional<String> campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withProgramLabel(Optional<String> programLabel) {
            this.programLabel = programLabel;
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

        public PersonRelationshipV4Response build() {
            return new PersonRelationshipV4Response(role, isParent, reason, container, updatedAt, otherPersonId, data,
                campaignId, programLabel, causeEventId, rootEventId);
        }

    }

}
