package com.extole.client.rest.prehandler;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PrehandlerListQueryParams {

    private static final String QUERY_PARAM_TAGS = "tags";
    private static final String QUERY_PARAM_LIMIT = "limit";
    private static final String QUERY_PARAM_OFFSET = "offset";
    private static final String QUERY_INCLUDE_ARCHIVED = "include_archived";

    private final Optional<Set<String>> tags;
    private final Integer limit;
    private final Integer offset;
    private final boolean includeArchived;

    public PrehandlerListQueryParams(
        @QueryParam(QUERY_PARAM_TAGS) Set<String> tags,
        @QueryParam(QUERY_PARAM_OFFSET) @DefaultValue("0") Integer offset,
        @QueryParam(QUERY_PARAM_LIMIT) @DefaultValue("100") Integer limit,
        @QueryParam(QUERY_INCLUDE_ARCHIVED) @DefaultValue("false") boolean includeArchived) {
        this.tags = Optional.ofNullable(tags);
        this.limit = limit;
        this.offset = offset;
        this.includeArchived = includeArchived;
    }

    @QueryParam(QUERY_PARAM_TAGS)
    public Optional<Set<String>> getTags() {
        return tags;
    }

    @QueryParam(QUERY_PARAM_LIMIT)
    public Integer getLimit() {
        return limit;
    }

    @QueryParam(QUERY_PARAM_OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @QueryParam(QUERY_INCLUDE_ARCHIVED)
    public boolean getIncludeArchived() {
        return includeArchived;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Set<String> tags;
        private Integer limit;
        private Integer offset;
        private boolean includeArchived;

        private Builder() {
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
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

        public Builder withIncludeArchived(boolean includeArchived) {
            this.includeArchived = includeArchived;
            return this;
        }

        public PrehandlerListQueryParams build() {
            return new PrehandlerListQueryParams(tags, offset, limit, includeArchived);
        }
    }
}
