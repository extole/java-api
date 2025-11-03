package com.extole.reporting.rest.audience.list;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

public class AudienceListQueryParams {

    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String STATES = "states";
    private static final String TYPE = "type";
    private static final String INCLUDE_ARCHIVED = "include_archived";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String ORDER_BY = "order_by";
    private static final String ORDER = "order";

    private final Optional<String> name;
    private final Set<String> tags;
    private final Set<AudienceListState> states;
    private final Optional<AudienceListType> type;
    private final Optional<Boolean> includeArchived;
    private final Optional<Integer> limit;
    private final Optional<Integer> offset;
    private final Optional<AudienceOrderBy> orderBy;
    private final Optional<AudienceOrderDirection> order;

    public AudienceListQueryParams(
        @Parameter(description = "Optional name filter") @Nullable @QueryParam(NAME) String name,
        @Parameter(description = "Optionally a list of tags filter") @Nullable @QueryParam(TAGS) Set<String> tags,
        @Parameter(
            description = "Optionally a list of states filter") @Nullable @QueryParam(STATES) Set<
                AudienceListState> states,
        @Parameter(description = "Optional type filter") @Nullable @QueryParam(TYPE) AudienceListType type,
        @Parameter(
            description = "Optional includeArchived filter") @Nullable @QueryParam(INCLUDE_ARCHIVED) Boolean includeArchived,
        @Parameter(
            description = "Optional limit filter") @Nullable @QueryParam(LIMIT) @DefaultValue("1000") Integer limit,
        @Parameter(
            description = "Optional offset filter") @Nullable @QueryParam(OFFSET) @DefaultValue("0") Integer offset,
        @Nullable @QueryParam(ORDER_BY) AudienceOrderBy orderBy,
        @Nullable @QueryParam(ORDER) AudienceOrderDirection order) {
        this.name = Optional.ofNullable(name);
        this.tags = tags != null ? tags : Collections.emptySet();
        this.states = states != null ? states : Collections.emptySet();
        this.type = Optional.ofNullable(type);
        this.includeArchived = Optional.ofNullable(includeArchived);
        this.limit = Optional.ofNullable(limit);
        this.offset = Optional.ofNullable(offset);
        this.orderBy = Optional.ofNullable(orderBy);
        this.order = Optional.ofNullable(order);
    }

    @QueryParam(NAME)
    public Optional<String> getName() {
        return name;
    }

    @QueryParam(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @QueryParam(STATES)
    public Set<AudienceListState> getStates() {
        return states;
    }

    @QueryParam(TYPE)
    public Optional<AudienceListType> getType() {
        return type;
    }

    @QueryParam(INCLUDE_ARCHIVED)
    public Optional<Boolean> getIncludeArchived() {
        return includeArchived;
    }

    @QueryParam(LIMIT)
    public Optional<Integer> getLimit() {
        return limit;
    }

    @QueryParam(OFFSET)
    public Optional<Integer> getOffset() {
        return offset;
    }

    @QueryParam(ORDER_BY)
    public Optional<AudienceOrderBy> getOrderBy() {
        return orderBy;
    }

    @QueryParam(ORDER)
    public Optional<AudienceOrderDirection> getOrder() {
        return order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private Set<String> tags;
        private Set<AudienceListState> states;
        private AudienceListType type;
        private Boolean includeArchived;
        private Integer limit;
        private Integer offset;
        private AudienceOrderBy orderBy;
        private AudienceOrderDirection order;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withStates(Set<AudienceListState> states) {
            this.states = states;
            return this;
        }

        public Builder withType(AudienceListType type) {
            this.type = type;
            return this;
        }

        public Builder withIncludeArchived(Boolean includeArchived) {
            this.includeArchived = includeArchived;
            return this;
        }

        public Builder withOrderBy(AudienceOrderBy orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public Builder withOrder(AudienceOrderDirection order) {
            this.order = order;
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

        public AudienceListQueryParams build() {
            return new AudienceListQueryParams(name, tags, states, type, includeArchived, limit, offset, orderBy,
                order);
        }
    }
}
