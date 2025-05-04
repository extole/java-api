package com.extole.client.rest.campaign.built;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.VariantSelectionContext;
import com.extole.client.rest.campaign.CampaignLockType;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentResponse;
import com.extole.client.rest.campaign.built.controller.BuiltCampaignStepResponse;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepResponse;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.incentive.IncentiveResponse;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.RuntimeEvaluatable;

public class BuiltCampaignResponse {

    private static final String INCENTIVE_ID = "incentive_id";
    private static final String INCENTIVE = "incentive";
    private static final String STEPS = "steps";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String UPDATED_DATE = "updated_date";
    private static final String PUBLISHED_DATE = "last_published_date";
    private static final String START_DATE = "start_date";
    private static final String STOP_DATE = "stop_date";
    private static final String PAUSE_DATE = "pause_date";
    private static final String END_DATE = "end_date";
    private static final String IS_PUBLISHED = "is_published";
    private static final String STATE = "state";
    private static final String COMPONENTS = "components";
    private static final String LABELS = "labels";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String VERSION = "version";
    private static final String PARENT_VERSION = "parent_version";
    private static final String PROGRAM_TYPE = "program_type";
    private static final String THEME_NAME = "theme_name";
    private static final String FLOW_STEPS = "flow_steps";
    private static final String CAMPAIGN_LOCKS = "campaign_locks";
    private static final String TAGS = "tags";
    private static final String VARIANT_SELECTOR = "variant_selector";
    private static final String VARIANTS = "variants";
    private static final String CAMPAIGN_TYPE = "campaign_type";

    private final String id;
    private final String name;
    private final String description;
    private final Integer version;
    private final Optional<Integer> parentVersion;
    private final String incentiveId;
    private final ZonedDateTime updatedDate;
    private final Optional<ZonedDateTime> publishedDate;
    private final Optional<ZonedDateTime> startDate;
    private final Optional<ZonedDateTime> stopDate;
    private final Optional<ZonedDateTime> pauseDate;
    private final Optional<ZonedDateTime> endDate;
    private final boolean isPublished;
    private final CampaignState state;
    private final IncentiveResponse incentive;
    private final List<BuiltCampaignComponentResponse> components;
    private final List<BuiltCampaignStepResponse> steps;
    private final List<CampaignLabelResponse> labels;
    private final String programLabel;
    private final String programType;
    private final String themeName;
    private final List<BuiltCampaignFlowStepResponse> flowSteps;
    private final Set<CampaignLockType> campaignLocks;
    private final Set<String> tags;

    private final RuntimeEvaluatable<VariantSelectionContext, String> variantSelector;
    private final List<String> variants;
    private final CampaignType campaignType;

