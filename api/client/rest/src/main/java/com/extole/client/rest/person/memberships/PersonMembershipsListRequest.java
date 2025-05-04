package com.extole.client.rest.person.memberships;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonMembershipsListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_AUDIENCE_IDS = "audience_ids";
    private static final String PARAMETER_AUDIENCE_NAMES = "audience_names";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";

    private final List<String> audienceIds;
    private final List<String> audienceNames;
    private final Integer offset;
    private final Integer limit;

    public PersonMembershipsListRequest(
        @Parameter(description = "Optional audience id filter. " +
            "Will include audience memberships that match at least one of the audience ids.")
        @QueryParam(PARAMETER_AUDIENCE_IDS) List<String> audienceIds,
        @Parameter(description = "Optional audience name filter. " +
            "Will include audience memberships that match at least one of the audience names.")
        @QueryParam(PARAMETER_AUDIENCE_NAMES) List<String> audienceNames,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".")
        @DefaultValue("" + DEFAULT_OFFSET)
        @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".")
        @DefaultValue("" + DEFAULT_LIMIT)
        @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit) {
        this.audienceIds = audienceIds == null ? ImmutableList.of() : ImmutableList.copyOf(audienceIds);
        this.audienceNames = audienceNames == null ? ImmutableList.of() : ImmutableList.copyOf(audienceNames);
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET));
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT));
    }

    @QueryParam(PARAMETER_AUDIENCE_IDS)
    public List<String> getAudienceIds() {
        return audienceIds;
    }

    @QueryParam(PARAMETER_AUDIENCE_NAMES)
    public List<String> getAudienceNames() {
        return audienceNames;
    }

    @QueryParam(PARAMETER_OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<String> audienceIds = Lists.newArrayList();
        private final List<String> audienceNames = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addAudienceId(String audienceId) {
            this.audienceIds.add(audienceId);
            return this;
        }

        public Builder addAudienceName(String audienceName) {
            this.audienceNames.add(audienceName);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public PersonMembershipsListRequest build() {
            return new PersonMembershipsListRequest(audienceIds, audienceNames, offset, limit);
        }
    }
}
