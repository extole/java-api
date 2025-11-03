package com.extole.client.rest.campaign.built.controller.trigger;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.controller.trigger.has.prior.step.PartnerEventId;
import com.extole.api.person.Person;
import com.extole.api.trigger.has.prior.step.HasPriorStepTriggerContext;
import com.extole.api.trigger.has.prior.step.StepHasPriorStepTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.StepFilterScope;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.StepQuality;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerHasPriorStepResponse extends BuiltCampaignControllerTriggerResponse {

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

    private final Set<String> filterNames;
    private final StepFilterScope filterScope;
    private final Optional<String> filterPartnerEventIdName;
    private final Optional<String> filterPartnerEventIdValue;
    private final Optional<PartnerEventId> partnerEventId;
    private final Optional<Duration> filterMinAge;
    private final Optional<Duration> filterMaxAge;
    private final Optional<BigDecimal> filterMinValue;
    private final Optional<BigDecimal> filterMaxValue;
    private final StepQuality filterQuality;
    private final Set<String> filterExpressions;
    private final RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean> filterExpression;
    private final Optional<String> filterProgramLabel;
    private final Optional<String> filterCampaignId;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>> filterProgramLabels;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>> filterCampaignIds;
    private final Optional<ZonedDateTime> filterMinDate;
    private final Optional<ZonedDateTime> filterMaxDate;
    private final Optional<BigDecimal> sumOfValueMin;
    private final Optional<BigDecimal> sumOfValueMax;
    private final Optional<Integer> countMin;
    private final Optional<Integer> countMax;
    private final Set<Integer> countMatches;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> personId;
    private final RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>> havingAllNames;

    public BuiltCampaignControllerTriggerHasPriorStepResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(FILTER_NAMES) Set<String> filterNames,
        @JsonProperty(FILTER_SCOPE) StepFilterScope filterScope,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_NAME) Optional<String> filterPartnerEventIdName,
        @JsonProperty(FILTER_PARTNER_EVENT_ID_VALUE) Optional<String> filterPartnerEventIdValue,
        @JsonProperty(FILTER_PARTNER_EVENT_ID) Optional<PartnerEventId> partnerEventId,
        @JsonProperty(FILTER_MIN_AGE) Optional<Duration> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) Optional<Duration> filterMaxAge,
        @JsonProperty(FILTER_MIN_VALUE) Optional<BigDecimal> filterMinValue,
        @JsonProperty(FILTER_MAX_VALUE) Optional<BigDecimal> filterMaxValue,
        @JsonProperty(FILTER_QUALITY) StepQuality filterQuality,
        @JsonProperty(FILTER_EXPRESSIONS) Set<String> filterExpressions,
        @JsonProperty(FILTER_EXPRESSION) RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean> filterExpression,
        @JsonProperty(FILTER_PROGRAM_LABEL) Optional<String> filterProgramLabel,
        @JsonProperty(FILTER_CAMPAIGN_ID) Optional<String> filterCampaignId,
        @JsonProperty(FILTER_PROGRAM_LABELS) RuntimeEvaluatable<HasPriorStepTriggerContext,
            Set<String>> filterProgramLabels,
        @JsonProperty(FILTER_CAMPAIGN_IDS) RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>> filterCampaignIds,
        @JsonProperty(FILTER_MIN_DATE) Optional<ZonedDateTime> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) Optional<ZonedDateTime> filterMaxDate,
        @JsonProperty(SUM_OF_VALUE_MIN) Optional<BigDecimal> sumOfValueMin,
        @JsonProperty(SUM_OF_VALUE_MAX) Optional<BigDecimal> sumOfValueMax,
        @JsonProperty(COUNT_MIN) Optional<Integer> countMin,
        @JsonProperty(COUNT_MAX) Optional<Integer> countMax,
        @JsonProperty(COUNT_MATCHES) Set<Integer> countMatches,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(PERSON_ID) RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> personId,
        @JsonProperty(HAVING_ALL_NAMES) RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>> havingAllNames) {
        super(triggerId,
            CampaignControllerTriggerType.HAS_PRIOR_STEP,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);
        this.filterNames = filterNames;
        this.filterScope = filterScope;
        this.filterPartnerEventIdName = filterPartnerEventIdName;
        this.filterPartnerEventIdValue = filterPartnerEventIdValue;
        this.partnerEventId = partnerEventId;
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
        this.countMatches = countMatches != null ? ImmutableSet.copyOf(countMatches) : ImmutableSet.of();
        this.personId = personId;
        this.havingAllNames = havingAllNames;
    }

    @JsonProperty(FILTER_NAMES)
    public Set<String> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public StepFilterScope getFilterScope() {
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
    public Optional<PartnerEventId> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public Optional<Duration> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public Optional<Duration> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_VALUE)
    public Optional<BigDecimal> getFilterMinValue() {
        return filterMinValue;
    }

    @JsonProperty(FILTER_MAX_VALUE)
    public Optional<BigDecimal> getFilterMaxValue() {
        return filterMaxValue;
    }

    @JsonProperty(FILTER_QUALITY)
    public StepQuality getFilterQuality() {
        return filterQuality;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Set<String> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(FILTER_EXPRESSION)
    public RuntimeEvaluatable<StepHasPriorStepTriggerContext, Boolean> getFilterExpression() {
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
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>> getFilterProgramLabels() {
        return filterProgramLabels;
    }

    @JsonProperty(FILTER_CAMPAIGN_IDS)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Set<Id<?>>> getFilterCampaignIds() {
        return filterCampaignIds;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public Optional<ZonedDateTime> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public Optional<ZonedDateTime> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(SUM_OF_VALUE_MIN)
    public Optional<BigDecimal> getSumOfValueMin() {
        return sumOfValueMin;
    }

    @JsonProperty(SUM_OF_VALUE_MAX)
    public Optional<BigDecimal> getSumOfValueMax() {
        return sumOfValueMax;
    }

    @JsonProperty(COUNT_MIN)
    public Optional<Integer> getCountMin() {
        return countMin;
    }

    @JsonProperty(COUNT_MAX)
    public Optional<Integer> getCountMax() {
        return countMax;
    }

    @JsonProperty(COUNT_MATCHES)
    public Set<Integer> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(PERSON_ID)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Optional<Id<Person>>> getPersonId() {
        return personId;
    }

    @JsonProperty(HAVING_ALL_NAMES)
    public RuntimeEvaluatable<HasPriorStepTriggerContext, Set<String>> getHavingAllNames() {
        return havingAllNames;
    }

}
