package com.extole.client.rest.audience;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

public class AudienceQueryParams {

    private static final String INCLUDE_ARCHIVED = "include_archived";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final boolean includeArchived;
    private final Integer limit;
    private final Integer offset;

    public AudienceQueryParams(
        @Parameter(description = "Optional includeArchived filter")
        @QueryParam(INCLUDE_ARCHIVED) @DefaultValue("false") boolean includeArchived,
        @Parameter(description = "Optional limit filter")
        @QueryParam(LIMIT) @DefaultValue("1000") Integer limit,
        @Parameter(description = "Optional offset filter")
        @QueryParam(OFFSET) @DefaultValue("0") Integer offset) {
        this.includeArchived = includeArchived;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(INCLUDE_ARCHIVED)
    public boolean getIncludeArchived() {
        return includeArchived;
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

        private boolean includeArchived;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withIncludeArchived(boolean includeArchived) {
            this.includeArchived = includeArchived;
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

        public AudienceQueryParams build() {
            return new AudienceQueryParams(includeArchived, limit, offset);
        }

    }

}
