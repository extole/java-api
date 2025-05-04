package com.extole.reporting.rest.report.runner;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class ReportRunnersListRequest {

    public static final String PARAMETER_TYPE = "type";
    public static final String PARAMETER_REPORT_TYPE = "report_type";
    public static final String PARAMETER_REPORT_TYPE_NAME = "report_type_name";
    public static final String PARAMETER_DISPLAY_NAME = "display_name";
    public static final String PARAMETER_USER_ID = "user_id";
    public static final String PARAMETER_USER_IDS = "user_ids";
    public static final String PARAMETER_HAVING_ANY_TAGS = "having_any_tags";
    public static final String PARAMETER_HAVING_ALL_TAGS = "having_all_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ANY_TAGS = "exclude_having_any_tags";
    public static final String PARAMETER_EXCLUDE_HAVING_ALL_TAGS = "exclude_having_all_tags";
    public static final String PARAMETER_TAGS = "tags";
    public static final String PARAMETER_REQUIRED_TAGS = "required_tags";
    public static final String PARAMETER_EXCLUDE_TAGS = "exclude_tags";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_ORDER_BY = "order_by";
    public static final String PARAMETER_ORDER = "order";
    public static final String PARAMETER_SEARCH_QUERY = "search_query";
    public static final String PARAMETER_PAUSE_STATUS = "pause_status";
    public static final String PARAMETER_AGGREGATION_STATUS = "aggregation_status";

    private final Optional<String> type;
    private final Optional<String> reportType;
    private final Optional<String> displayName;
    private final Set<String> userIds;
    private final Optional<String> havingAnyTags;
    private final Optional<String> havingAllTags;
    private final Optional<String> excludeHavingAnyTags;
    private final Optional<String> excludeHavingAllTags;
    private final Optional<String> searchQuery;
    private final Optional<String> pauseStatus;
    private final Optional<String> aggregationStatus;
    private final Optional<String> offset;
    private final Optional<String> limit;
    private final Optional<String> orderBy;
    private final Optional<String> order;
    private final Optional<ZoneId> timezone;

    public ReportRunnersListRequest(
        @Parameter(description = "Optional filter for report runner type, one of: SCHEDULED, REFRESHING.")
        @Nullable @QueryParam(PARAMETER_TYPE) String type,
        @Parameter(description = "Optional filter for report type name.")
        @Nullable @QueryParam(PARAMETER_REPORT_TYPE) String reportType,
        @Parameter(description = "Optional filter for report type name.")
        @Nullable @QueryParam(PARAMETER_REPORT_TYPE_NAME) String reportTypeName,
        @Nullable @QueryParam(PARAMETER_DISPLAY_NAME) String displayName,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @QueryParam(PARAMETER_USER_ID) String userId,
        @Nullable @QueryParam(PARAMETER_USER_IDS) Set<String> userIds,
        @Parameter(description = "Optional filter for report runner tags, " +
            "asks for report runners that contain at least one of the specified tags.")
        @Nullable @QueryParam(PARAMETER_HAVING_ANY_TAGS) String havingAnyTags,
        @Parameter(description = "Optional filter for required report runner tags, " +
            "asks for report runners that contain all of the specified tags.")
        @Nullable @QueryParam(PARAMETER_HAVING_ALL_TAGS) String havingAllTags,
        @Parameter(description = "Optional filter for report runner exclude tags, " +
            "asks for report runners that do not contain any of the specified tags.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ANY_TAGS) String excludeHavingAnyTags,
        @Parameter(description = "Optional filter for report runner exclude tags, " +
            "asks for report runners that do not contain all of the specified tags.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_HAVING_ALL_TAGS) String excludeHavingAllTags,
        @Parameter(deprecated = true, description = "Optional filter for report runner tags, " +
            "asks for report runners that contain at least one of the specified tags. Deprecated, "
            + PARAMETER_HAVING_ANY_TAGS + " should be used instead.")
        @Nullable @QueryParam(PARAMETER_TAGS) String tags,
        @Parameter(deprecated = true, description = "Optional filter for required report runner tags, " +
            "asks for report runners that contain all of the specified tags. Deprecated, " + PARAMETER_HAVING_ALL_TAGS
            + " should be used instead.")
        @Nullable @QueryParam(PARAMETER_REQUIRED_TAGS) String requiredTags,
        @Parameter(deprecated = true, description = "Optional filter for report runner exclude tags, " +
            "asks for report runners that do not contain any of the specified tags. Deprecated, "
            + PARAMETER_EXCLUDE_HAVING_ANY_TAGS + " should be used instead.")
        @Nullable @QueryParam(PARAMETER_EXCLUDE_TAGS) String excludeTags,
        @Nullable @QueryParam(PARAMETER_SEARCH_QUERY) String searchQuery,
        @Nullable @QueryParam(PARAMETER_PAUSE_STATUS) String pauseStatus,
        @Nullable @QueryParam(PARAMETER_AGGREGATION_STATUS) String aggregationStatus,
        @Parameter(description = "Optional filter for offset, defaults to 0.")
        @Nullable @QueryParam(PARAMETER_OFFSET) String offset,
        @Parameter(description = "Optional filter for limit, defaults to all.")
        @Nullable @QueryParam(PARAMETER_LIMIT) String limit,
        @Nullable @QueryParam(PARAMETER_ORDER_BY) String orderBy,
        @Nullable @QueryParam(PARAMETER_ORDER) String order,
        @Nullable @TimeZoneParam ZoneId timezone) {
        this.type = Optional.ofNullable(type);
        this.reportType = Optional.ofNullable(reportType != null ? reportType : reportTypeName);
        this.displayName = Optional.ofNullable(displayName);
        this.userIds = userIds != null ? ImmutableSet.copyOf(userIds)
                : userId != null ? ImmutableSet.of(userId) : ImmutableSet.of();
        this.havingAnyTags = Optional.ofNullable(havingAnyTags).or(() -> Optional.ofNullable(tags));
        this.havingAllTags = Optional.ofNullable(havingAllTags).or(() -> Optional.ofNullable(requiredTags));
        this.excludeHavingAnyTags =
            Optional.ofNullable(excludeHavingAnyTags).or(() -> Optional.ofNullable(excludeTags));
        this.excludeHavingAllTags = Optional.ofNullable(excludeHavingAllTags);
        this.searchQuery = Optional.ofNullable(searchQuery);
        this.pauseStatus = Optional.ofNullable(pauseStatus);
        this.aggregationStatus = Optional.ofNullable(aggregationStatus);
        this.offset = Optional.ofNullable(offset);
        this.limit = Optional.ofNullable(limit);
        this.orderBy = Optional.ofNullable(orderBy);
        this.order = Optional.ofNullable(order);
        this.timezone = Optional.ofNullable(timezone);
    }

    @QueryParam(PARAMETER_TYPE)
    public Optional<String> getType() {
        return type;
    }

    @QueryParam(PARAMETER_REPORT_TYPE)
    public Optional<String> getReportType() {
        return reportType;
    }

    @QueryParam(PARAMETER_REPORT_TYPE_NAME)
    public Optional<String> getReportTypeName() {
        return reportType;
    }

    @QueryParam(PARAMETER_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
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

    @QueryParam(PARAMETER_PAUSE_STATUS)
    public Optional<String> getPauseStatus() {
        return pauseStatus;
    }

    @QueryParam(PARAMETER_AGGREGATION_STATUS)
    public Optional<String> getAggregationStatus() {
        return aggregationStatus;
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
    public Optional<String> getOrderBy() {
        return orderBy;
    }

    @QueryParam(PARAMETER_ORDER)
    public Optional<String> getOrder() {
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

        private String type;
        private String reportType;
        private String displayName;
        private Set<String> userIds = ImmutableSet.of();
        private String havingAnyTags;
        private String havingAllTags;
        private String excludeHavingAnyTags;
        private String excludeHavingAllTags;
        private String tags;
        private String requiredTags;
        private String excludeTags;
        private String searchQuery;
        private String pauseStatus;
        private String aggregationStatus;
        private String offset;
        private String limit;
        private String orderBy;
        private String order;
        private ZoneId timezone;

        private Builder() {
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
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

        public Builder withPauseStatus(String pauseStatus) {
            this.pauseStatus = pauseStatus;
            return this;
        }

        public Builder withAggregationStatus(String aggregationStatus) {
            this.aggregationStatus = aggregationStatus;
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

        public Builder withOrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Builder withOrder(String order) {
            this.order = order;
            return this;
        }

        public Builder withTimezone(ZoneId timezone) {
            this.timezone = timezone;
            return this;
        }

        public ReportRunnersListRequest build() {
            return new ReportRunnersListRequest(type, reportType, reportType, displayName, null, userIds, havingAnyTags,
                havingAllTags, excludeHavingAnyTags, excludeHavingAllTags, tags, requiredTags, excludeTags, searchQuery,
                pauseStatus, aggregationStatus, offset, limit, orderBy, order, timezone);
        }
    }
}
