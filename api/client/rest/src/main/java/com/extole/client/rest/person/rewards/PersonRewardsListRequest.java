package com.extole.client.rest.person.rewards;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;

public class PersonRewardsListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_PROGRAMS = "programs";
    private static final String PARAMETER_CAMPAIGN_IDS = "campaign_ids";
    private static final String PARAMETER_CONTAINERS = "containers";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_DATA_VALUES = "data_values";
    private static final String PARAMETER_REWARD_TYPES = "reward_types";
    private static final String PARAMETER_REWARD_STATES = "reward_states";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";
    private static final String PARAMETER_JOURNEY_NAMES = "journey_names";
    private static final String PARAMETER_JOURNEY_KEY_NAMES = "journey_key_names";
    private static final String PARAMETER_JOURNEY_KEY_VALUES = "journey_key_values";

    private final List<String> programs;
    private final List<String> campaignIds;
    private final List<String> containers;
    private final List<String> dataKeys;
    private final List<String> dataValues;
    private final List<RewardSupplierType> rewardTypes;
    private final List<RewardState> rewardStates;
    private final int offset;
    private final int limit;
    private final List<String> journeyNames;
    private final List<String> journeyKeyNames;
    private final List<String> journeyKeyValues;

    public PersonRewardsListRequest(
        @Parameter(description = "Optional campaign id filter. " +
            "Will include rewards that match at least one of the campaign ids.") @QueryParam(PARAMETER_CAMPAIGN_IDS) List<
                String> campaignIds,
        @Parameter(description = "Optional program label filter. " +
            "Will include rewards that match at least one of the programs.") @QueryParam(PARAMETER_PROGRAMS) List<
                String> programs,
        @Parameter(description = "Optional containers filter. " +
            "Will include rewards for all containers if not specified or match at least one of " +
            "the specified containers.") @QueryParam(PARAMETER_CONTAINERS) List<String> containers,
        @Parameter(description = "Optional data keys filter. " +
            "Will include rewards that match at least one of the data keys.") @QueryParam(PARAMETER_DATA_KEYS) List<
                String> dataKeys,
        @Parameter(description = "Optional filter for existence of specific data values. " +
            "Will include rewards that have at least one of the specified data name-value pair. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_DATA_VALUES) List<String> dataValues,
        @Parameter(description = "Optional reward types filter. " +
            "Will include rewards that match at least one of the reward types.") @QueryParam(PARAMETER_REWARD_TYPES) List<
                RewardSupplierType> rewardTypes,
        @Parameter(description = "Optional reward states filter. " +
            "Will include rewards that match at least one of the reward states.") @QueryParam(PARAMETER_REWARD_STATES) List<
                RewardState> rewardStates,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit,
        @Parameter(description = "Optional journey names filter. " +
            "Will include rewards that match at least one of the journey names.") @QueryParam(PARAMETER_JOURNEY_NAMES) List<
                String> journeyNames,
        @Parameter(description = "Optional journey key names filter. " +
            "Will include rewards, whose journey key matches one of the journey key names.") @QueryParam(PARAMETER_JOURNEY_KEY_NAMES) List<
                String> journeyKeyNames,
        @Parameter(description = "Optional journey key values filter. " +
            "Will include rewards that have one of the specified journey keys. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_JOURNEY_KEY_VALUES) List<String> journeyKeyValues) {
        this.programs = programs == null ? ImmutableList.of() : ImmutableList.copyOf(programs);
        this.campaignIds = campaignIds == null ? ImmutableList.of() : ImmutableList.copyOf(campaignIds);
        this.containers = containers == null ? ImmutableList.of() : ImmutableList.copyOf(containers);
        this.dataKeys = dataKeys == null ? ImmutableList.of() : ImmutableList.copyOf(dataKeys);
        this.dataValues = dataValues == null ? ImmutableList.of() : ImmutableList.copyOf(dataValues);
        this.rewardTypes = rewardTypes == null ? ImmutableList.of() : ImmutableList.copyOf(rewardTypes);
        this.rewardStates = rewardStates == null ? ImmutableList.of() : ImmutableList.copyOf(rewardStates);
        this.offset = offset.orElse(Integer.valueOf(DEFAULT_OFFSET)).intValue();
        this.limit = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
        this.journeyNames = journeyNames == null ? ImmutableList.of() : ImmutableList.copyOf(journeyNames);
        this.journeyKeyNames = journeyKeyNames == null ? ImmutableList.of() : ImmutableList.copyOf(journeyKeyNames);
        this.journeyKeyValues = journeyKeyValues == null ? ImmutableList.of() : ImmutableList.copyOf(journeyKeyValues);
    }

    @QueryParam(PARAMETER_PROGRAMS)
    public List<String> getPrograms() {
        return programs;
    }

    @QueryParam(PARAMETER_CAMPAIGN_IDS)
    public List<String> getCampaignIds() {
        return campaignIds;
    }

    @QueryParam(PARAMETER_CONTAINERS)
    public List<String> getContainers() {
        return containers;
    }

    @QueryParam(PARAMETER_DATA_KEYS)
    public List<String> getDataKeys() {
        return dataKeys;
    }

    @QueryParam(PARAMETER_DATA_VALUES)
    public List<String> getDataValues() {
        return dataValues;
    }

    @QueryParam(PARAMETER_REWARD_TYPES)
    public List<RewardSupplierType> getRewardTypes() {
        return rewardTypes;
    }

    @QueryParam(PARAMETER_REWARD_STATES)
    public List<RewardState> getRewardStates() {
        return rewardStates;
    }

    @QueryParam(PARAMETER_OFFSET)
    public int getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public int getLimit() {
        return limit;
    }

    @QueryParam(PARAMETER_JOURNEY_NAMES)
    public List<String> getJourneyNames() {
        return journeyNames;
    }

    @QueryParam(PARAMETER_JOURNEY_KEY_NAMES)
    public List<String> getJourneyKeyNames() {
        return journeyKeyNames;
    }

    @QueryParam(PARAMETER_JOURNEY_KEY_VALUES)
    public List<String> getJourneyKeyValues() {
        return journeyKeyValues;
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
        private final List<String> containers = Lists.newArrayList();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> dataValues = Lists.newArrayList();
        private final List<RewardSupplierType> rewardTypes = Lists.newArrayList();
        private final List<RewardState> rewardStates = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private final List<String> journeyNames = Lists.newArrayList();
        private final List<String> journeyKeyNames = Lists.newArrayList();
        private final List<String> journeyKeyValues = Lists.newArrayList();

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

        public Builder addContainer(String container) {
            this.containers.add(container);
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

        public Builder addRewardType(RewardSupplierType rewardType) {
            this.rewardTypes.add(rewardType);
            return this;
        }

        public Builder addRewardState(RewardState rewardState) {
            this.rewardStates.add(rewardState);
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

        public Builder addJourneyName(String journeyName) {
            this.journeyNames.add(journeyName);
            return this;
        }

        public Builder addJourneyKeyName(String journeyKeyName) {
            this.journeyKeyNames.add(journeyKeyName);
            return this;
        }

        public Builder addJourneyKeyValue(String journeyKeyName, String journeyKeyValue) {
            this.journeyKeyValues.add(journeyKeyName + ":" + journeyKeyValue);
            return this;
        }

        public PersonRewardsListRequest build() {
            return new PersonRewardsListRequest(campaignIds, programs, containers, dataKeys, dataValues, rewardTypes,
                rewardStates, offset, limit, journeyNames, journeyKeyNames, journeyKeyValues);
        }

    }

}
