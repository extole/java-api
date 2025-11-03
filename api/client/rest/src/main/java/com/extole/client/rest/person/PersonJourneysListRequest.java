package com.extole.client.rest.person;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonJourneysListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 100;

    private static final String PARAMETER_IDS = "ids";
    private static final String PARAMETER_NAMES = "names";
    private static final String PARAMETER_PROGRAMS = "programs";
    private static final String PARAMETER_CAMPAIGN_IDS = "campaign_ids";
    private static final String PARAMETER_CONTAINERS = "containers";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_DATA_VALUES = "data_values";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";
    private static final String PARAMETER_KEY_NAMES = "key_names";
    private static final String PARAMETER_KEY_VALUES = "key_values";

    private final List<String> ids;
    private final List<String> names;
    private final List<String> programs;
    private final List<String> campaignIds;
    private final List<String> containers;
    private final List<String> dataKeys;
    private final List<String> dataValues;
    private final int offset;
    private final int limit;
    private final List<String> keyNames;
    private final List<String> keyValues;

    public PersonJourneysListRequest(
        @Parameter(description = "Optional journey ids filter. " +
            "Will include journeys that match at least one of the ids.") @QueryParam(PARAMETER_IDS) List<String> ids,
        @Parameter(description = "Optional journey names filter. " +
            "Will include journeys that match at least one of the names.") @QueryParam(PARAMETER_NAMES) List<
                String> names,
        @Parameter(description = "Optional program label filter. " +
            "Will include journeys that match at least one of the programs.") @QueryParam(PARAMETER_PROGRAMS) List<
                String> programs,
        @Parameter(description = "Optional campaign id filter. " +
            "Will include journeys that match at least one of the campaign ids.") @QueryParam(PARAMETER_CAMPAIGN_IDS) List<
                String> campaignIds,
        @Parameter(description = "Optional container filter, defaults to all containers. " +
            "Will include journeys that match at least one of the containers.") @QueryParam(PARAMETER_CONTAINERS) List<
                String> containers,
        @Parameter(description = "Optional filter for existence of specific data keys with non-empty values. " +
            "Will include journeys that have at least one of the data keys.") @QueryParam(PARAMETER_DATA_KEYS) List<
                String> dataKeys,
        @Parameter(description = "Optional filter for existence of specific data values. " +
            "Will include journeys that have at least one of the specified data name-value pair. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_DATA_VALUES) List<String> dataValues,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam(PARAMETER_OFFSET) Integer offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam(PARAMETER_LIMIT) Integer limit,
        @Parameter(description = "Optional key names filter. " +
            "Will include journeys, whose key matches at least one of the key names.") @QueryParam(PARAMETER_KEY_NAMES) List<
                String> keyNames,
        @Parameter(description = "Optional key values filter. " +
            "Will include journeys that have one of the specified keys. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_KEY_VALUES) List<String> keyValues) {
        this.ids = ids == null ? ImmutableList.of() : ImmutableList.copyOf(ids);
        this.names = names == null ? ImmutableList.of() : ImmutableList.copyOf(names);
        this.programs = programs == null ? ImmutableList.of() : ImmutableList.copyOf(programs);
        this.campaignIds = campaignIds == null ? ImmutableList.of() : ImmutableList.copyOf(campaignIds);
        this.containers = containers == null ? ImmutableList.of() : ImmutableList.copyOf(containers);
        this.dataKeys = dataKeys == null ? ImmutableList.of() : ImmutableList.copyOf(dataKeys);
        this.dataValues = dataValues == null ? ImmutableList.of() : ImmutableList.copyOf(dataValues);
        this.offset = offset == null ? DEFAULT_OFFSET : offset.intValue();
        this.limit = limit == null ? DEFAULT_LIMIT : limit.intValue();
        this.keyNames = keyNames == null ? ImmutableList.of() : ImmutableList.copyOf(keyNames);
        this.keyValues = keyValues == null ? ImmutableList.of() : ImmutableList.copyOf(keyValues);
    }

    @QueryParam(PARAMETER_IDS)
    public List<String> getIds() {
        return ids;
    }

    @QueryParam(PARAMETER_NAMES)
    public List<String> getNames() {
        return names;
    }

    @QueryParam(PARAMETER_CONTAINERS)
    public List<String> getContainers() {
        return containers;
    }

    @QueryParam(PARAMETER_CAMPAIGN_IDS)
    public List<String> getCampaignIds() {
        return campaignIds;
    }

    @QueryParam(PARAMETER_PROGRAMS)
    public List<String> getPrograms() {
        return programs;
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

    @QueryParam(PARAMETER_KEY_NAMES)
    public List<String> getKeyNames() {
        return keyNames;
    }

    @QueryParam(PARAMETER_KEY_VALUES)
    public List<String> getKeyValues() {
        return keyValues;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<String> ids = Lists.newArrayList();
        private final List<String> names = Lists.newArrayList();
        private final List<String> programs = Lists.newArrayList();
        private final List<String> campaignIds = Lists.newArrayList();
        private final List<String> containers = Lists.newArrayList();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> dataValues = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private final List<String> keyNames = Lists.newArrayList();
        private final List<String> keyValues = Lists.newArrayList();

        private Builder() {
        }

        public Builder addId(String id) {
            this.ids.add(id);
            return this;
        }

        public Builder addName(String name) {
            this.names.add(name);
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

        public Builder withOffset(Integer offset) {
            this.offset = Optional.ofNullable(offset);
            return this;
        }

        public Builder withLimit(Integer limit) {
            this.limit = Optional.ofNullable(limit);
            return this;
        }

        public Builder addKeyName(String keyName) {
            this.keyNames.add(keyName);
            return this;
        }

        public Builder addKeyValue(String keyName, String keyValue) {
            this.keyValues.add(keyName + ":" + keyValue);
            return this;
        }

        public PersonJourneysListRequest build() {
            return new PersonJourneysListRequest(ids, names, programs, campaignIds, containers, dataKeys, dataValues,
                offset.orElse(null), limit.orElse(null), keyNames, keyValues);
        }

    }

}
