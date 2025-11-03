package com.extole.reporting.rest.audience.member;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class AudienceMemberQueryParameters {

    public static final String DEFAULT_LIMIT = "1000";
    public static final String DEFAULT_OFFSET = "0";

    private static final String EMAIL = "email";
    private static final String HAS_DATA_ATTRIBUTES = "has_data_attributes";
    private static final String INCLUDE_DATA_ATTRIBUTES = "include_data_attributes";
    private static final String SORT_ORDER = "sort_order";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Optional<String> email;
    private final Optional<String> sortOrder;
    private final Optional<Integer> limit;
    private final Optional<Integer> offset;
    private final List<String> hasDataAttributes;
    private final List<String> includeDataAttributes;

    public AudienceMemberQueryParameters(
        @Nullable @QueryParam(EMAIL) String email,
        @Nullable @QueryParam(HAS_DATA_ATTRIBUTES) List<String> hasDataAttributes,
        @Nullable @QueryParam(INCLUDE_DATA_ATTRIBUTES) List<String> includeDataAttributes,
        @QueryParam(SORT_ORDER) String sortOrder,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_LIMIT) Integer limit,
        @QueryParam(OFFSET) @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        this.email = Optional.ofNullable(email);
        this.hasDataAttributes = hasDataAttributes != null ? hasDataAttributes : List.of();
        this.includeDataAttributes = includeDataAttributes != null ? includeDataAttributes : List.of();
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.limit = Optional.ofNullable(limit);
        this.offset = Optional.ofNullable(offset);
    }

    @QueryParam(EMAIL)
    public Optional<String> getEmail() {
        return email;
    }

    @QueryParam(HAS_DATA_ATTRIBUTES)
    public List<String> getHasDataAttributes() {
        return hasDataAttributes;
    }

    @QueryParam(INCLUDE_DATA_ATTRIBUTES)
    public List<String> getIncludeDataAttributes() {
        return includeDataAttributes;
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
    public Optional<Integer> getOffset() {
        return offset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String email;
        private List<String> hasDataAttributes;
        private List<String> includeDataAttributes;
        private String sortOrder;
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withHasDataAttributes(List<String> hasDataAttributes) {
            this.hasDataAttributes = hasDataAttributes;
            return this;
        }

        public Builder withIncludeDataAttributes(List<String> includeDataAttributes) {
            this.includeDataAttributes = includeDataAttributes;
            return this;
        }

        public Builder withSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
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

        public AudienceMemberQueryParameters build() {
            return new AudienceMemberQueryParameters(email, hasDataAttributes, includeDataAttributes, sortOrder, limit,
                offset);
        }
    }
}
