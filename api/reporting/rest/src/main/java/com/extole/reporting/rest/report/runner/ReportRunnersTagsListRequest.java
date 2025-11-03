package com.extole.reporting.rest.report.runner;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class ReportRunnersTagsListRequest {

    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_HAVING_ANY_TAGS = "having_any_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    public static final String PARAMETER_TAGS = "tags";
    public static final String PARAMETER_EXCLUDE_TAGS = "exclude_tags";

    private final Optional<String> havingAnyTags;
    private final Optional<String> excludeHavingAnyTags;
    private final Optional<String> offset;
    private final Optional<String> limit;

    public ReportRunnersTagsListRequest(
        @Nullable @QueryParam(PARAMETER_HAVING_ANY_TAGS) @Parameter(description = "Optional filter for tags, " +
            "asks for tags that match any of specified patterns.") String havingAnyTags,
        @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS) @Parameter(
            description = "Optional filter for exclude tags, " +
                "asks for tags that are do not match any of the specified patterns.") String excludeHavingAnyTags,
        @Nullable @QueryParam(PARAMETER_TAGS) @Parameter(deprecated = true, description = "Optional filter for tags, " +
            "asks for tags that are contained in the specified tags. Deprecated, " + PARAMETER_HAVING_ANY_TAGS
            + " should be used instead.") String tags,
        @Nullable @QueryParam(PARAMETER_EXCLUDE_TAGS) @Parameter(deprecated = true,
            description = "Optional filter for exclude tags, " +
                "asks for tags that are not contained in the specified tags. Deprecated, "
                + PARAMETER_EXCLUDE_HAVING_ANY_TAGS + " should be used instead") String excludeTags,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam(PARAMETER_OFFSET) String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to 100.") @Nullable @QueryParam(PARAMETER_LIMIT) String limit) {
        this.havingAnyTags = Optional.ofNullable(havingAnyTags).or(() -> Optional.ofNullable(tags));
        this.excludeHavingAnyTags =
            Optional.ofNullable(excludeHavingAnyTags).or(() -> Optional.ofNullable(excludeTags));
        this.offset = Optional.ofNullable(offset);
        this.limit = Optional.ofNullable(limit);
    }

    @QueryParam(PARAMETER_HAVING_ANY_TAGS)
    public Optional<String> getHavingAnyTags() {
        return havingAnyTags;
    }

    @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS)
    public Optional<String> getExcludeHavingAnyTags() {
        return excludeHavingAnyTags;
    }

    public Optional<String> getOffset() {
        return offset;
    }

    public Optional<String> getLimit() {
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

        private String havingAnyTags;
        private String excludeHavingAnyTags;
        private String tags;
        private String excludeTags;
        private String offset;
        private String limit;

        private Builder() {
        }

        public Builder withHavingAnyTags(String havingAnyTags) {
            this.havingAnyTags = havingAnyTags;
            return this;
        }

        public Builder withExcludeHavingAnyTags(String excludeHavingAnyTags) {
            this.excludeHavingAnyTags = excludeHavingAnyTags;
            return this;
        }

        public Builder withTags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder withExcludeTags(String excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        public Builder withOffset(String offset) {
            this.offset = offset;
            return this;
        }

        public Builder withLimit(String limit) {
            this.limit = limit;
            return this;
        }

        public ReportRunnersTagsListRequest build() {
            return new ReportRunnersTagsListRequest(havingAnyTags, excludeHavingAnyTags, tags, excludeTags, offset,
                limit);
        }
    }
}
