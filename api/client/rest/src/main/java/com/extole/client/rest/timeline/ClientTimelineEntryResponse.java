package com.extole.client.rest.timeline;

import java.time.ZonedDateTime;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public class ClientTimelineEntryResponse {
    private static final String JSON_USER_ID = "userId";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_DATE = "date";
    private static final String JSON_TAGS = "tags";

    private final String userId;
    private final String name;
    private final String description;
    private final ZonedDateTime date;
    private final Set<String> tags;

    @JsonCreator
    public ClientTimelineEntryResponse(
        @JsonProperty(JSON_USER_ID) String userId,
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_DATE) ZonedDateTime date,
        @JsonProperty(JSON_TAGS) Set<String> tags) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.tags = ImmutableSet.copyOf(tags);
    }

    @JsonProperty(JSON_USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(JSON_DESCRIPTION)
    public String getDescription() {
        return description;
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

        private String userId;
        private String name;
        private String description;
        private ZonedDateTime date;
        private Set<String> tags;

        private Builder() {
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
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

        public ClientTimelineEntryResponse build() {
            return new ClientTimelineEntryResponse(userId, name, description, date, tags);
        }
    }
}
