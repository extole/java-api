package com.extole.client.rest.campaign.configuration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.campaign.controller.trigger.has.prior.step.PartnerEventId;
import com.extole.api.person.Person;
import com.extole.api.trigger.has.prior.step.HasPriorStepTriggerContext;
import com.extole.api.trigger.has.prior.step.StepHasPriorStepTriggerContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerHasPriorStepConfiguration extends CampaignControllerTriggerConfiguration {

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

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope> filterScope;
    private final Optional<String> filterPartnerEventIdName;
    private final Optional<String> filterPartnerEventIdValue;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>> filterPartnerEventId;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMinAge;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMaxAge;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> filterMinValue;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> filterMaxValue;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality> filterQuality;
    private final Set<String> filterExpressions;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>> filterExpression;
    private final Optional<String> filterProgramLabel;
    private final Optional<String> filterCampaignId;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>> filterProgramLabels;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>> filterCampaignIds;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMinDate;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMaxDate;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfValueMin;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfValueMax;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> personId;

    public CampaignControllerTriggerHasPriorStepConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(FILTER_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames,
        @JsonProperty(FILTER_SCOPE) BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope> filterScope,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_NAME) Optional<String> filterPartnerEventIdName,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_VALUE) Optional<String> filterPartnerEventIdValue,
        @JsonProperty(FILTER_PARTNER_EVENT_ID) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<PartnerEventId>> filterPartnerEventId,
        @JsonProperty(FILTER_MIN_AGE) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMaxAge,
        @JsonProperty(FILTER_MIN_VALUE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> filterMinValue,
        @JsonProperty(FILTER_MAX_VALUE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> filterMaxValue,
        @JsonProperty(FILTER_QUALITY) BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality> filterQuality,
        @JsonProperty(FILTER_EXPRESSIONS) Set<String> filterExpressions,
        @JsonProperty(FILTER_EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>> filterExpression,
        @JsonProperty(FILTER_PROGRAM_LABEL) Optional<String> filterProgramLabel,
        @JsonProperty(FILTER_CAMPAIGN_ID) Optional<String> filterCampaignId,
        @JsonProperty(FILTER_PROGRAM_LABELS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>> filterProgramLabels,
        @JsonProperty(FILTER_CAMPAIGN_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>> filterCampaignIds,
        @JsonProperty(FILTER_MIN_DATE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<ZonedDateTime>> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<ZonedDateTime>> filterMaxDate,
        @JsonProperty(SUM_OF_VALUE_MIN) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> sumOfValueMin,
        @JsonProperty(SUM_OF_VALUE_MAX) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> sumOfValueMax,
        @JsonProperty(COUNT_MIN) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin,
        @JsonProperty(COUNT_MAX) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax,
        @JsonProperty(COUNT_MATCHES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches,
        @JsonProperty(PERSON_ID) RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> personId,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.HAS_PRIOR_STEP, triggerPhase, name, description, enabled,
            negated, componentReferences);
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
        this.filterExpressions = filterExpressions != null ? ImmutableSet.copyOf(filterExpressions) : ImmutableSet.of();
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
    }

    @JsonProperty(FILTER_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, StepFilterScope> getFilterScope() {
        return filterScope;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID_NAME)
    public Optional<String> getFilterPartnerEventIdName() {
        return filterPartnerEventIdName;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID_VALUE)
    public Optional<String> getFilterPartnerEventIdValue() {
        return filterPartnerEventIdValue;
    }

    @JsonProperty(FILTER_PARTNER_EVENT_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<PartnerEventId>> getFilterPartnerEventId() {
        return filterPartnerEventId;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_VALUE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getFilterMinValue() {
        return filterMinValue;
    }

    @JsonProperty(FILTER_MAX_VALUE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getFilterMaxValue() {
        return filterMaxValue;
    }

    @JsonProperty(FILTER_QUALITY)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, StepQuality> getFilterQuality() {
        return filterQuality;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Set<String> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(FILTER_EXPRESSION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean>>
        getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(FILTER_PROGRAM_LABEL)
    public Optional<String> getFilterProgramLabel() {
        return filterProgramLabel;
    }

    @JsonProperty(FILTER_CAMPAIGN_ID)
    public Optional<String> getFilterCampaignId() {
        return filterCampaignId;
    }

    @JsonProperty(FILTER_PROGRAM_LABELS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>>>
        getFilterProgramLabels() {
        return filterProgramLabels;
    }

    @JsonProperty(FILTER_CAMPAIGN_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>>>
        getFilterCampaignIds() {
        return filterCampaignIds;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(SUM_OF_VALUE_MIN)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getSumOfValueMin() {
        return sumOfValueMin;
    }

    @JsonProperty(SUM_OF_VALUE_MAX)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getSumOfValueMax() {
        return sumOfValueMax;
    }

    @JsonProperty(COUNT_MIN)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> getCountMin() {
        return countMin;
    }

    @JsonProperty(COUNT_MAX)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> getCountMax() {
        return countMax;
    }

    @JsonProperty(COUNT_MATCHES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(PERSON_ID)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> getPersonId() {
        return personId;
    }

}
