package com.extole.client.rest.person;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonRelationshipsListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_MY_ROLES = "my_roles";
    private static final String PARAMETER_CONTAINERS = "containers";
    private static final String PARAMETER_PROGRAMS = "programs";
    private static final String PARAMETER_CAMPAIGN_IDS = "campaign_ids";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_DATA_VALUES = "data_values";
    private static final String PARAMETER_EXCLUDE_ANONYMOUS = "exclude_anonymous";
    private static final String PARAMETER_INCLUDE_DUPLICATE_IDENTITIES = "include_duplicate_identities";
    private static final String PARAMETER_INCLUDE_SELF_REFERRALS = "include_self_referrals";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";

    private final List<PersonReferralRole> myRoles;
    private final List<String> containers;
    private final List<String> programs;
    private final List<String> campaignIds;
    private final List<String> dataKeys;
    private final List<String> dataValues;
    private final boolean excludeAnonymous;
    private final boolean includeDuplicateIdentities;
    private final boolean includeSelfReferrals;
    private final int offset;
    private final int limit;

    public PersonRelationshipsListRequest(
        @Parameter(description = "Optional roles filter for the current person in the relationship. " +
            "Will include journeys that match at least one of the roles. Eligible values are FRIEND, ADVOCATE.") @QueryParam(PARAMETER_MY_ROLES) List<
                PersonReferralRole> myRoles,
        @Parameter(description = "Optional container filter, defaults to all containers. " +
            "Will include relationships that match at least one of the containers.") @QueryParam(PARAMETER_CONTAINERS) List<
                String> containers,
        @Parameter(description = "Optional program label filter. " +
            "Will include relationships that match at least one of the programs.") @QueryParam(PARAMETER_PROGRAMS) List<
                String> programs,
        @Parameter(description = "Optional campaign id filter. " +
            "Will include relationships that match at least one of the campaign ids.") @QueryParam(PARAMETER_CAMPAIGN_IDS) List<
                String> campaignIds,
        @Parameter(description = "Optional filter for existence of specific data keys with non-empty values. " +
            "Will include relationships that have at least one of the data keys.") @QueryParam(PARAMETER_DATA_KEYS) List<
                String> dataKeys,
        @Parameter(description = "Optional filter for existence of specific data values. " +
            "Will include relationships that have at least one of the specified data name-value pair. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_DATA_VALUES) List<String> dataValues,
        @Parameter(
            description = "Optional flag to exclude relationships with anonymous persons.") @DefaultValue("false") @QueryParam(PARAMETER_EXCLUDE_ANONYMOUS) boolean excludeAnonymous,
        @Parameter(description = "Optional flag to return all relationships, not de-duplicate by identity. " +
            "Default behavior (or when flag is set to false) returns last relationship per person.") @DefaultValue("false") @QueryParam(PARAMETER_INCLUDE_DUPLICATE_IDENTITIES) boolean includeDuplicateIdentities,
        @Parameter(
            description = "Optional flag to include self referrals.") @DefaultValue("false") @QueryParam(PARAMETER_INCLUDE_SELF_REFERRALS) boolean includeSelfReferrals,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam("offset") Integer offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam("limit") Integer limit) {
        this.myRoles = myRoles == null ? ImmutableList.of() : ImmutableList.copyOf(myRoles);
        this.containers = containers == null ? ImmutableList.of() : ImmutableList.copyOf(containers);
        this.programs = programs == null ? ImmutableList.of() : ImmutableList.copyOf(programs);
        this.campaignIds = campaignIds == null ? ImmutableList.of() : ImmutableList.copyOf(campaignIds);
        this.dataKeys = dataKeys == null ? ImmutableList.of() : ImmutableList.copyOf(dataKeys);
        this.dataValues = dataValues == null ? ImmutableList.of() : ImmutableList.copyOf(dataValues);
        this.excludeAnonymous = excludeAnonymous;
        this.includeDuplicateIdentities = includeDuplicateIdentities;
        this.includeSelfReferrals = includeSelfReferrals;
        this.offset = offset == null ? DEFAULT_OFFSET : offset.intValue();
        this.limit = limit == null ? DEFAULT_LIMIT : limit.intValue();
    }

    @QueryParam(PARAMETER_MY_ROLES)
    public List<PersonReferralRole> getMyRoles() {
        return myRoles;
    }

    @QueryParam(PARAMETER_CONTAINERS)
    public List<String> getContainers() {
        return containers;
    }

    @QueryParam(PARAMETER_PROGRAMS)
    public List<String> getPrograms() {
        return programs;
    }

    @QueryParam(PARAMETER_CAMPAIGN_IDS)
    public List<String> getCampaignIds() {
        return campaignIds;
    }

    @QueryParam(PARAMETER_DATA_KEYS)
    public List<String> getDataKeys() {
        return dataKeys;
    }

    @QueryParam(PARAMETER_DATA_VALUES)
    public List<String> getDataValues() {
        return dataValues;
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

        private final List<PersonReferralRole> myRoles = Lists.newArrayList();
        private final List<String> containers = Lists.newArrayList();
        private final List<String> programs = Lists.newArrayList();
        private final List<String> campaignIds = Lists.newArrayList();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> dataValues = Lists.newArrayList();
        private boolean excludeAnonymous;
        private boolean includeDuplicateIdentities;
        private boolean includeSelfReferrals;
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addMyRole(PersonReferralRole myRole) {
            this.myRoles.add(myRole);
            return this;
        }

        public Builder addContainer(String container) {
            this.containers.add(container);
            return this;
        }

        public Builder addProgram(String program) {
            this.programs.add(program);
            return this;
        }

        public Builder addCampaignId(String campaignId) {
            this.campaignIds.add(campaignId);
            return this;
        }

        public Builder addDataKey(String dataKey) {
            this.dataKeys.add(dataKey);
            return this;
        }

        public Builder addDataValue(String dataKey, Object dataValue) {
            this.dataValues.add(dataKey + ":" + dataValue);
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
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public PersonRelationshipsListRequest build() {
            return new PersonRelationshipsListRequest(myRoles, containers, programs, campaignIds, dataKeys, dataValues,
                excludeAnonymous, includeDuplicateIdentities, includeSelfReferrals,
                offset.orElse(null), limit.orElse(null));
        }

    }

}
