package com.extole.reporting.rest.report.schedule;

import java.time.ZoneId;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.common.rest.time.TimeZoneParam;

public class ReportScheduleListRequest {

    public static final String PARAMETER_REPORT_TYPE = "report_type";
    public static final String PARAMETER_DISPLAY_NAME = "display_name";
    public static final String PARAMETER_USER_ID = "user_id";
    public static final String PARAMETER_TAGS = "tags";
    public static final String PARAMETER_REQUIRED_TAGS = "required_tags";
    public static final String PARAMETER_EXCLUDE_TAGS = "exclude_tags";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_ORDER_BY = "order_by";
    public static final String PARAMETER_ORDER = "order";
    public static final String PARAMETER_SEARCH_QUERY = "search_query";

    private final Optional<String> reportType;
    private final Optional<String> displayName;
    private final Optional<String> userId;
    private final Optional<String> tags;
    private final Optional<String> requiredTags;
    private final Optional<String> excludeTags;
    private final Optional<String> searchQuery;
    private final Optional<String> offset;
    private final Optional<String> limit;
    private final Optional<String> orderBy;
    private final Optional<String> order;
    private final Optional<ZoneId> timezone;

    public ReportScheduleListRequest(
        @Parameter(
            description = "Optional filter for report type name.") @Nullable @QueryParam(PARAMETER_REPORT_TYPE) String reportType,
        @Nullable @QueryParam(PARAMETER_DISPLAY_NAME) String displayName,
        @Nullable @QueryParam(PARAMETER_USER_ID) String userId,
        @Parameter(description = "Optional filter for report runner tags, " +
            "asks for runners that contain at least one of the specified tags.") @Nullable @QueryParam(PARAMETER_TAGS) String tags,
        @Parameter(description = "Optional filter for required report runner tags, " +
            "asks for runners that contain all of the specified tags.") @Nullable @QueryParam(PARAMETER_REQUIRED_TAGS) String requiredTags,
        @Parameter(description = "Optional filter for report runner exclude tags, " +
            "asks for runners that do not contain any of the specified tags.") @Nullable @QueryParam(PARAMETER_EXCLUDE_TAGS) String excludeTags,
        @Nullable @QueryParam(PARAMETER_SEARCH_QUERY) String searchQuery,
        @Parameter(
            description = "Optional filter for offset, defaults to 0.") @Nullable @QueryParam(PARAMETER_OFFSET) String offset,
        @Parameter(
            description = "Optional filter for limit, defaults to all.") @Nullable @QueryParam(PARAMETER_LIMIT) String limit,
        @Nullable @QueryParam(PARAMETER_ORDER_BY) String orderBy,
        @Nullable @QueryParam(PARAMETER_ORDER) String order,
        @Nullable @TimeZoneParam ZoneId timezone) {
        this.reportType = Optional.ofNullable(reportType);
        this.displayName = Optional.ofNullable(displayName);
        this.userId = Optional.ofNullable(userId);
        this.tags = Optional.ofNullable(tags);
        this.requiredTags = Optional.ofNullable(requiredTags);
        this.excludeTags = Optional.ofNullable(excludeTags);
        this.searchQuery = Optional.ofNullable(searchQuery);
        this.offset = Optional.ofNullable(offset);
        this.limit = Optional.ofNullable(limit);
        this.orderBy = Optional.ofNullable(orderBy);
        this.order = Optional.ofNullable(order);
        this.timezone = Optional.ofNullable(timezone);
    }

    @QueryParam(PARAMETER_REPORT_TYPE)
    public Optional<String> getReportType() {
        return reportType;
    }

    @QueryParam(PARAMETER_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @QueryParam(PARAMETER_USER_ID)
    public Optional<String> getUserId() {
        return userId;
    }

    @QueryParam(PARAMETER_TAGS)
    public Optional<String> getTags() {
        return tags;
    }

    @QueryParam(PARAMETER_REQUIRED_TAGS)
    public Optional<String> getRequiredTags() {
        return requiredTags;
    }

    @QueryParam(PARAMETER_EXCLUDE_TAGS)
    public Optional<String> getExcludeTags() {
        return excludeTags;
    }

    @QueryParam(PARAMETER_SEARCH_QUERY)
    public Optional<String> getSearchQuery() {
        return searchQuery;
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

        private String reportType;
        private String displayName;
        private String userId;
        private String tags;
        private String requiredTags;
        private String excludeTags;
        private String searchQuery;
        private String offset;
        private String limit;
        private String orderBy;
        private String order;
        private ZoneId timezone;

        private Builder() {
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
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

        public ReportScheduleListRequest build() {
            return new ReportScheduleListRequest(reportType, displayName, userId, tags, requiredTags, excludeTags,
                searchQuery, offset, limit, orderBy, order, timezone);
        }

    }

}
