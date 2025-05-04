package com.extole.reporting.rest.report.runner;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PauseInfoResponse {
    private static final String JSON_USER_ID = "user_id";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_UPDATED_DATE = "updated_date";
    private final String userId;
    private final Optional<String> description;
    private final Instant updatedDate;

    public PauseInfoResponse(@JsonProperty(JSON_USER_ID) String userId,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_UPDATED_DATE) Instant updatedDate) {
        this.userId = userId;
        this.description = description;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public Instant getUpdatedDate() {
        return updatedDate;
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
        private Optional<String> description = Optional.empty();
        private Instant updatedDate;

        private Builder() {
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public Builder withUpdatedDate(Instant updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public PauseInfoResponse build() {
            return new PauseInfoResponse(userId, description, updatedDate);
        }
    }

}
