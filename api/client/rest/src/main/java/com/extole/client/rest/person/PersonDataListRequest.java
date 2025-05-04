package com.extole.client.rest.person;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonDataListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_NAMES = "names";
    private static final String PARAMETER_SCOPES = "scopes";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";

    private final List<String> names;
    private final List<PersonDataScope> scopes;
    private final Optional<Integer> offset;
    private final Optional<Integer> limit;

    public PersonDataListRequest(
        @Parameter(description = "Optional data name filter. Will include data values that match at least one of the " +
            "names. Will include all data if not provided.")
        @QueryParam("names") List<String> names,
        @Parameter(description = "Optional data scope filter. Will include data values that match at least one of the" +
            " scopes. Will include all data if not provided.")
        @QueryParam("scopes") List<PersonDataScope> scopes,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".")
        @DefaultValue("" + DEFAULT_OFFSET) @QueryParam("offset") Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".")
        @DefaultValue("" + DEFAULT_LIMIT) @QueryParam("limit") Optional<Integer> limit) {
        this.names = names == null ? ImmutableList.of() : ImmutableList.copyOf(names);
        this.scopes = scopes == null ? ImmutableList.of() : ImmutableList.copyOf(scopes);
        this.offset = offset;
        this.limit = limit;
    }

    @QueryParam(PARAMETER_NAMES)
    public List<String> getNames() {
        return names;
    }

    @QueryParam(PARAMETER_SCOPES)
    public List<PersonDataScope> getScopes() {
        return scopes;
    }

    @QueryParam(PARAMETER_OFFSET)
    public Optional<Integer> getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public Optional<Integer> getLimit() {
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

        private final List<String> names = Lists.newArrayList();
        private final List<PersonDataScope> scopes = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addName(String name) {
            this.names.add(name);
            return this;
        }

        public Builder addScope(PersonDataScope scope) {
            this.scopes.add(scope);
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public PersonDataListRequest build() {
            return new PersonDataListRequest(names, scopes, offset, limit);
        }

    }

}
