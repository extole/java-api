package com.extole.client.rest.campaign.controller.trigger.has.prior.reward;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.MonthDay;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.trigger.has.prior.reward.HasPriorRewardTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerHasPriorRewardCreateRequest extends CampaignControllerTriggerRequest {

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

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope>> filterScope;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterTags;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> filterRewardSupplierIds;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>>> filterFaceValueTypes;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> filterStates;
    private final Omissible<Set<String>> filterExpressions;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>>> filterExpression;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMax;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMin;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>>> taxYearStart;

    @JsonCreator
    public CampaignControllerTriggerHasPriorRewardCreateRequest(
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
            BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope>> filterScope,
        @JsonProperty(FILTER_TAGS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterTags,
        @JsonProperty(FILTER_MIN_AGE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge,
        @JsonProperty(FILTER_MAX_AGE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge,
        @JsonProperty(FILTER_MIN_DATE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate,
        @JsonProperty(FILTER_MAX_DATE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate,
        @JsonProperty(FILTER_REWARD_SUPPLIER_IDS) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> filterRewardSupplierIds,
        @JsonProperty(FILTER_FACE_VALUE_TYPES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>>> filterFaceValueTypes,
        @JsonProperty(FILTER_STATES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> filterStates,
        @JsonProperty(FILTER_EXPRESSIONS) Omissible<Set<String>> filterExpressions,
        @JsonProperty(FILTER_EXPRESSION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>>> filterExpression,
        @JsonProperty(SUM_OF_FACE_VALUE_MAX) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMax,
        @JsonProperty(SUM_OF_FACE_VALUE_MIN) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMin,
        @JsonProperty(COUNT_MAX) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax,
        @JsonProperty(COUNT_MIN) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin,
        @JsonProperty(COUNT_MATCHES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches,
        @JsonProperty(TAX_YEAR_START) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>>> taxYearStart,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
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
        this.filterExpressions = filterExpressions;
        this.filterExpression = filterExpression;
        this.sumOfFaceValueMax = sumOfFaceValueMax;
        this.sumOfFaceValueMin = sumOfFaceValueMin;
        this.countMax = countMax;
        this.countMin = countMin;
        this.countMatches = countMatches;
        this.taxYearStart = taxYearStart;
    }

    @JsonProperty(FILTER_NAMES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getFilterNames() {
        return filterNames;
    }

    @JsonProperty(FILTER_SCOPE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope>> getFilterScope() {
        return filterScope;
    }

    @JsonProperty(FILTER_TAGS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getFilterTags() {
        return filterTags;
    }

    @JsonProperty(FILTER_MIN_AGE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> getFilterMinAge() {
        return filterMinAge;
    }

    @JsonProperty(FILTER_MAX_AGE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> getFilterMaxAge() {
        return filterMaxAge;
    }

    @JsonProperty(FILTER_MIN_DATE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> getFilterMinDate() {
        return filterMinDate;
    }

    @JsonProperty(FILTER_MAX_DATE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> getFilterMaxDate() {
        return filterMaxDate;
    }

    @JsonProperty(FILTER_REWARD_SUPPLIER_IDS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> getFilterRewardSupplierIds() {
        return filterRewardSupplierIds;
    }

    @JsonProperty(FILTER_FACE_VALUE_TYPES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>>> getFilterFaceValueTypes() {
        return filterFaceValueTypes;
    }

    @JsonProperty(FILTER_STATES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> getFilterStates() {
        return filterStates;
    }

    @JsonProperty(FILTER_EXPRESSIONS)
    public Omissible<Set<String>> getFilterExpressions() {
        return filterExpressions;
    }

    @JsonProperty(FILTER_EXPRESSION)
    public
        Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>>>
        getFilterExpression() {
        return filterExpression;
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MAX)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getSumOfFaceValueMax() {
        return sumOfFaceValueMax;
    }

    @JsonProperty(SUM_OF_FACE_VALUE_MIN)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> getSumOfFaceValueMin() {
        return sumOfFaceValueMin;
    }

    @JsonProperty(COUNT_MAX)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> getCountMax() {
        return countMax;
    }

    @JsonProperty(COUNT_MIN)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> getCountMin() {
        return countMin;
    }

    @JsonProperty(COUNT_MATCHES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> getCountMatches() {
        return countMatches;
    }

    @JsonProperty(TAX_YEAR_START)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>>> getTaxYearStart() {
        return taxYearStart;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope>> filterScope =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> filterTags =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMinAge =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Duration>>> filterMaxAge =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMinDate =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>>> filterMaxDate =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> filterRewardSupplierIds =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>>> filterFaceValueTypes =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> filterRewardStates =
            Omissible.omitted();
        private Omissible<Set<String>> filterExpressions = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>>> filterExpression =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMax =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>>> sumOfFaceValueMin =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMax =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>>> countMin =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>>> countMatches =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>>> taxYearStart =
            Omissible.omitted();

        private Builder() {
        }

        public Builder
            withFilterNames(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterNames) {
            this.filterNames = Omissible.of(filterNames);
            return this;
        }

        public Builder
            withFilterScope(BuildtimeEvaluatable<ControllerBuildtimeContext, RewardFilterScope> filterScope) {
            this.filterScope = Omissible.of(filterScope);
            return this;
        }

        public Builder withFilterScope(RewardFilterScope filterScope) {
            this.filterScope = Omissible.of(Provided.of(filterScope));
            return this;
        }

        public Builder withFilterTags(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> filterTags) {
            this.filterTags = Omissible.of(filterTags);
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
            withFilterMinDate(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMinDate) {
            this.filterMinDate = Omissible.of(filterMinDate);
            return this;
        }

        public Builder
            withFilterMaxDate(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<ZonedDateTime>> filterMaxDate) {
            this.filterMaxDate = Omissible.of(filterMaxDate);
            return this;
        }

        public Builder withFilterRewardSupplierIds(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> filterRewardSupplierIds) {
            this.filterRewardSupplierIds = Omissible.of(filterRewardSupplierIds);
            return this;
        }

        public Builder withFilterFaceValueTypes(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<FaceValueType>> filterFaceValueTypes) {
            this.filterFaceValueTypes = Omissible.of(filterFaceValueTypes);
            return this;
        }

        public Builder
            withFilterStates(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> filterStates) {
            this.filterRewardStates = Omissible.of(filterStates);
            return this;
        }

        public Builder withFilterStates(Set<RewardState> filterRewardStates) {
            this.filterRewardStates = Omissible.of(Provided.of(filterRewardStates));
            return this;
        }

        public Builder withFilterExpressions(Set<String> filterExpressions) {
            this.filterExpressions = Omissible.of(filterExpressions);
            return this;
        }

        public Builder withFilterExpression(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<HasPriorRewardTriggerContext, Boolean>> filterExpression) {
            this.filterExpression = Omissible.of(filterExpression);
            return this;
        }

        public Builder withSumOfFaceValueMax(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfFaceValueMax) {
            this.sumOfFaceValueMax = Omissible.of(sumOfFaceValueMax);
            return this;
        }

        public Builder withSumOfFaceValueMin(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<BigDecimal>> sumOfFaceValueMin) {
            this.sumOfFaceValueMin = Omissible.of(sumOfFaceValueMin);
            return this;
        }

        public Builder withCountMax(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMax) {
            this.countMax = Omissible.of(countMax);
            return this;
        }

        public Builder withCountMin(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Integer>> countMin) {
            this.countMin = Omissible.of(countMin);
            return this;
        }

        public Builder
            withCountMatches(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Integer>> countMatches) {
            this.countMatches = Omissible.of(countMatches);
            return this;
        }

        public Builder withTaxYearStart(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<MonthDay>> taxYearStart) {
            this.taxYearStart = Omissible.of(taxYearStart);
            return this;
        }

        @Override
        public CampaignControllerTriggerHasPriorRewardCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerHasPriorRewardCreateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                filterNames,
                filterScope,
                filterTags,
                filterMinAge,
                filterMaxAge,
                filterMinDate,
                filterMaxDate,
                filterRewardSupplierIds,
                filterFaceValueTypes,
                filterRewardStates,
                filterExpressions,
                filterExpression,
                sumOfFaceValueMax,
                sumOfFaceValueMin,
                countMax,
                countMin,
                countMatches,
                taxYearStart,
                componentIds,
                componentReferences);
        }

    }

}
