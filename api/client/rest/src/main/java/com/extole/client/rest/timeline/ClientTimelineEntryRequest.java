package com.extole.client.rest.timeline;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public class ClientTimelineEntryRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_DATE = "date";

    private final String name;
    private final String description;
    private final ZonedDateTime date;
    private final Set<String> tags;

    @JsonCreator
    public ClientTimelineEntryRequest(
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_DATE) ZonedDateTime date,
        @JsonProperty(JSON_TAGS) Set<String> tags) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : Collections.emptySet();
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @JsonProperty(JSON_DATE)
    public ZonedDateTime getDate() {
        return date;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
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

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withDate(ZonedDateTime date) {
            this.date = date;
            return this;
        }

        public ClientTimelineEntryRequest build() {
            return new ClientTimelineEntryRequest(name, description, date, tags);
        }
    }
}
