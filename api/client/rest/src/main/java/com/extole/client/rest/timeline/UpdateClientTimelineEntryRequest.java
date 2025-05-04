package com.extole.client.rest.timeline;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public class UpdateClientTimelineEntryRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_DATE = "date";
    private static final String JSON_TAGS = "tags";

    private final String name;
    private final String description;
    private final ZonedDateTime date;
    private final Set<String> tags;

    @JsonCreator
    public UpdateClientTimelineEntryRequest(
        @Nullable @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description,
        @Nullable @JsonProperty(JSON_DATE) ZonedDateTime date,
        @Nullable @JsonProperty(JSON_TAGS) Set<String> tags) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : null;
    }

    @JsonProperty(JSON_NAME)
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @JsonProperty(JSON_DATE)
    public Optional<ZonedDateTime> getDate() {
        return Optional.ofNullable(date);
    }

    @JsonProperty(JSON_TAGS)
    public Optional<Set<String>> getTags() {
        return Optional.ofNullable(tags);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String description;
        private ZonedDateTime date;
        private Set<String> tags;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withDate(ZonedDateTime date) {
            this.date = date;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public UpdateClientTimelineEntryRequest build() {
            return new UpdateClientTimelineEntryRequest(name, description, date, tags);
        }
    }
}