    @JsonCreator
    public BuiltCampaignResponse(
        @JsonProperty(CAMPAIGN_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(INCENTIVE_ID) String incentiveId,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(PUBLISHED_DATE) Optional<ZonedDateTime> publishedDate,
        @JsonProperty(START_DATE) Optional<ZonedDateTime> startDate,
        @JsonProperty(STOP_DATE) Optional<ZonedDateTime> stopDate,
        @JsonProperty(PAUSE_DATE) Optional<ZonedDateTime> pauseDate,
        @JsonProperty(END_DATE) Optional<ZonedDateTime> endDate,
        @JsonProperty(IS_PUBLISHED) boolean isPublished,
        @JsonProperty(STATE) CampaignState state,
        @JsonProperty(COMPONENTS) List<BuiltCampaignComponentResponse> components,
        @JsonProperty(STEPS) List<BuiltCampaignStepResponse> steps,
        @JsonProperty(LABELS) List<CampaignLabelResponse> labels,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(INCENTIVE) IncentiveResponse incentive,
        @JsonProperty(VERSION) Integer version,
        @JsonProperty(PARENT_VERSION) Optional<Integer> parentVersion,
        @JsonProperty(PROGRAM_TYPE) String programType,
        @JsonProperty(THEME_NAME) String themeName,
        @JsonProperty(FLOW_STEPS) List<BuiltCampaignFlowStepResponse> flowSteps,
        @JsonProperty(CAMPAIGN_LOCKS) Set<CampaignLockType> campaignLocks,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(VARIANT_SELECTOR) RuntimeEvaluatable<VariantSelectionContext, String> variantSelector,
        @JsonProperty(VARIANTS) List<String> variants,
        @JsonProperty(CAMPAIGN_TYPE) CampaignType campaignType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.pauseDate = pauseDate;
        this.endDate = endDate;
        this.components = components != null ? ImmutableList.copyOf(components) : Collections.emptyList();
        this.version = version;
        this.parentVersion = parentVersion;
        this.incentiveId = incentiveId;
        this.updatedDate = updatedDate;
        this.publishedDate = publishedDate;
        this.isPublished = isPublished;
        this.state = state;
        this.steps = steps != null ? ImmutableList.copyOf(steps) : Collections.emptyList();
        this.labels = labels != null ? ImmutableList.copyOf(labels) : Collections.emptyList();
        this.programLabel = programLabel;
        this.incentive = incentive;
        this.programType = programType;
        this.themeName = themeName;
        this.flowSteps = flowSteps != null ? ImmutableList.copyOf(flowSteps) : Collections.emptyList();
        this.campaignLocks = campaignLocks != null ? ImmutableSet.copyOf(campaignLocks) : Collections.emptySet();
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : Collections.emptySet();
        this.variantSelector = variantSelector;
        this.variants = variants != null ? ImmutableList.copyOf(variants) : Collections.emptyList();
        this.campaignType = campaignType;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(VERSION)
    public Integer getVersion() {
        return version;
    }

    @JsonProperty(PARENT_VERSION)
    public Optional<Integer> getParentVersion() {
        return parentVersion;
    }

    @JsonProperty(STEPS)
    public List<BuiltCampaignStepResponse> getSteps() {
        return steps;
    }

    @JsonProperty(INCENTIVE_ID)
    public String getIncentiveId() {
        return incentiveId;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getLastUpdateDate() {
        return updatedDate;
    }

    @JsonProperty(PUBLISHED_DATE)
    public Optional<ZonedDateTime> getPublishDate() {
        return publishedDate;
    }

    @JsonProperty(START_DATE)
    public Optional<ZonedDateTime> getStartDate() {
        return startDate;
    }

    @JsonProperty(STOP_DATE)
    public Optional<ZonedDateTime> getStopDate() {
        return stopDate;
    }

    @JsonProperty(PAUSE_DATE)
    public Optional<ZonedDateTime> getPauseDate() {
        return pauseDate;
    }

    @JsonProperty(END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return endDate;
    }

    @JsonProperty(IS_PUBLISHED)
    public boolean isPublished() {
        return isPublished;
    }

    @JsonProperty(STATE)
    public CampaignState getState() {
        return state;
    }

    @JsonProperty(COMPONENTS)
    public List<BuiltCampaignComponentResponse> getComponents() {
        return components;
    }

    @JsonProperty(LABELS)
    public List<CampaignLabelResponse> getLabels() {
        return labels;
    }

    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(INCENTIVE)
    public IncentiveResponse getIncentive() {
        return incentive;
    }

    @JsonProperty(PROGRAM_TYPE)
    public String getProgramType() {
        return programType;
    }

    @JsonProperty(THEME_NAME)
    public Optional<String> getThemeName() {
        return Optional.ofNullable(themeName);
    }

    @JsonProperty(FLOW_STEPS)
    public List<BuiltCampaignFlowStepResponse> getFlowSteps() {
        return flowSteps;
    }

    @JsonProperty(CAMPAIGN_LOCKS)
    public Set<CampaignLockType> getCampaignLocks() {
        return campaignLocks;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(VARIANT_SELECTOR)
    public RuntimeEvaluatable<VariantSelectionContext, String> getVariantSelector() {
        return variantSelector;
    }

    @JsonProperty(VARIANTS)
    public List<String> getVariants() {
        return variants;
    }

    @JsonProperty(CAMPAIGN_TYPE)
    public CampaignType getCampaignType() {
        return campaignType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
