package com.extole.client.rest.campaign.built.controller.trigger;

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

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.RewardFilterScope;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.RewardState;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerHasPriorRewardResponse extends BuiltCampaignControllerTriggerResponse {

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
    private static final String SUM_OF_FACE_VALUE_MAX = "sum_of_face_value_max";
    private static final String SUM_OF_FACE_VALUE_MIN = "sum_of_face_value_min";
    private static final String COUNT_MAX = "count_max";
    private static final String COUNT_MIN = "count_min";
    private static final String COUNT_MATCHES = "count_matches";
    private static final String TAX_YEAR_START = "tax_year_start";

    private final Set<String> filterNames;
    private final RewardFilterScope filterScope;
    private final Set<String> filterTags;
    private final Optional<Duration> filterMinAge;
    private final Optional<Duration> filterMaxAge;
    private final Optional<ZonedDateTime> filterMinDate;
    private final Optional<ZonedDateTime> filterMaxDate;
    private final Set<String> filterRewardSupplierIds;
    private final Set<FaceValueType> filterFaceValueTypes;
    private final Set<RewardState> filterStates;
    private final Set<String> filterExpressions;
    private final BigDecimal sumOfFaceValueMax;
    private final BigDecimal sumOfFaceValueMin;
    private final Integer countMax;
    private final Integer countMin;
    private final Set<Integer> countMatches;
    private final Optional<MonthDay> taxYearStart;

    public BuiltCampaignControllerTriggerHasPriorRewardResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(FILTER_NAMES) Set<String> filterNames,
        @JsonProperty(FILTER_SCOPE) RewardFilterScope filterScope,
        @JsonProperty(FILTER_TAGS) Set<String> filterTags,
        @JsonProperty(FILTER_MIN_AGE) Optional<Duration> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) Optional<Duration> filterMaxAge,
        @JsonProperty(FILTER_MIN_DATE) Optional<ZonedDateTime> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) Optional<ZonedDateTime> filterMaxDate,
        @JsonProperty(FILTER_REWARD_SUPPLIER_IDS) Set<String> filterRewardSupplierIds,
        @JsonProperty(FILTER_FACE_VALUE_TYPES) Set<FaceValueType> filterFaceValueTypes,
        @JsonProperty(FILTER_STATES) Set<RewardState> filterStates,
        @JsonProperty(FILTER_EXPRESSIONS) Set<String> filterExpressions,
        @JsonProperty(SUM_OF_FACE_VALUE_MAX) BigDecimal sumOfFaceValueMax,
        @JsonProperty(SUM_OF_FACE_VALUE_MIN) BigDecimal sumOfFaceValueMin,
        @JsonProperty(COUNT_MAX) Integer countMax,
        @JsonProperty(COUNT_MIN) Integer countMin,
        @JsonProperty(COUNT_MATCHES) Set<Integer> countMatches,
        @JsonProperty(TAX_YEAR_START) Optional<MonthDay> taxYearStart,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.HAS_PRIOR_REWARD, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.filterNames = immutableCopyOrEmptySet(filterNames);
        this.filterScope = filterScope;
        this.filterTags = immutableCopyOrEmptySet(filterTags);
        this.filterMinAge = filterMinAge;
        this.filterMaxAge = filterMaxAge;
        this.filterMinDate = filterMinDate;
        this.filterMaxDate = filterMaxDate;
        this.filterRewardSupplierIds = immutableCopyOrEmptySet(filterRewardSupplierIds);
        this.filterFaceValueTypes = immutableCopyOrEmptySet(filterFaceValueTypes);
        this.filterStates = immutableCopyOrEmptySet(filterStates);
        this.filterExpressions = immutableCopyOrEmptySet(filterExpressions);
        this.sumOfFaceValueMax = sumOfFaceValueMax;
        this.sumOfFaceValueMin = sumOfFaceValueMin;
        this.countMax = countMax;
        this.countMin = countMin;
        this.countMatches = immutableCopyOrEmptySet(countMatches);
        this.taxYearStart = taxYearStart;
    }

    @JsonProperty(FILTER_NAMES)
    public Set<String> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public RewardFilterScope getFilterScope() {
        return filterScope;
    }

    @JsonProperty(FILTER_TAGS)
    public Set<String> getFilterTags() {
        return filterTags;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public Optional<Duration> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public Optional<Duration> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public Optional<ZonedDateTime> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public Optional<ZonedDateTime> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(FILTER_REWARD_SUPPLIER_IDS)
    public Set<String> getFilterRewardSupplierIds() {
        return filterRewardSupplierIds;
    }

    @JsonProperty(FILTER_FACE_VALUE_TYPES)
    public Set<FaceValueType> getFilterFaceValueTypes() {
        return filterFaceValueTypes;
    }

    @JsonProperty(FILTER_STATES)
    public Set<RewardState> getFilterStates() {
        return filterStates;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Set<String> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MAX)
    public Optional<BigDecimal> getSumOfFaceValueMax() {
        return Optional.ofNullable(sumOfFaceValueMax);
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MIN)
    public Optional<BigDecimal> getSumOfFaceValueMin() {
        return Optional.ofNullable(sumOfFaceValueMin);
    }

    @JsonProperty(COUNT_MAX)
    public Optional<Integer> getCountMax() {
        return Optional.ofNullable(countMax);
    }

    @JsonProperty(COUNT_MIN)
    public Optional<Integer> getCountMin() {
        return Optional.ofNullable(countMin);
    }

    @JsonProperty(COUNT_MATCHES)
    public Set<Integer> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(TAX_YEAR_START)
    public Optional<MonthDay> getTaxYearStart() {
        return taxYearStart;
    }

    private <T> Set<T> immutableCopyOrEmptySet(Set<? extends T> collection) {
        return collection != null ? ImmutableSet.copyOf(collection) : Collections.emptySet();
    }
}
