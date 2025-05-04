package com.extole.reporting.rest.report.execution;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportOrderBy;
import com.extole.reporting.rest.report.ReportOrderDirection;

public class LatestReportDownloadRequest {

    public static final String PARAMETER_HAVING_ANY_TAGS = "having_any_tags";
    public static final String PARAMETER_HAVING_ALL_TAGS = "having_all_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ALL_TAGS = "exclude_having_all_tags";
    public static final String PARAMETER_TAGS = "tags";
    public static final String PARAMETER_EXCLUDE_TAGS = "exclude_tags";
    public static final String PARAMETER_ORDER_BY = "order_by";
    public static final String PARAMETER_ORDER = "order";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_FILENAME = "filename";
    private final Optional<String> havingAnyTags;
    private final Optional<String> havingAllTags;
    private final Optional<String> excludeHavingAnyTags;
    private final Optional<String> excludeHavingAllTags;
    private final Optional<ReportOrderBy> orderBy;
    private final Optional<ReportOrderDirection> order;
    private final Optional<String> offset;
    private final Optional<String> limit;
    private final Optional<String> filename;

    public LatestReportDownloadRequest(
        @Parameter(description = "Optional filter for report tags, " +
            "asks for reports that contain at least one of the specified tags.")
        @Nullable @QueryParam(PARAMETER_HAVING_ANY_TAGS) String havingAnyTags,
        @Parameter(description = "Optional filter for required report tags, " +
            "asks for reports that contain all of the specified tags.")
        @Nullable @QueryParam(PARAMETER_HAVING_ALL_TAGS) String havingAllTags,
        @Parameter(description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain any of the specified tags.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS) String excludeHavingAnyTags,
        @Parameter(description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain all of the specified tags.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ALL_TAGS) String excludeHavingAllTags,
        @Parameter(deprecated = true, description = "Optional filter for report tags, " +
            "asks for reports that contain at least one of the specified tags. Deprecated, " + PARAMETER_HAVING_ANY_TAGS
            + " should be used instead.")
        @Nullable @QueryParam(PARAMETER_TAGS) String tags,
        @Parameter(deprecated = true, description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain any of the specified tags. Deprecated, " + PARAMETER_HAVING_ANY_TAGS
            + " should be used instead.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_TAGS) String excludeTags,
        @Parameter(description = "Optional order by property, " +
            "will return latest report based on order by property. " +
            "Valid values: NAME, USER, DATE_RUN, DATE_COMPLETED, STATUS, DATE_SCHEDULED")
        @Nullable @QueryParam(PARAMETER_ORDER_BY) ReportOrderBy orderBy,
        @Parameter(description = "Optional order direction, " +
            "will return latest report sorted in specified direction. " +
            "Valid values: ASCENDING, DESCENDING")
        @Nullable @QueryParam(PARAMETER_ORDER) ReportOrderDirection order,
        @Parameter(description = "Optional filter for offset, defaults to 0.")
        @Nullable @QueryParam(PARAMETER_OFFSET) String offset,
        @Parameter(description = "Optional filter for limit, defaults to all.")
        @Nullable @QueryParam(PARAMETER_LIMIT) String limit,
        @Nullable @QueryParam(PARAMETER_FILENAME) String filename) {
        this.havingAnyTags = Optional.ofNullable(havingAnyTags).or(() -> Optional.ofNullable(tags));
        this.havingAllTags = Optional.ofNullable(havingAllTags);
        this.excludeHavingAnyTags =
            Optional.ofNullable(excludeHavingAnyTags).or(() -> Optional.ofNullable(excludeTags));
        this.excludeHavingAllTags = Optional.ofNullable(excludeHavingAllTags);
        this.orderBy = Optional.ofNullable(orderBy);
        this.order = Optional.ofNullable(order);
        this.offset = Optional.ofNullable(offset);
        this.limit = Optional.ofNullable(limit);
        this.filename = Optional.ofNullable(filename);

    }

    @QueryParam(PARAMETER_HAVING_ANY_TAGS)
    public Optional<String> getHavingAnyTags() {
        return havingAnyTags;
    }

    @QueryParam(PARAMETER_HAVING_ALL_TAGS)
    public Optional<String> getHavingAllTags() {
        return havingAllTags;
    }

    @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS)
    public Optional<String> getExcludeHavingAnyTags() {
        return excludeHavingAnyTags;
    }

    @QueryParam(PARAMETER_EXCLUDE_HAVING_ALL_TAGS)
    public Optional<String> getExcludeHavingAllTags() {
        return excludeHavingAllTags;
    }

    @QueryParam(PARAMETER_ORDER_BY)
    public Optional<ReportOrderBy> getOrderBy() {
        return orderBy;
    }

    @QueryParam(PARAMETER_ORDER)
    public Optional<ReportOrderDirection> getOrder() {
        return order;
    }

    @QueryParam(PARAMETER_OFFSET)
    public Optional<String> getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public Optional<String> getLimit() {
        return limit;
    }

    @QueryParam(PARAMETER_FILENAME)
    public Optional<String> getFilename() {
        return filename;
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
        private String havingAllTags;
        private String excludeHavingAnyTags;
        private String excludeHavingAllTags;
        private String tags;
        private String excludeTags;
        private ReportOrderBy orderBy;
        private ReportOrderDirection order;
        private String offset;
        private String limit;
        private String filename;

        private Builder() {
        }

        public Builder withHavingAnyTags(String havingAnyTags) {
            this.havingAnyTags = havingAnyTags;
            return this;
        }

        public Builder withHavingAllTags(String havingAllTags) {
            this.havingAllTags = havingAllTags;
            return this;
        }

        public Builder withExcludeHavingAnyTags(String excludeHavingAnyTags) {
            this.excludeHavingAnyTags = excludeHavingAnyTags;
            return this;
        }

        public Builder withExcludeHavingAllTags(String excludeHavingAllTags) {
            this.excludeHavingAllTags = excludeHavingAllTags;
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

        public Builder withOrderBy(ReportOrderBy orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Builder withOrder(ReportOrderDirection order) {
            this.order = order;
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

        public Builder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public LatestReportDownloadRequest build() {
            return new LatestReportDownloadRequest(havingAnyTags, havingAllTags,
                excludeHavingAnyTags, excludeHavingAllTags, tags, excludeTags, orderBy, order, offset, limit, filename);
        }
    }
}
