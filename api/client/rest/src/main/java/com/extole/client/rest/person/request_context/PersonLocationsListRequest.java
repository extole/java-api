package com.extole.client.rest.person.request_context;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonLocationsListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_COUNTRIES = "countries";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";

    private final List<String> countries;
    private final int offset;
    private final int limit;

    public PersonLocationsListRequest(
        @Parameter(description = "Optional countries filter. " +
            "Will include locations that match at least one of the countries.")
        @QueryParam(PARAMETER_COUNTRIES) List<String> countries,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".")
        @DefaultValue("" + DEFAULT_OFFSET)
        @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".")
        @DefaultValue("" + DEFAULT_LIMIT)
        @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit) {
        this.countries = countries == null ? ImmutableList.of() : ImmutableList.copyOf(countries);
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET)).intValue();
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
    }

    @QueryParam(PARAMETER_COUNTRIES)
    public List<String> getCountries() {
        return countries;
    }

    @QueryParam(PARAMETER_OFFSET)
    public int getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public int getLimit() {
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
        private final List<String> countries = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addCountry(String country) {
            this.countries.add(country);
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

        public PersonLocationsListRequest build() {
            return new PersonLocationsListRequest(countries, offset, limit);
        }
    }
}
