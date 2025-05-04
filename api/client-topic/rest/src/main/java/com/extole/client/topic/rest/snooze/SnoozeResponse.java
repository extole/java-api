package com.extole.client.topic.rest.snooze;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SnoozeResponse {

    private static final String SNOOZE_ID = "snooze_id";
    private static final String COMMENT = "comment";
    private static final String HAVING_EXACTLY_TAGS = "having_exactly_tags";
    private static final String EXPIRES_AT = "expires_at";

    private final String snoozeId;
    private final Optional<String> comment;
    private final Set<String> havingExactlyTags;
    private final ZonedDateTime expiresAt;

    public SnoozeResponse(@JsonProperty(SNOOZE_ID) String snoozeId,
        @JsonProperty(COMMENT) Optional<String> comment,
        @JsonProperty(HAVING_EXACTLY_TAGS) Set<String> havingExactlyTags,
        @JsonProperty(EXPIRES_AT) ZonedDateTime expiresAt) {
        this.snoozeId = snoozeId;
        this.comment = comment;
        this.havingExactlyTags = havingExactlyTags;
        this.expiresAt = expiresAt;
    }

    @JsonProperty(SNOOZE_ID)
    public String getId() {
        return snoozeId;
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
    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
