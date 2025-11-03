package com.extole.reporting.rest.report.execution;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.reporting.rest.report.ReportOrderBy;
import com.extole.reporting.rest.report.ReportOrderDirection;

public class ReportListRequest {

    public static final String PARAMETER_REPORT_TYPE_NAME = "report_type_name";
    public static final String PARAMETER_DISPLAY_NAME = "display_name";
    public static final String PARAMETER_STATUS = "status";
    public static final String PARAMETER_USER_ID = "user_id";
    public static final String PARAMETER_USER_IDS = "user_ids";
    public static final String PARAMETER_HAVING_ANY_TAGS = "having_any_tags";
    public static final String PARAMETER_HAVING_ALL_TAGS = "having_all_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ALL_TAGS = "exclude_having_all_tags";
    public static final String PARAMETER_TAGS = "tags";
    public static final String PARAMETER_REQUIRED_TAGS = "required_tags";
    public static final String PARAMETER_EXCLUDE_TAGS = "exclude_tags";
    public static final String PARAMETER_SEARCH_QUERY = "search_query";
    public static final String PARAMETER_CREATION_INTERVAL = "creation_interval";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_ORDER_BY = "order_by";
    public static final String PARAMETER_ORDER = "order";

    private final Optional<String> reportTypeName;
    private final Optional<String> displayName;
    private final Optional<Set<ReportStatus>> statuses;
    private final Set<String> userIds;
    private final Optional<String> havingAnyTags;
    private final Optional<String> havingAllTags;
    private final Optional<String> excludeHavingAnyTags;
    private final Optional<String> excludeHavingAllTags;
    private final Optional<String> searchQuery;
    private final Optional<String> creationInterval;
    private final Optional<String> offset;
    private final Optional<String> limit;
    private final Optional<ReportOrderBy> orderBy;
    private final Optional<ReportOrderDirection> order;
    private final Optional<ZoneId> timezone;

