package com.extole.consumer.rest.me;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.person.PublicPersonResponse;

public class RelationshipResponse {
    private static final String JSON_SHAREABLE_ID = "shareable_id";
    private static final String JSON_REASON = "reason";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_UPDATED_AT = "updated_at";
    private static final String JSON_PERSON = "person";

    private final String shareableId;
    private final String reason;
    private final String container;
    private final String updatedAt;
    private final PublicPersonResponse publicPersonResponse;

    public RelationshipResponse(
        @JsonProperty(JSON_SHAREABLE_ID) String shareableId,
        @JsonProperty(JSON_REASON) String reason,
        @JsonProperty(JSON_CONTAINER) String container,
        @JsonProperty(JSON_UPDATED_AT) String updatedAt,
        @JsonProperty(JSON_PERSON) PublicPersonResponse publicPersonResponse) {
        this.shareableId = shareableId;
        this.reason = reason;
        this.container = container;
        this.updatedAt = updatedAt;
        this.publicPersonResponse = publicPersonResponse;
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
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty(JSON_PERSON)
    public PublicPersonResponse getPersonResponse() {
        return publicPersonResponse;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
