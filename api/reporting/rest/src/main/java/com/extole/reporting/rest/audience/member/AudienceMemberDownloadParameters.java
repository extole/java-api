package com.extole.reporting.rest.audience.member;

import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class AudienceMemberDownloadParameters {

    public static final String DEFAULT_OFFSET = "0";

    private static final String FILENAME = "filename";
    private static final String SORT_ORDER = "sort_order";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Optional<String> filename;

    private final Optional<String> sortOrder;
    private final Optional<Integer> limit;
    private final Integer offset;

    public AudienceMemberDownloadParameters(
        @QueryParam(FILENAME) Optional<String> filename,
        @QueryParam(SORT_ORDER) Optional<String> sortOrder,
        @QueryParam(LIMIT) Optional<Integer> limit,
        @QueryParam(OFFSET) @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        this.filename = filename;
        this.sortOrder = sortOrder;
        this.limit = limit;
        this.offset = offset;
    }

    @QueryParam(FILENAME)
    public Optional<String> getFilename() {
        return filename;
    }

    @QueryParam(SORT_ORDER)
    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    @QueryParam(LIMIT)
    public Optional<Integer> getLimit() {
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

        private Optional<String> filename = Optional.empty();
        private Optional<String> sortOrder = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private Integer offset;

        private Builder() {
        }

        public Builder withFilename(String filename) {
            this.filename = Optional.of(filename);
            return this;
        }

        public Builder withSortOrder(String sortOrder) {
            this.sortOrder = Optional.of(sortOrder);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.of(limit);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public AudienceMemberDownloadParameters build() {
            return new AudienceMemberDownloadParameters(filename, sortOrder, limit, offset);
        }
    }
}
