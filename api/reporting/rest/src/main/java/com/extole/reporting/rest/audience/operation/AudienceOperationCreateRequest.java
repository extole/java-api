package com.extole.reporting.rest.audience.operation;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class AudienceOperationCreateRequest {

    private static final String TYPE = "type";
    private static final String TAGS = "tags";
    private static final String DATA_SOURCE = "data_source";

    private final AudienceOperationType type;
    private final Omissible<Set<String>> tags;
    private final AudienceOperationDataSourceRequest dataSource;

    public AudienceOperationCreateRequest(@JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(DATA_SOURCE) AudienceOperationDataSourceRequest dataSource) {
        this.type = type;
        this.tags = tags;
        this.dataSource = dataSource;
    }

    @JsonProperty(TYPE)
    public AudienceOperationType getType() {
        return type;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(DATA_SOURCE)
    public AudienceOperationDataSourceRequest getDataSource() {
        return dataSource;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AudienceOperationType type;
        private Omissible<Set<String>> tags = Omissible.omitted();
        private AudienceOperationDataSourceRequest dataSource;

        private Builder() {
        }

        public Builder withType(AudienceOperationType type) {
            this.type = type;
            return this;
        }

        public Builder withTags(Omissible<Set<String>> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withDataSource(AudienceOperationDataSourceRequest dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public AudienceOperationCreateRequest build() {
            return new AudienceOperationCreateRequest(type, tags, dataSource);
        }

    }

}
