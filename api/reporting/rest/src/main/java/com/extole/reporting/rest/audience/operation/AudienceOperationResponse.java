package com.extole.reporting.rest.audience.operation;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.audience.operation.AudienceOperation;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class AudienceOperationResponse {

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String TAGS = "tags";
    private static final String DATA_SOURCE = "data_source";

    private final Id<AudienceOperation> id;
    private final AudienceOperationType type;
    private final Set<String> tags;
    private final AudienceOperationDataSourceResponse dataSource;

    public AudienceOperationResponse(@JsonProperty(ID) Id<AudienceOperation> id,
        @JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA_SOURCE) AudienceOperationDataSourceResponse dataSource) {
        this.id = id;
        this.type = type;
        this.tags = ImmutableSet.copyOf(tags);
        this.dataSource = dataSource;
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

    @JsonProperty(DATA_SOURCE)
    public AudienceOperationDataSourceResponse getDataSource() {
        return dataSource;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
