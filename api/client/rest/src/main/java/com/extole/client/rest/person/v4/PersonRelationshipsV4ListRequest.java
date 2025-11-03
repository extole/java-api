package com.extole.client.rest.person.v4;

import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.client.rest.person.PersonReferralRole;
import com.extole.common.lang.ToString;

public class PersonRelationshipsV4ListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 1000;
    public static final String ALL_CONTAINERS = "*";

    public static final String PARAMETER_CONTAINER = "container";
    public static final String PARAMETER_ROLE = "role";
    public static final String PARAMETER_EXCLUDE_ANONYMOUS = "exclude_anonymous";
    public static final String PARAMETER_INCLUDE_DUPLICATE_IDENTITIES = "include_duplicate_identities";
    public static final String PARAMETER_INCLUDE_SELF_REFERRALS = "include_self_referrals";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";

    private final String container;
    private final PersonReferralRole role;
    private final boolean excludeAnonymous;
    private final boolean includeDuplicateIdentities;
    private final boolean includeSelfReferrals;
    private final Integer offset;
    private final Integer limit;

    public PersonRelationshipsV4ListRequest(
        @Parameter(description = "Optional container filter, defaults to production container. " +
            "Pass \"" + ALL_CONTAINERS
            + "\" to include steps for all containers") @Nullable @QueryParam("container") String container,
        @Parameter(
            description = "Optional role of the other person in the relationship, one of friend or advocate.") @Nullable @QueryParam("role") PersonReferralRole role,
        @Parameter(
            description = "Optional flag to exclude relationships with anonymous persons.") @DefaultValue("false") @QueryParam("exclude_anonymous") boolean excludeAnonymous,
        @Parameter(description = "Optional flag to return all relationships, not de-duplicate by identity. " +
            "Default behavior (or when flag is set to false) returns last relationship per person.") @DefaultValue("false") @QueryParam("include_duplicate_identities") boolean includeDuplicateIdentities,
        @Parameter(
            description = "Optional flag to include self referrals.") @DefaultValue("false") @QueryParam("include_self_referrals") boolean includeSelfReferrals,
        @Parameter(description = "Optional offset filter, defaults to 0.") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to 1000.") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam("limit") Integer limit) {
        this.container = container;
        this.role = role;
        this.excludeAnonymous = excludeAnonymous;
        this.includeDuplicateIdentities = includeDuplicateIdentities;
        this.includeSelfReferrals = includeSelfReferrals;
        this.offset = offset;
        this.limit = limit;
    }

    @Nullable
    @QueryParam(PARAMETER_CONTAINER)
    public String getContainer() {
        return container;
    }

    @Nullable
    @QueryParam(PARAMETER_ROLE)
    public PersonReferralRole getRole() {
        return role;
    }

    @QueryParam(PARAMETER_EXCLUDE_ANONYMOUS)
    public boolean isExcludeAnonymous() {
        return excludeAnonymous;
    }

    @QueryParam(PARAMETER_INCLUDE_DUPLICATE_IDENTITIES)
    public boolean isIncludeDuplicateIdentities() {
        return includeDuplicateIdentities;
    }

    @QueryParam(PARAMETER_INCLUDE_SELF_REFERRALS)
    public boolean isIncludeSelfReferrals() {
        return includeSelfReferrals;
    }

    @Nullable
    @QueryParam(PARAMETER_OFFSET)
    public Integer getOffset() {
        return offset;
    }

    @Nullable
    @QueryParam(PARAMETER_LIMIT)
    public Integer getLimit() {
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

        private String container;
        private PersonReferralRole role;
        private boolean excludeAnonymous;
        private boolean includeDuplicateIdentities;
        private boolean includeSelfReferrals;
        private Integer offset;
        private Integer limit;

        private Builder() {
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withRole(PersonReferralRole role) {
            this.role = role;
            return this;
        }

        public Builder withExcludeAnonymous(boolean excludeAnonymous) {
            this.excludeAnonymous = excludeAnonymous;
            return this;
        }

        public Builder withIncludeDuplicateIdentities(boolean includeDuplicateIdentities) {
            this.includeDuplicateIdentities = includeDuplicateIdentities;
            return this;
        }

        public Builder withIncludeSelfReferrals(boolean includeSelfReferrals) {
            this.includeSelfReferrals = includeSelfReferrals;
            return this;
        }

        public Builder withOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = limit;
            return this;
        }

        public PersonRelationshipsV4ListRequest build() {
            return new PersonRelationshipsV4ListRequest(container, role, excludeAnonymous, includeDuplicateIdentities,
                includeSelfReferrals, offset, limit);
        }

    }

}
