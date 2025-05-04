package com.extole.client.rest.person.v2;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RelationshipV2Response {
    private static final String JSON_SHAREABLE_ID = "shareable_id";
    private static final String JSON_REASON = "reason";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_UPDATED_AT = "updated_at";
    private static final String JSON_PERSON = "person";

    private final String shareableId;
    private final String reason;
    private final String container;
    private final ZonedDateTime updatedAt;
    private final PersonV2Response person;

    public RelationshipV2Response(
        @JsonProperty(JSON_SHAREABLE_ID) String shareableId,
        @JsonProperty(JSON_REASON) String reason,
        @JsonProperty(JSON_CONTAINER) String container,
        @JsonProperty(JSON_UPDATED_AT) ZonedDateTime updatedAt,
        @JsonProperty(JSON_PERSON) PersonV2Response person) {
        this.shareableId = shareableId;
        this.reason = reason;
        this.container = container;
        this.updatedAt = updatedAt;
        this.person = person;
    }

    @Deprecated // TODO remove ENG-18496
    @Nullable
    @JsonProperty(JSON_SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
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

    @JsonProperty(JSON_PERSON)
    public PersonV2Response getPersonResponse() {
        return person;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
