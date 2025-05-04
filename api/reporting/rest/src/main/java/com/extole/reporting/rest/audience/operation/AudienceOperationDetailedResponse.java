package com.extole.reporting.rest.audience.operation;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.audience.operation.AudienceOperation;
import com.extole.api.user.User;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class AudienceOperationDetailedResponse {

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String TAGS = "tags";
    private static final String USER_ID = "user_id";
    private static final String DATA_SOURCE = "data_source";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final Id<AudienceOperation> id;
    private final AudienceOperationType type;
    private final Set<String> tags;
    private final Id<User> userId;
    private final AudienceOperationDataSourceResponse dataSource;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public AudienceOperationDetailedResponse(@JsonProperty(ID) Id<AudienceOperation> id,
        @JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(USER_ID) Id<User> userId,
        @JsonProperty(DATA_SOURCE) AudienceOperationDataSourceResponse dataSource,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.id = id;
        this.type = type;
        this.tags = ImmutableSet.copyOf(tags);
        this.userId = userId;
        this.dataSource = dataSource;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(ID)
    public Id<AudienceOperation> getId() {
        return id;
    }

    @JsonProperty(TYPE)
    public AudienceOperationType getType() {
        return type;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(USER_ID)
    public Id<User> getUserId() {
        return userId;
    }

    @JsonProperty(DATA_SOURCE)
    public AudienceOperationDataSourceResponse getDataSource() {
        return dataSource;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
