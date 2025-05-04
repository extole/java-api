package com.extole.client.topic.rest.snooze;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SnoozeRequest {

    private static final String COMMENT = "comment";
    private static final String HAVING_EXACTLY_TAGS = "having_exactly_tags";
    private static final String EXPIRES_AT = "expires_at";

    private final Optional<String> comment;
    private final Set<String> havingExactlyTags;
    private final Optional<ZonedDateTime> expiresAt;

    public SnoozeRequest(@JsonProperty(COMMENT) Optional<String> comment,
        @JsonProperty(HAVING_EXACTLY_TAGS) Set<String> havingExactlyTags,
        @JsonProperty(EXPIRES_AT) Optional<ZonedDateTime> expiresAt) {
        this.comment = comment;
        this.havingExactlyTags = havingExactlyTags == null ? new HashSet<>() : havingExactlyTags;
        this.expiresAt = expiresAt;
    }

    @JsonProperty(COMMENT)
    public Optional<String> getComment() {
        return comment;
    }

    @JsonProperty(HAVING_EXACTLY_TAGS)
    public Set<String> getHavingExactlyTags() {
        return havingExactlyTags;
    }

    @JsonProperty(EXPIRES_AT)
    public Optional<ZonedDateTime> getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private Optional<String> comment = Optional.empty();
        private final Set<String> havingExactlyTags = new HashSet<>();
        private Optional<ZonedDateTime> expiresAt = Optional.empty();

        public Builder() {
        }

        public Builder withComment(String comment) {
            this.comment = Optional.of(comment);
            return this;
        }

        public Builder addTag(String tag) {
            this.havingExactlyTags.add(tag);
            return this;
        }

        public Builder withExpiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = Optional.of(expiresAt);
            return this;
        }

        public SnoozeRequest build() {
            return new SnoozeRequest(comment, havingExactlyTags, expiresAt);
        }
    }
}