    public ReportListRequest(
        @Parameter(
            description = "Optional filter for report type name.") @Nullable @QueryParam(PARAMETER_REPORT_TYPE_NAME) String reportTypeName,
        @Nullable @QueryParam(PARAMETER_DISPLAY_NAME) String displayName,
        @Nullable @QueryParam(PARAMETER_STATUS) Set<ReportStatus> statuses,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @QueryParam(PARAMETER_USER_ID) String userId,
        @Nullable @QueryParam(PARAMETER_USER_IDS) Set<String> userIds,
        @Parameter(description = "Optional filter for report tags, " +
            "asks for reports that contain at least one of the specified tags.") @Nullable @QueryParam(PARAMETER_HAVING_ANY_TAGS) String havingAnyTags,
        @Parameter(description = "Optional filter for required report tags, " +
            "asks for reports that contain all of the specified tags.") @Nullable @QueryParam(PARAMETER_HAVING_ALL_TAGS) String havingAllTags,
        @Parameter(description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain any of the specified tags.") @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS) String excludeHavingAnyTags,
        @Parameter(description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain all of the specified tags.") @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ALL_TAGS) String excludeHavingAllTags,
        @Parameter(deprecated = true, description = "Optional filter for report tags, " +
            "asks for reports that contain at least one of the specified tags. Deprecated, " + PARAMETER_HAVING_ANY_TAGS
            + " should be used instead.") @Nullable @QueryParam(PARAMETER_TAGS) String tags,
        @Parameter(deprecated = true, description = "Optional filter for required report tags, " +
            "asks for reports that contain all of the specified tags. Deprecated, " + PARAMETER_HAVING_ALL_TAGS
            + " should be used instead.") @Nullable @QueryParam(PARAMETER_REQUIRED_TAGS) String requiredTags,
        @Parameter(deprecated = true, description = "Optional filter for report exclude tags, " +
            "asks for reports that do not contain any of the specified tags. Deprecated, " + PARAMETER_HAVING_ANY_TAGS
            + " should be used instead.") @Nullable @QueryParam(PARAMETER_EXCLUDE_TAGS) String excludeTags,
        @Nullable @QueryParam(PARAMETER_SEARCH_QUERY) String searchQuery,
        @Parameter(description = "Optional filter for report creation date, " +
            "asks for reports that were created during the specified interval (end date exclusive). " +
            "Unless an explicit timezone if specified in the interval, the timezone from list request will be used," +
            "defaulting to client's timezone.") @Nullable @QueryParam(PARAMETER_CREATION_INTERVAL) String creationInterval,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam(PARAMETER_OFFSET) String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to all.") @Nullable @QueryParam(PARAMETER_LIMIT) String limit,
        @Nullable @QueryParam(PARAMETER_ORDER_BY) ReportOrderBy orderBy,
        @Nullable @QueryParam(PARAMETER_ORDER) ReportOrderDirection order,
        @Nullable @TimeZoneParam ZoneId timezone) {
        this.reportTypeName = Optional.ofNullable(reportTypeName);
        this.displayName = Optional.ofNullable(displayName);
        this.statuses = Optional.ofNullable(statuses);
        this.userIds = userIds != null ? ImmutableSet.copyOf(userIds)
            : userId != null ? ImmutableSet.of(userId) : ImmutableSet.of();
        this.havingAnyTags = Optional.ofNullable(havingAnyTags).or(() -> Optional.ofNullable(tags));
        this.havingAllTags = Optional.ofNullable(havingAllTags).or(() -> Optional.ofNullable(requiredTags));
        this.excludeHavingAnyTags =
            Optional.ofNullable(excludeHavingAnyTags).or(() -> Optional.ofNullable(excludeTags));
        this.excludeHavingAllTags = Optional.ofNullable(excludeHavingAllTags);
        this.searchQuery = Optional.ofNullable(searchQuery);
        this.creationInterval = Optional.ofNullable(creationInterval);
        this.offset = Optional.ofNullable(offset);
        this.limit = Optional.ofNullable(limit);
        this.orderBy = Optional.ofNullable(orderBy);
        this.order = Optional.ofNullable(order);
        this.timezone = Optional.ofNullable(timezone);
    }

    @QueryParam(PARAMETER_REPORT_TYPE_NAME)
    public Optional<String> getReportTypeName() {
        return reportTypeName;
    }

    @QueryParam(PARAMETER_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @QueryParam(PARAMETER_STATUS)
    public Optional<Set<ReportStatus>> getStatuses() {
        return statuses;
    }

    @QueryParam(PARAMETER_USER_IDS)
    public Set<String> getUserIds() {
        return userIds;
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

    @QueryParam(PARAMETER_SEARCH_QUERY)
    public Optional<String> getSearchQuery() {
        return searchQuery;
    }

    @QueryParam(PARAMETER_CREATION_INTERVAL)
    public Optional<String> getCreationInterval() {
        return creationInterval;
    }

    @QueryParam(PARAMETER_OFFSET)
    public Optional<String> getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public Optional<String> getLimit() {
        return limit;
    }

    @QueryParam(PARAMETER_ORDER_BY)
    public Optional<ReportOrderBy> getOrderBy() {
        return orderBy;
    }

    @QueryParam(PARAMETER_ORDER)
    public Optional<ReportOrderDirection> getOrder() {
        return order;
    }

    @TimeZoneParam
    public Optional<ZoneId> getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String reportTypeName;
        private String displayName;
        private Set<ReportStatus> statuses;
        private Set<String> userIds = ImmutableSet.of();
        private String havingAnyTags;
        private String havingAllTags;
        private String excludeHavingAnyTags;
        private String excludeHavingAllTags;
        private String tags;
        private String requiredTags;
        private String excludeTags;
        private String searchQuery;
        private String creationInterval;
        private String offset;
        private String limit;
        private ReportOrderBy orderBy;
        private ReportOrderDirection order;
        private ZoneId timezone;

        private Builder() {
        }

        public Builder withReportTypeName(String reportTypeName) {
            this.reportTypeName = reportTypeName;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withStatuses(Set<ReportStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder withUserIds(Set<String> userIds) {
            this.userIds = ImmutableSet.copyOf(userIds);
            return this;
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

        public Builder withRequiredTags(String requiredTags) {
            this.requiredTags = requiredTags;
            return this;
        }

        public Builder withExcludeTags(String excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        public Builder withSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
            return this;
        }

        public Builder withCreationInterval(String creationInterval) {
            this.creationInterval = creationInterval;
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

        public Builder withOrderBy(ReportOrderBy orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Builder withOrder(ReportOrderDirection order) {
            this.order = order;
            return this;
        }

        public Builder withTimezone(ZoneId timezone) {
            this.timezone = timezone;
            return this;
        }

        public ReportListRequest build() {
            return new ReportListRequest(reportTypeName, displayName, statuses, null, userIds, havingAnyTags,
                havingAllTags, excludeHavingAnyTags, excludeHavingAllTags, tags, requiredTags, excludeTags, searchQuery,
                creationInterval, offset, limit, orderBy, order, timezone);
        }
    }
}
