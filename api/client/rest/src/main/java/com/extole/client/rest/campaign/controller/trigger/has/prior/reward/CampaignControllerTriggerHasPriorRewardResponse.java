package com.extole.client.rest.campaign.controller.trigger.has.prior.reward;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.MonthDay;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.trigger.has.prior.reward.HasPriorRewardTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerHasPriorRewardResponse extends CampaignControllerTriggerResponse {

    private static final String FILTER_NAMES = "filter_names";
    private static final String FILTER_SCOPE = "filter_scope";
    private static final String FILTER_TAGS = "filter_tags";
    private static final String FILTER_MIN_AGE = "filter_min_age";
    private static final String FILTER_MAX_AGE = "filter_max_age";
    private static final String FILTER_MIN_DATE = "filter_min_date";
    private static final String FILTER_MAX_DATE = "filter_max_date";
    private static final String FILTER_REWARD_SUPPLIER_IDS = "filter_reward_supplier_ids";
    private static final String FILTER_FACE_VALUE_TYPES = "filter_face_value_types";
    private static final String FILTER_STATES = "filter_states";
    private static final String FILTER_EXPRESSIONS = "filter_expressions";
    private static final String FILTER_EXPRESSION = "filter_expression";
    private static final String SUM_OF_FACE_VALUE_MAX = "sum_of_face_value_max";
    private static final String SUM_OF_FACE_VALUE_MIN = "sum_of_face_value_min";
    private static final String COUNT_MAX = "count_max";
    private static final String COUNT_MIN = "count_min";
    private static final String COUNT_MATCHES = "count_matches";
    private static final String TAX_YEAR_START = "tax_year_start";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope> filterScope;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterTags;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMinAge;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMaxAge;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMinDate;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMaxDate;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> filterRewardSupplierIds;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>> filterFaceValueTypes;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> filterStates;
    private final Set<String> filterExpressions;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>> filterExpression;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfFaceValueMax;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfFaceValueMin;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>> taxYearStart;

    public CampaignControllerTriggerHasPriorRewardResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(FILTER_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames,
        @JsonProperty(FILTER_SCOPE) BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope> filterScope,
        @JsonProperty(FILTER_TAGS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterTags,
        @JsonProperty(FILTER_MIN_AGE) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> filterMaxAge,
        @JsonProperty(FILTER_MIN_DATE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<ZonedDateTime>> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<ZonedDateTime>> filterMaxDate,
        @JsonProperty(FILTER_REWARD_SUPPLIER_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<Id<?>>> filterRewardSupplierIds,
        @JsonProperty(FILTER_FACE_VALUE_TYPES) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<FaceValueType>> filterFaceValueTypes,
        @JsonProperty(FILTER_STATES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> filterStates,
        @JsonProperty(FILTER_EXPRESSIONS) Set<String> filterExpressions,
        @JsonProperty(FILTER_EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>> filterExpression,
        @JsonProperty(SUM_OF_FACE_VALUE_MAX) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> sumOfFaceValueMax,
        @JsonProperty(SUM_OF_FACE_VALUE_MIN) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<BigDecimal>> sumOfFaceValueMin,
        @JsonProperty(COUNT_MAX) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax,
        @JsonProperty(COUNT_MIN) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin,
        @JsonProperty(COUNT_MATCHES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches,
        @JsonProperty(TAX_YEAR_START) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>> taxYearStart,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.HAS_PRIOR_REWARD, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.filterNames = filterNames;
        this.filterScope = filterScope;
        this.filterTags = filterTags;
        this.filterMinAge = filterMinAge;
        this.filterMaxAge = filterMaxAge;
        this.filterMinDate = filterMinDate;
        this.filterMaxDate = filterMaxDate;
        this.filterRewardSupplierIds = filterRewardSupplierIds;
        this.filterFaceValueTypes = filterFaceValueTypes;
        this.filterStates = filterStates;
        this.filterExpressions = immutableCopyOrEmptySet(filterExpressions);
        this.filterExpression = filterExpression;
        this.sumOfFaceValueMax = sumOfFaceValueMax;
        this.sumOfFaceValueMin = sumOfFaceValueMin;
        this.countMax = countMax;
        this.countMin = countMin;
        this.countMatches = countMatches;
        this.taxYearStart = taxYearStart;
    }

    @JsonProperty(FILTER_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope> getFilterScope() {
        return filterScope;
    }

    @JsonProperty(FILTER_TAGS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getFilterTags() {
        return filterTags;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(FILTER_REWARD_SUPPLIER_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getFilterRewardSupplierIds() {
        return filterRewardSupplierIds;
    }

    @JsonProperty(FILTER_FACE_VALUE_TYPES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>> getFilterFaceValueTypes() {
        return filterFaceValueTypes;
    }

    @JsonProperty(FILTER_STATES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> getFilterStates() {
        return filterStates;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Set<String> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(FILTER_EXPRESSION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>>
        getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MAX)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getSumOfFaceValueMax() {
        return sumOfFaceValueMax;
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MIN)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> getSumOfFaceValueMin() {
        return sumOfFaceValueMin;
    }

    @JsonProperty(COUNT_MAX)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> getCountMax() {
        return countMax;
    }

    @JsonProperty(COUNT_MIN)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> getCountMin() {
        return countMin;
    }

    @JsonProperty(COUNT_MATCHES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(TAX_YEAR_START)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>> getTaxYearStart() {
        return taxYearStart;
    }

    private <T> Set<T> immutableCopyOrEmptySet(Set<? extends T> collection) {
        return collection != null ? ImmutableSet.copyOf(collection) : Collections.emptySet();
    }
}
