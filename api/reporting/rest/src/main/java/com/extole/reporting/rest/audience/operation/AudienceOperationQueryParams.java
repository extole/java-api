package com.extole.reporting.rest.audience.operation;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.extole.api.audience.Audience;
import com.extole.id.Id;

public class AudienceOperationQueryParams {

    private static final String AUDIENCE_ID = "audience_id";
    private static final String STATES = "states";
    private static final String TAGS = "tags";
    private static final String TYPE = "type";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Id<Audience> audienceId;
    private final Set<AudienceOperationState> states;
    private final Set<String> tags;
    private final Optional<AudienceOperationType> type;
    private final Integer limit;
    private final Integer offset;

    public AudienceOperationQueryParams(
        @QueryParam(AUDIENCE_ID) Id<Audience> audienceId,
        @QueryParam(STATES) Set<AudienceOperationState> states,
        @QueryParam(TAGS) Set<String> tags,
        @Nullable @QueryParam(TYPE) AudienceOperationType type,
        @QueryParam(LIMIT) @DefaultValue("1000") Integer limit,
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset) {
        this.audienceId = audienceId;
        this.states = states;
        this.tags = tags;
        this.type = Optional.ofNullable(type);
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(AUDIENCE_ID)
    public Id<Audience> getAudienceId() {
        return audienceId;
    }

    @QueryParam(STATES)
    public Set<AudienceOperationState> getStates() {
        return states;
    }

    @QueryParam(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @QueryParam(TYPE)
    public Optional<AudienceOperationType> getType() {
        return type;
    }

    @QueryParam(LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(OFFSET)
    public Integer getOffset() {
        return offset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Id<Audience> audienceId;
        private Set<AudienceOperationState> states;
        private Set<String> tags;
        private AudienceOperationType type;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withAudienceId(Id<Audience> audienceId) {
            this.audienceId = audienceId;
            return this;
        }

        public Builder withStates(Set<AudienceOperationState> states) {
            this.states = states;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withType(AudienceOperationType type) {
            this.type = type;
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

        public AudienceOperationQueryParams build() {
            return new AudienceOperationQueryParams(audienceId, states, tags, type, limit, offset);
        }

    }

}
