package com.extole.reporting.rest.audience.member;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class AudienceMemberQueryParameters {

    public static final String DEFAULT_LIMIT = "1000";
    public static final String DEFAULT_OFFSET = "0";

    private static final String EMAIL = "email";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    private final Optional<String> email;
    private final Optional<Integer> limit;
    private final Optional<Integer> offset;

    public AudienceMemberQueryParameters(
        @Nullable @QueryParam(EMAIL) String email,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_LIMIT) Integer limit,
        @QueryParam(OFFSET) @DefaultValue(DEFAULT_OFFSET) Integer offset) {
        this.email = Optional.ofNullable(email);
        this.limit = Optional.ofNullable(limit);
        this.offset = Optional.ofNullable(offset);
    }

    @QueryParam(EMAIL)
    public Optional<String> getEmail() {
        return email;
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
        private Integer limit;
        private Integer offset;

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
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
            return new AudienceMemberQueryParameters(email, limit, offset);
        }

    }

}
