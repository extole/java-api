package com.extole.client.rest.person.step;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class PersonStepsListRequest {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 20;

    private static final String PARAMETER_NAMES = "names";
    private static final String PARAMETER_CONTAINERS = "containers";
    private static final String PARAMETER_CAMPAIGN_IDS = "campaign_ids";
    private static final String PARAMETER_PROGRAMS = "programs";
    private static final String PARAMETER_JOURNEY_NAMES = "journey_names";
    private static final String PARAMETER_IS_PRIMARY = "is_primary";
    private static final String PARAMETER_DATA_KEYS = "data_keys";
    private static final String PARAMETER_EVENT_IDS = "event_ids";
    private static final String PARAMETER_CAUSE_EVENT_IDS = "cause_event_ids";
    private static final String PARAMETER_ROOT_EVENT_IDS = "root_event_ids";
    private static final String PARAMETER_OFFSET = "offset";
    private static final String PARAMETER_LIMIT = "limit";
    private static final String PARAMETER_JOURNEY_KEY_NAMES = "journey_key_names";
    private static final String PARAMETER_JOURNEY_KEY_VALUES = "journey_key_values";

    private final List<String> names;
    private final List<String> containers;
    private final List<String> campaignIds;
    private final List<String> programs;
    private final List<String> journeyNames;
    private final Optional<Boolean> isPrimary;
    private final List<String> dataKeys;
    private final List<String> eventIds;
    private final List<String> causeEventIds;
    private final List<String> rootEventIds;
    private final Optional<Integer> offset;
    private final Optional<Integer> limit;
    private final List<String> journeyKeyNames;
    private final List<String> journeyKeyValues;

    public PersonStepsListRequest(
        @Parameter(
            description = "Optional step name filter. Will include steps that match at least one of the names.") @QueryParam(PARAMETER_NAMES) List<
                String> names,
        @Parameter(description = "Optional container filter, defaults to all containers. " +
            "Will include steps that match at least one of the containers.") @QueryParam(PARAMETER_CONTAINERS) List<
                String> containers,
        @Parameter(description = "Optional campaign id filter. " +
            "Will include steps that match at least one of the campaign ids.") @QueryParam(PARAMETER_CAMPAIGN_IDS) List<
                String> campaignIds,
        @Parameter(description = "Optional program label filter. " +
            "Will include steps that match at least one of the programs.") @QueryParam(PARAMETER_PROGRAMS) List<
                String> programs,
        @Parameter(description = "Optional journey names filter. " +
            "Will include steps that match at least one of the journey names.") @QueryParam(PARAMETER_JOURNEY_NAMES) List<
                String> journeyNames,
        @Parameter(
            description = "Optional filter for primary steps. Defaults to all steps.") @QueryParam(PARAMETER_IS_PRIMARY) Optional<
                Boolean> isPrimary,
        @Parameter(description = "Optional filter for existence of a specific data keys with non-empty values. " +
            "Will include steps that have at least one of the data keys.") @QueryParam(PARAMETER_DATA_KEYS) List<
                String> dataKeys,
        @Parameter(description = "Optional event IDs filter. " +
            "Will include steps that match at least one of the event IDs.") @QueryParam(PARAMETER_EVENT_IDS) List<
                String> eventIds,
        @Parameter(description = "Optional cause event IDs filter. " +
            "Will include steps that match at least one of the cause event IDs.") @QueryParam(PARAMETER_CAUSE_EVENT_IDS) List<
                String> causeEventIds,
        @Parameter(description = "Optional root event IDs filter. " +
            "Will include steps that match at least one of the root event IDs.") @QueryParam(PARAMETER_ROOT_EVENT_IDS) List<
                String> rootEventIds,
        @Parameter(description = "Optional offset filter, defaults to " + DEFAULT_OFFSET + ".") @DefaultValue(""
            + DEFAULT_OFFSET) @QueryParam(PARAMETER_OFFSET) Optional<Integer> offset,
        @Parameter(description = "Optional limit filter, defaults to " + DEFAULT_LIMIT + ".") @DefaultValue(""
            + DEFAULT_LIMIT) @QueryParam(PARAMETER_LIMIT) Optional<Integer> limit,
        @Parameter(description = "Optional journey key names filter. " +
            "Will include steps, whose journey key matches one of the journey key names.") @QueryParam(PARAMETER_JOURNEY_KEY_NAMES) List<
                String> journeyKeyNames,
        @Parameter(description = "Optional journey key values filter. " +
            "Will include steps that have one of the specified journey keys. " +
            "Valid format is name:value.") @QueryParam(PARAMETER_JOURNEY_KEY_VALUES) List<String> journeyKeyValues) {
        this.names = names == null ? ImmutableList.of() : ImmutableList.copyOf(names);
        this.containers = containers == null ? ImmutableList.of() : ImmutableList.copyOf(containers);
        this.campaignIds = campaignIds == null ? ImmutableList.of() : ImmutableList.copyOf(campaignIds);
        this.programs = programs == null ? ImmutableList.of() : ImmutableList.copyOf(programs);
        this.journeyNames = journeyNames == null ? ImmutableList.of() : ImmutableList.copyOf(journeyNames);
        this.isPrimary = isPrimary;
        this.dataKeys = dataKeys == null
            ? ImmutableList.of()
            : ImmutableList.copyOf(dataKeys);
        this.eventIds = eventIds == null ? ImmutableList.of() : ImmutableList.copyOf(eventIds);
        this.causeEventIds = causeEventIds == null ? ImmutableList.of() : ImmutableList.copyOf(causeEventIds);
        this.rootEventIds = rootEventIds == null ? ImmutableList.of() : ImmutableList.copyOf(rootEventIds);
        this.offset = offset;
        this.limit = limit;
        this.journeyKeyNames = journeyKeyNames == null ? ImmutableList.of() : ImmutableList.copyOf(journeyKeyNames);
        this.journeyKeyValues = journeyKeyValues == null ? ImmutableList.of() : ImmutableList.copyOf(journeyKeyValues);
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

    @QueryParam(PARAMETER_JOURNEY_NAMES)
    public List<String> getJourneyNames() {
        return journeyNames;
    }

    @QueryParam(PARAMETER_IS_PRIMARY)
    public Optional<Boolean> getIsPrimary() {
        return isPrimary;
    }

    @QueryParam(PARAMETER_DATA_KEYS)
    public List<String> getDataKeys() {
        return dataKeys;
    }

    @QueryParam(PARAMETER_EVENT_IDS)
    public List<String> getEventIds() {
        return eventIds;
    }

    @QueryParam(PARAMETER_CAUSE_EVENT_IDS)
    public List<String> getCauseEventIds() {
        return causeEventIds;
    }

    @QueryParam(PARAMETER_ROOT_EVENT_IDS)
    public List<String> getRootEventIds() {
        return rootEventIds;
    }

    @QueryParam(PARAMETER_OFFSET)
    public Optional<Integer> getOffset() {
        return offset;
    }

    @QueryParam(PARAMETER_LIMIT)
    public Optional<Integer> getLimit() {
        return limit;
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

        private final List<String> names = Lists.newArrayList();
        private final List<String> containers = Lists.newArrayList();
        private final List<String> campaignIds = Lists.newArrayList();
        private final List<String> programs = Lists.newArrayList();
        private final List<String> journeyNames = Lists.newArrayList();
        private Optional<Boolean> isPrimary = Optional.empty();
        private final List<String> dataKeys = Lists.newArrayList();
        private final List<String> eventIds = Lists.newArrayList();
        private final List<String> causeEventIds = Lists.newArrayList();
        private final List<String> rootEventIds = Lists.newArrayList();
        private Optional<Integer> offset = Optional.empty();
        private Optional<Integer> limit = Optional.empty();
        private final List<String> journeyKeyNames = Lists.newArrayList();
        private final List<String> journeyKeyValues = Lists.newArrayList();

        private Builder() {
        }

        public Builder addName(String name) {
            this.names.add(name);
            return this;
        }

        public Builder addContainer(String container) {
            this.containers.add(container);
            return this;
        }

        public Builder addCampaignId(String campaignId) {
            this.campaignIds.add(campaignId);
            return this;
        }

        public Builder addProgram(String program) {
            this.programs.add(program);
            return this;
        }

        public Builder addJourneyName(String journeyName) {
            this.journeyNames.add(journeyName);
            return this;
        }

        public Builder withPrimary(Boolean isPrimary) {
            this.isPrimary = Optional.ofNullable(isPrimary);
            return this;
        }

        public Builder addDataKey(String dataKey) {
            this.dataKeys.add(dataKey);
            return this;
        }

        public Builder addEventId(String eventId) {
            this.eventIds.add(eventId);
            return this;
        }

        public Builder addCauseEventId(String causeEventId) {
            this.causeEventIds.add(causeEventId);
            return this;
        }

        public Builder addRootEventId(String rootEventId) {
            this.rootEventIds.add(rootEventId);
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

        public Builder addJourneyKeyName(String journeyKeyName) {
            this.journeyKeyNames.add(journeyKeyName);
            return this;
        }

        public Builder addJourneyKeyValue(String journeyKeyName, String journeyKeyValue) {
            this.journeyKeyValues.add(journeyKeyName + ":" + journeyKeyValue);
            return this;
        }

        public PersonStepsListRequest build() {
            return new PersonStepsListRequest(names, containers, campaignIds, programs, journeyNames, isPrimary,
                dataKeys, eventIds, causeEventIds, rootEventIds, offset, limit, journeyKeyNames, journeyKeyValues);
        }

    }

}
