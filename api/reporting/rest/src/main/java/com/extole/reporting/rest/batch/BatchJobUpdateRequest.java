package com.extole.reporting.rest.batch;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class BatchJobUpdateRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_SCOPES = "scopes";

    private final Omissible<String> name;
    private final Omissible<Set<BatchJobScope>> scopes;
    private final Omissible<Set<String>> tags;

    public BatchJobUpdateRequest(
        @Parameter(description = "BatchJob name") @JsonProperty(JSON_NAME) Omissible<String> name,
        @Parameter(description = "A set of scopes") @JsonProperty(JSON_SCOPES) Omissible<Set<BatchJobScope>> scopes,
        @Parameter(description = "A set of tags") @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags) {
        this.name = name;
        this.tags = tags;
        this.scopes = scopes;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPES)
    public Omissible<Set<BatchJobScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Set<BatchJobScope>> scopes = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withScopes(Set<BatchJobScope> scopes) {
            this.scopes = Omissible.of(scopes);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public BatchJobUpdateRequest build() {
            return new BatchJobUpdateRequest(name, scopes, tags);
        }
    }
}
