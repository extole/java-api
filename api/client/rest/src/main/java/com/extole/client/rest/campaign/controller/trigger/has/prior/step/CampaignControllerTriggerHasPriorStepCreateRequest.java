package com.extole.client.rest.campaign.controller.trigger.has.prior.step;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.campaign.controller.trigger.has.prior.step.PartnerEventId;
import com.extole.api.person.Person;
import com.extole.api.trigger.has.prior.step.HasPriorStepTriggerContext;
import com.extole.api.trigger.has.prior.step.StepHasPriorStepTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public final class CampaignControllerTriggerHasPriorStepCreateRequest extends CampaignControllerTriggerRequest {

    private static final String FILTER_NAMES = "filter_names";
    private static final String FILTER_SCOPE = "filter_scope";
    private static final String FILTER_PARTNER_EVENT_ID_NAME = "filter_partner_event_id_name";
    private static final String FILTER_PARTNER_EVENT_ID_VALUE = "filter_partner_event_id_value";
    private static final String FILTER_PARTNER_EVENT_ID = "filter_partner_event_id";
    private static final String FILTER_MIN_AGE = "filter_min_age";
    private static final String FILTER_MAX_AGE = "filter_max_age";
    private static final String FILTER_MIN_VALUE = "filter_min_value";
    private static final String FILTER_MAX_VALUE = "filter_max_value";
    private static final String FILTER_QUALITY = "filter_quality";
    private static final String FILTER_EXPRESSIONS = "filter_expressions";
    private static final String FILTER_EXPRESSION = "filter_expression";
    private static final String FILTER_PROGRAM_LABEL = "filter_program_label";
    private static final String FILTER_CAMPAIGN_ID = "filter_campaign_id";
    private static final String FILTER_PROGRAM_LABELS = "filter_program_labels";
    private static final String FILTER_CAMPAIGN_IDS = "filter_campaign_ids";
    private static final String FILTER_MIN_DATE = "filter_min_date";
    private static final String FILTER_MAX_DATE = "filter_max_date";
    private static final String SUM_OF_VALUE_MIN = "sum_of_value_min";
    private static final String SUM_OF_VALUE_MAX = "sum_of_value_max";
    private static final String COUNT_MIN = "count_min";
    private static final String COUNT_MAX = "count_max";
    private static final String COUNT_MATCHES = "count_matches";
    private static final String PERSON_ID = "person_id";
    private static final String HAVING_ALL_NAMES = "having_all_names";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope>> filterScope;
    private final Omissible<Optional<String>> filterPartnerEventIdName;
    private final Omissible<Optional<String>> filterPartnerEventIdValue;
    private final Omissible<
        BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>>> filterPartnerEventId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMinValue;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMaxValue;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality>> filterQuality;
    private final Omissible<Set<String>> filterExpressions;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>>> filterExpression;
    private final Omissible<Optional<String>> filterProgramLabel;
    private final Omissible<Optional<String>> filterCampaignId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> filterProgramLabels;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>>> filterCampaignIds;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMin;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMax;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches;
    private final Omissible<RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>>> personId;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> havingAllNames;

    @JsonCreator
    private CampaignControllerTriggerHasPriorStepCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(FILTER_NAMES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterNames,
        @JsonProperty(FILTER_SCOPE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope>> filterScope,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_NAME) Omissible<Optional<String>> filterPartnerEventIdName,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_VALUE) Omissible<Optional<String>> filterPartnerEventIdValue,
        @JsonProperty(FILTER_PARTNER_EVENT_ID) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>>> filterPartnerEventId,
        @JsonProperty(FILTER_MIN_AGE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge,
        @JsonProperty(FILTER_MIN_VALUE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMinValue,
        @JsonProperty(FILTER_MAX_VALUE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMaxValue,
        @JsonProperty(FILTER_QUALITY) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality>> filterQuality,
        @JsonProperty(FILTER_EXPRESSIONS) Omissible<Set<String>> filterExpressions,
        @JsonProperty(FILTER_EXPRESSION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>>> filterExpression,
        @JsonProperty(FILTER_PROGRAM_LABEL) Omissible<Optional<String>> filterProgramLabel,
        @JsonProperty(FILTER_CAMPAIGN_ID) Omissible<Optional<String>> filterCampaignId,
        @JsonProperty(FILTER_PROGRAM_LABELS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> filterProgramLabels,
        @JsonProperty(FILTER_CAMPAIGN_IDS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>>> filterCampaignIds,
        @JsonProperty(FILTER_MIN_DATE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate,
        @JsonProperty(SUM_OF_VALUE_MIN) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMin,
        @JsonProperty(SUM_OF_VALUE_MAX) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMax,
        @JsonProperty(COUNT_MIN) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin,
        @JsonProperty(COUNT_MAX) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax,
        @JsonProperty(COUNT_MATCHES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches,
        @JsonProperty(PERSON_ID) Omissible<
            RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>>> personId,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(HAVING_ALL_NAMES) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> havingAllNames) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.filterNames = filterNames;
        this.filterScope = filterScope;
        this.filterPartnerEventIdName = filterPartnerEventIdName;
        this.filterPartnerEventIdValue = filterPartnerEventIdValue;
        this.filterPartnerEventId = filterPartnerEventId;
        this.filterMinAge = filterMinAge;
        this.filterMaxAge = filterMaxAge;
        this.filterMinValue = filterMinValue;
        this.filterMaxValue = filterMaxValue;
        this.filterQuality = filterQuality;
        this.filterExpressions = filterExpressions;
        this.filterExpression = filterExpression;
        this.filterProgramLabel = filterProgramLabel;
        this.filterCampaignId = filterCampaignId;
        this.filterProgramLabels = filterProgramLabels;
        this.filterCampaignIds = filterCampaignIds;
        this.filterMinDate = filterMinDate;
        this.filterMaxDate = filterMaxDate;
        this.sumOfValueMin = sumOfValueMin;
        this.sumOfValueMax = sumOfValueMax;
        this.countMin = countMin;
        this.countMax = countMax;
        this.countMatches = countMatches;
        this.personId = personId;
        this.havingAllNames = havingAllNames;
    }

    @JsonProperty(FILTER_NAMES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope>> getFilterScope() {
        return filterScope;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>>>
        getFilterPartnerEventId() {
        return filterPartnerEventId;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID_NAME)
    public Omissible<Optional<String>> getFilterPartnerEventIdName() {
        return filterPartnerEventIdName;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID_VALUE)
    public Omissible<Optional<String>> getFilterPartnerEventIdValue() {
        return filterPartnerEventIdValue;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_VALUE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getFilterMinValue() {
        return filterMinValue;
    }

    @JsonProperty(FILTER_MAX_VALUE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getFilterMaxValue() {
        return filterMaxValue;
    }

    @JsonProperty(FILTER_QUALITY)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality>> getFilterQuality() {
        return filterQuality;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Omissible<Set<String>> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(FILTER_EXPRESSION)
    public
        Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>>>
        getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(FILTER_PROGRAM_LABEL)
    public Omissible<Optional<String>> getFilterProgramLabel() {
        return filterProgramLabel;
    }

    @JsonProperty(FILTER_CAMPAIGN_ID)
    public Omissible<Optional<String>> getFilterCampaignId() {
        return filterCampaignId;
    }

    @JsonProperty(FILTER_PROGRAM_LABELS)
    public
        Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>>
        getFilterProgramLabels() {
        return filterProgramLabels;
    }

    @JsonProperty(FILTER_CAMPAIGN_IDS)
    public
        Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>>>
        getFilterCampaignIds() {
        return filterCampaignIds;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(SUM_OF_VALUE_MIN)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getSumOfValueMin() {
        return sumOfValueMin;
    }

    @JsonProperty(SUM_OF_VALUE_MAX)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getSumOfValueMax() {
        return sumOfValueMax;
    }

    @JsonProperty(COUNT_MIN)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> getCountMin() {
        return countMin;
    }

    @JsonProperty(COUNT_MAX)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> getCountMax() {
        return countMax;
    }

    @JsonProperty(COUNT_MATCHES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(PERSON_ID)
    public Omissible<RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>>> getPersonId() {
        return personId;
    }

    @JsonProperty(HAVING_ALL_NAMES)
    public
        Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>>
        getHavingAllNames() {
        return havingAllNames;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope>> filterScope =
            Omissible.omitted();
        private Omissible<Optional<String>> filterPartnerEventIdName = Omissible.omitted();
        private Omissible<Optional<String>> filterPartnerEventIdValue = Omissible.omitted();
        private Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>>> filterPartnerEventId =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMinValue =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> filterMaxValue =
            Omissible.omitted();
        private Omissible<Set<String>> filterExpressions = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality>> filterQuality =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>>> filterExpression =
                Omissible.omitted();
        private Omissible<Optional<String>> filterProgramLabel = Omissible.omitted();
        private Omissible<Optional<String>> filterCampaignId = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> filterProgramLabels =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>>> filterCampaignIds =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMin =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfValueMax =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches =
            Omissible.omitted();
        private Omissible<RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>>> personId =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>> havingAllNames =
                Omissible.omitted();

        private Builder() {
        }

        public Builder withFilterNames(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames) {
            this.filterNames = Omissible.of(filterNames);
            return this;
        }

        public Builder withFilterNames(Set<String> filterNames) {
            this.filterNames = Omissible.of(Provided.of(filterNames));
            return this;
        }

        public Builder clearFilterNames() {
            this.filterNames = Omissible.of(Provided.emptySet());
            return this;
        }

        public Builder withFilterScope(BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope> filterScope) {
            this.filterScope = Omissible.of(filterScope);
            return this;
        }

        public Builder withFilterScope(StepFilterScope filterScope) {
            this.filterScope = Omissible.of(Provided.of(filterScope));
            return this;
        }

        public Builder withFilterPartnerEventIdName(Optional<String> filterPartnerEventIdName) {
            this.filterPartnerEventIdName = Omissible.of(filterPartnerEventIdName);
            return this;
        }

        public Builder withFilterPartnerEventIdValue(Optional<String> filterPartnerEventIdValue) {
            this.filterPartnerEventIdValue = Omissible.of(filterPartnerEventIdValue);
            return this;
        }

        public Builder withFilterPartnerEventId(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>> filterPartnerEventId) {
            this.filterPartnerEventId = Omissible.of(filterPartnerEventId);
            return this;
        }

        public Builder
            withFilterMinAge(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMinAge) {
            this.filterMinAge = Omissible.of(filterMinAge);
            return this;
        }

        public Builder
            withFilterMaxAge(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMaxAge) {
            this.filterMaxAge = Omissible.of(filterMaxAge);
            return this;
        }

        public Builder
            withFilterMinValue(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> filterMinValue) {
            this.filterMinValue = Omissible.of(filterMinValue);
            return this;
        }

        public Builder
            withFilterMaxValue(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> filterMaxValue) {
            this.filterMaxValue = Omissible.of(filterMaxValue);
            return this;
        }

        public Builder withFilterQuality(BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality> filterQuality) {
            this.filterQuality = Omissible.of(filterQuality);
            return this;
        }

        public Builder withFilterExpressions(Set<String> filterExpressions) {
            this.filterExpressions = Omissible.of(filterExpressions);
            return this;
        }

        public Builder withFilterExpression(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>> filterExpression) {
            this.filterExpression = Omissible.of(filterExpression);
            return this;
        }

        public Builder withFilterProgramLabel(Optional<String> filterProgramLabel) {
            this.filterProgramLabel = Omissible.of(filterProgramLabel);
            return this;
        }

        public Builder
            withFilterCampaignId(Optional<String> filterCampaignId) {
            this.filterCampaignId = Omissible.of(filterCampaignId);
            return this;
        }

        public Builder withFilterProgramLabels(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>> filterProgramLabels) {
            this.filterProgramLabels = Omissible.of(filterProgramLabels);
            return this;
        }

        public Builder withFilterCampaignIds(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>> filterCampaignIds) {
            this.filterCampaignIds = Omissible.of(filterCampaignIds);
            return this;
        }

        public Builder
            withFilterMinDate(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMinDate) {
            this.filterMinDate = Omissible.of(filterMinDate);
            return this;
        }

        public Builder
            withFilterMaxDate(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMaxDate) {
            this.filterMaxDate = Omissible.of(filterMaxDate);
            return this;
        }

        public Builder
            withSumOfValueMin(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfValueMin) {
            this.sumOfValueMin = Omissible.of(sumOfValueMin);
            return this;
        }

        public Builder
            withSumOfValueMax(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfValueMax) {
            this.sumOfValueMax = Omissible.of(sumOfValueMax);
            return this;
        }

        public Builder withCountMin(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin) {
            this.countMin = Omissible.of(countMin);
            return this;
        }

        public Builder withCountMax(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax) {
            this.countMax = Omissible.of(countMax);
            return this;
        }

        public Builder withCountMatches(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches) {
            this.countMatches = Omissible.of(countMatches);
            return this;
        }

        public Builder withPersonId(RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> personId) {
            this.personId = Omissible.of(personId);
            return this;
        }

        public Builder withHavingAllNames(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>> havingAllNames) {
            this.havingAllNames = Omissible.of(havingAllNames);
            return this;
        }

        public Builder withHavingAllNames(Set<String> havingAllNames) {
            this.havingAllNames = Omissible.of(Provided.nestedOf(ImmutableSet.copyOf(havingAllNames)));
            return this;
        }

        @Override
        public CampaignControllerTriggerHasPriorStepCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerHasPriorStepCreateRequest(
                triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                filterNames,
                filterScope,
                filterPartnerEventIdName,
                filterPartnerEventIdValue,
                filterPartnerEventId,
                filterMinAge,
                filterMaxAge,
                filterMinValue,
                filterMaxValue,
                filterQuality,
                filterExpressions,
                filterExpression,
                filterProgramLabel,
                filterCampaignId,
                filterProgramLabels,
                filterCampaignIds,
                filterMinDate,
                filterMaxDate,
                sumOfValueMin,
                sumOfValueMax,
                countMin,
                countMax,
                countMatches,
                personId,
                componentIds,
                componentReferences,
                havingAllNames);
        }

    }

}
