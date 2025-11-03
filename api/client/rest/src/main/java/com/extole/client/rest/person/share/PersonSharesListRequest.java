package com.extole.client.rest.person.share;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonSharesListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_PROGRAMS = "programs";
    private static final String PARAMETER_CAMPAIGN_IDS = "campaign_ids";
    private static final String PARAMETER_PARTNER_IDS = "partner_ids";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_DATA_VALUES = "data_values";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";
    private final List<String> programs;
    private final List<String> campaignIds;
    private final List<String> partnerIds;
    private final List<String> dataKeys;
    private final List<String> dataValues;
    private final int offset;
    private final int limit;

    public PersonSharesListRequest(
        @Parameter(description = "Optional program filter. " +
            "Will include shares that match at least one of the programs.") @QueryParam(PARAMETER_PROGRAMS) List<
                String> programs,
        @Parameter(description = "Optional campaign id filter. " +
            "Will include shares that match at least one of the campaign ids.") @QueryParam(PARAMETER_CAMPAIGN_IDS) List<
                String> campaignIds,
        @Parameter(description = "Optional program label filter. " +
            "Will include shares that match at least one of the programs.") @QueryParam(PARAMETER_PARTNER_IDS) List<
                String> partnerIds,
        @Parameter(description = "Optional data keys filter. " +
            "Will include shares that match at least one of the data keys.") @QueryParam(PARAMETER_DATA_KEYS) List<
                String> dataKeys,
        @Parameter(description = "Optional filter for existence of specific data values. " +
            "Will include shares that have at least one of the specified data name-value pair. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_DATA_VALUES) List<String> dataValues,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit) {
        this.programs = programs == null ? ImmutableList.of() : ImmutableList.copyOf(programs);
        this.campaignIds = campaignIds == null ? ImmutableList.of() : ImmutableList.copyOf(campaignIds);
        this.partnerIds = partnerIds == null ? ImmutableList.of() : ImmutableList.copyOf(partnerIds);
        this.dataKeys = dataKeys == null ? ImmutableList.of() : ImmutableList.copyOf(dataKeys);
        this.dataValues = dataValues == null ? ImmutableList.of() : ImmutableList.copyOf(dataValues);
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET)).intValue();
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
    }

    @QueryParam(PARAMETER_PROGRAMS)
    public List<String> getPrograms() {
        return programs;
    }

    @QueryParam(PARAMETER_CAMPAIGN_IDS)
    public List<String> getCampaignIds() {
        return campaignIds;
    }

    @QueryParam(PARAMETER_PARTNER_IDS)
    public List<String> getPartnerIds() {
        return partnerIds;
    }

    @QueryParam(PARAMETER_DATA_KEYS)
    public List<String> getDataKeys() {
        return dataKeys;
    }

    @QueryParam(PARAMETER_DATA_VALUES)
    public List<String> getDataValues() {
        return dataValues;
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
        private final List<String> programs = Lists.newArrayList();
        private final List<String> campaignIds = Lists.newArrayList();
        private final List<String> partnerIds = Lists.newArrayList();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> dataValues = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();

        private Builder() {
        }

        public Builder addProgram(String program) {
            this.programs.add(program);
            return this;
        }

        public Builder addCampaignId(String campaignId) {
            this.campaignIds.add(campaignId);
            return this;
        }

        public Builder addPartnerId(String partnerId) {
            this.partnerIds.add(partnerId);
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

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public PersonSharesListRequest build() {
            return new PersonSharesListRequest(programs, campaignIds, partnerIds, dataKeys, dataValues, offset, limit);
        }
    }
}
