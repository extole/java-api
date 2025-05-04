package com.extole.reporting.rest.batch;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

public class BatchJobQueryParams {

    private static final String NAME = "name";
    private static final String EVENT_NAME = "event_name";
    private static final String USER_ID = "user_id";
    private static final String TAGS = "tags";
    private static final String STATUS = "status";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Optional<String> name;
    private final Optional<String> eventName;
    private final Optional<String> userId;
    private final Optional<Set<String>> tags;
    private final Optional<Set<BatchJobStatus>> status;
    private final Integer limit;
    private final Integer offset;

    public BatchJobQueryParams(
        @Parameter(description = "Optional name filter.") @QueryParam(NAME) String name,
        @Parameter(description = "Optional tags filter.") @QueryParam(TAGS) Set<String> tags,
        @Parameter(description = "Optional statuses filter.") @QueryParam(STATUS) Set<BatchJobStatus> status,
        @Parameter(description = "Optional event name filter specified on the creation of batch job.")
        @QueryParam(EVENT_NAME) String eventName,
        @Parameter(description = "Optional user id filter.") @QueryParam(USER_ID) String userId,
        @Parameter(description = "Optional offset filter, defaults to 0.")
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 100.")
        @QueryParam(LIMIT) @DefaultValue("100") Integer limit) {
        this.name = Optional.ofNullable(name);
        this.eventName = Optional.ofNullable(eventName);
        this.userId = Optional.ofNullable(userId);
        this.tags = Optional.ofNullable(tags);
        this.status = Optional.ofNullable(status);
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(NAME)
    public Optional<String> getName() {
        return name;
    }

    @QueryParam(TAGS)
    public Optional<Set<String>> getTags() {
        return tags;
    }

    @QueryParam(STATUS)
    public Optional<Set<BatchJobStatus>> getStatus() {
        return status;
    }

    @QueryParam(LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @QueryParam(EVENT_NAME)
    public Optional<String> getEventName() {
        return eventName;
    }

    @QueryParam(USER_ID)
    public Optional<String> getUserId() {
        return userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String eventName;
        private String userId;
        private Set<String> tags;
        private Set<BatchJobStatus> statusSet;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withStatus(Set<BatchJobStatus> statusSet) {
            this.statusSet = statusSet;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public BatchJobQueryParams build() {
            return new BatchJobQueryParams(name, tags, statusSet, eventName, userId, offset, limit);
        }
    }
}
