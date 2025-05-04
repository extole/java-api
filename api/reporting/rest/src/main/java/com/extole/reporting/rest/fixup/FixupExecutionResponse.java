package com.extole.reporting.rest.fixup;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class FixupExecutionResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_USER_ID = "user_id";
    private static final String JSON_START_DATE = "start_date";
    private static final String JSON_END_DATE = "end_date";
    private static final String JSON_STATUS = "status";
    private static final String JSON_ERROR_CODE = "error_code";
    private static final String JSON_ERROR_MESSAGE = "error_message";
    private static final String JSON_EVENT_COUNT = "event_count";
    private final String id;
    private final String userId;
    private final ZonedDateTime startDate;
    private final ZonedDateTime endDate;
    private final String status;
    private final String errorCode;
    private final String errorMessage;
    private final Long eventCount;

    @JsonCreator
    public FixupExecutionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_USER_ID) String userId,
        @JsonProperty(JSON_START_DATE) ZonedDateTime startDate,
        @JsonProperty(JSON_END_DATE) @Nullable ZonedDateTime endDate,
        @JsonProperty(JSON_STATUS) String status,
        @JsonProperty(JSON_ERROR_CODE) @Nullable String errorCode,
        @JsonProperty(JSON_ERROR_MESSAGE) @Nullable String errorMessage,
        @JsonProperty(JSON_EVENT_COUNT) @Nullable Long eventCount) {
        this.id = id;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.eventCount = eventCount;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(JSON_START_DATE)
    public ZonedDateTime getStartDate() {
        return startDate;
    }

    @JsonProperty(JSON_END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    @JsonProperty(JSON_STATUS)
    public String getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty(JSON_ERROR_CODE)
    public String getErrorCode() {
        return errorCode;
    }

    @Nullable
    @JsonProperty(JSON_ERROR_MESSAGE)
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    @JsonProperty(JSON_EVENT_COUNT)
    public Long getEventCount() {
        return eventCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String id;
        private String userId;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private String status;
        private String errorCode;
        private String errorMessage;
        private Long eventCount;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withStartDate(ZonedDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder withEventCount(long eventCount) {
            this.eventCount = Long.valueOf(eventCount);
            return this;
        }

        public FixupExecutionResponse build() {
            return new FixupExecutionResponse(id, userId, startDate, endDate, status, errorCode, errorMessage,
                eventCount);
        }

    }
}
