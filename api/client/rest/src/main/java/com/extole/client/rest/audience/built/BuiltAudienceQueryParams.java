package com.extole.client.rest.audience.built;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

public class BuiltAudienceQueryParams {

    private static final String NAME = "name";
    private static final String INCLUDE_ARCHIVED = "include_archived";
    private static final String ENABLED = "enabled";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final String name;
    private final boolean includeArchived;
    private final EnableFilter enabled;
    private final Integer limit;
    private final Integer offset;

    public BuiltAudienceQueryParams(
        @Parameter(description = "Optional name filter")
        @Nullable @QueryParam(NAME) String name,
        @Parameter(description = "Optional includeArchived filter")
        @QueryParam(INCLUDE_ARCHIVED) @DefaultValue("false") boolean includeArchived,
        @Parameter(description = "Optional enabled filter")
        @QueryParam(ENABLED) @DefaultValue("ANY") EnableFilter enabled,
        @Parameter(description = "Optional limit filter")
        @QueryParam(LIMIT) @DefaultValue("1000") Integer limit,
        @Parameter(description = "Optional offset filter")
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset) {
        this.name = name;
        this.includeArchived = includeArchived;
        this.enabled = enabled;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(NAME)
    public String getName() {
        return name;
    }

    @QueryParam(INCLUDE_ARCHIVED)
    public boolean getIncludeArchived() {
        return includeArchived;
    }

    @QueryParam(ENABLED)
    public EnableFilter getEnabled() {
        return enabled;
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

        private String name;
        private boolean includeArchived;
        private EnableFilter enabled;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withIncludeArchived(boolean includeArchived) {
            this.includeArchived = includeArchived;
            return this;
        }

        public Builder withEnabled(EnableFilter enabled) {
            this.enabled = enabled;
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

        public BuiltAudienceQueryParams build() {
            return new BuiltAudienceQueryParams(name, includeArchived, enabled, limit, offset);
        }

    }

}
