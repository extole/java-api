package com.extole.reporting.rest.report.type;

import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportOrderDirection;
import com.extole.reporting.rest.report.ReportTypeVisibility;

public class ReportTypeGetRequest {

    private final Optional<String> reportTypeId;
    private final Optional<String> displayName;
    private final Optional<String> description;
    private final Optional<ReportTypeVisibility> visibility;
    private final Optional<String> tags;
    private final Optional<String> excludeTags;
    private final Optional<String> searchQuery;
    private final Optional<Integer> limit;
    private final Optional<Integer> offset;
    private final Optional<ReportTypeOrderBy> orderBy;
    private final Optional<ReportOrderDirection> orderDirection;

    public ReportTypeGetRequest(
        @Parameter(description = "The Extole unique report type identifier.")
        @QueryParam("report_type_id") Optional<String> reportTypeId,
        @Parameter(description = "The display name of report type")
        @QueryParam("display_name") Optional<String> displayName,
        @Parameter(description = "The description of report type")
        @QueryParam("description") Optional<String> description,
        @Parameter(description = "The visibility of report type")
        @QueryParam("visibility") Optional<ReportTypeVisibility> visibility,
        @Parameter(description = "The clientId related to report type")
        @QueryParam("tags") Optional<String> tags,
        @QueryParam("exclude_tags") Optional<String> excludeTags,
        @QueryParam("search_query") Optional<String> searchQuery,
        @DefaultValue("100") @QueryParam("limit") Optional<Integer> limit,
        @DefaultValue("0") @QueryParam("offset") Optional<Integer> offset,
        @QueryParam("order_by") Optional<ReportTypeOrderBy> orderBy,
        @QueryParam("order") Optional<ReportOrderDirection> orderDirection) {
        this.reportTypeId = reportTypeId;
        this.displayName = displayName;
        this.description = description;
        this.visibility = visibility;
        this.tags = tags;
        this.excludeTags = excludeTags;
        this.searchQuery = searchQuery;
        this.limit = limit;
        this.offset = offset;
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    @QueryParam("report_type_id")
    public Optional<String> getReportTypeId() {
        return reportTypeId;
    }

    @QueryParam("display_name")
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @QueryParam("description")
    public Optional<String> getDescription() {
        return description;
    }

    @QueryParam("visibility")
    public Optional<ReportTypeVisibility> getVisibility() {
        return visibility;
    }

    @QueryParam("tags")
    public Optional<String> getTags() {
        return tags;
    }

    @QueryParam("exclude_tags")
    public Optional<String> getExcludeTags() {
        return excludeTags;
    }

    @QueryParam("search_query")
    public Optional<String> getSearchQuery() {
        return searchQuery;
    }

    @QueryParam("limit")
    public Optional<Integer> getLimit() {
        return limit;
    }

    @QueryParam("offset")
    public Optional<Integer> getOffset() {
        return offset;
    }

    @QueryParam("order_by")
    public Optional<ReportTypeOrderBy> getOrderBy() {
        return orderBy;
    }

    @QueryParam("order")
    public Optional<ReportOrderDirection> getOrderDirection() {
        return orderDirection;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Optional<String> reportTypeId = Optional.empty();
        private Optional<String> displayName = Optional.empty();
        private Optional<String> description = Optional.empty();
        private Optional<ReportTypeVisibility> visibility = Optional.empty();
        private Optional<String> tags = Optional.empty();
        private Optional<String> excludeTags = Optional.empty();
        private Optional<String> searchQuery = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private Optional<Integer> offset = Optional.empty();
        private Optional<ReportTypeOrderBy> orderBy = Optional.empty();
        private Optional<ReportOrderDirection> orderDirection = Optional.empty();

        private Builder() {
        }

        public Builder withReportTypeId(String reportTypeId) {
            this.reportTypeId = Optional.ofNullable(reportTypeId);
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Optional.ofNullable(displayName);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return this;
        }

        public Builder withVisibility(ReportTypeVisibility visibility) {
            this.visibility = Optional.ofNullable(visibility);
            return this;
        }

        public Builder withTags(String tags) {
            this.tags = Optional.ofNullable(tags);
            return this;
        }

        public Builder withExcludeTags(String excludeTags) {
            this.excludeTags = Optional.ofNullable(excludeTags);
            return this;
        }

        public Builder withSearchQuery(String searchQuery) {
            this.searchQuery = Optional.ofNullable(searchQuery);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withOrderBy(ReportTypeOrderBy orderBy) {
            this.orderBy = Optional.ofNullable(orderBy);
            return this;
        }

        public Builder withOrderDirection(ReportOrderDirection orderDirection) {
            this.orderDirection = Optional.ofNullable(orderDirection);
            return this;
        }

        public ReportTypeGetRequest build() {
            return new ReportTypeGetRequest(reportTypeId, displayName, description, visibility, tags, excludeTags,
                searchQuery, limit, offset, orderBy, orderDirection);
        }
    }
}
