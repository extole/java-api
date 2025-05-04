package com.extole.client.rest.campaign;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariantSelectionContext;
import com.extole.client.rest.campaign.component.CampaignComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.controller.response.CampaignStepResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepResponse;
import com.extole.client.rest.campaign.incentive.IncentiveResponse;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class CampaignResponse {

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
    private final Integer parentVersion;
    private final String incentiveId;
    private final ZonedDateTime updatedDate;
    private final ZonedDateTime publishedDate;
    private final ZonedDateTime startDate;
    private final ZonedDateTime stopDate;
    private final ZonedDateTime pauseDate;
    private final ZonedDateTime endDate;
    private final boolean isPublished;
    private final CampaignState state;
    private final IncentiveResponse incentive;
    private final List<CampaignComponentResponse> components;
    private final List<CampaignStepResponse> steps;
    private final List<CampaignLabelResponse> labels;
    private final String programLabel;
    private final String programType;
    private final String themeName;
    private final List<CampaignFlowStepResponse> flowSteps;
    private final Set<CampaignLockType> campaignLocks;
    private final Set<String> tags;
    private final CampaignType campaignType;

    private final BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<VariantSelectionContext, String>> variantSelector;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> variants;

    @JsonCreator
    public CampaignResponse(
        @JsonProperty(CAMPAIGN_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(INCENTIVE_ID) String incentiveId,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @Nullable @JsonProperty(PUBLISHED_DATE) ZonedDateTime publishedDate,
        @Nullable @JsonProperty(START_DATE) ZonedDateTime startDate,
        @Nullable @JsonProperty(STOP_DATE) ZonedDateTime stopDate,
        @Nullable @JsonProperty(PAUSE_DATE) ZonedDateTime pauseDate,
        @Nullable @JsonProperty(END_DATE) ZonedDateTime endDate,
        @JsonProperty(IS_PUBLISHED) boolean isPublished,
        @JsonProperty(STATE) CampaignState state,
        @JsonProperty(COMPONENTS) List<CampaignComponentResponse> components,
        @JsonProperty(STEPS) List<CampaignStepResponse> steps,
        @JsonProperty(LABELS) List<CampaignLabelResponse> labels,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(INCENTIVE) IncentiveResponse incentive,
        @JsonProperty(VERSION) Integer version,
        @Nullable @JsonProperty(PARENT_VERSION) Integer parentVersion,
        @JsonProperty(PROGRAM_TYPE) String programType,
        @JsonProperty(THEME_NAME) String themeName,
        @JsonProperty(FLOW_STEPS) List<CampaignFlowStepResponse> flowSteps,
        @JsonProperty(CAMPAIGN_LOCKS) Set<CampaignLockType> campaignLocks,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(VARIANT_SELECTOR) BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<VariantSelectionContext, String>> variantSelector,
        @JsonProperty(VARIANTS) BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> variants,
        @JsonProperty(CAMPAIGN_TYPE) CampaignType campaignType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.components = components != null ? ImmutableList.copyOf(components) : Collections.emptyList();
        this.version = version;
        this.parentVersion = parentVersion;
        this.incentiveId = incentiveId;
        this.updatedDate = updatedDate;
        this.publishedDate = publishedDate;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.pauseDate = pauseDate;
        this.endDate = endDate;
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
        this.variants = variants;
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
        return Optional.ofNullable(parentVersion);
    }

    @JsonProperty(STEPS)
    public List<CampaignStepResponse> getSteps() {
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
        return Optional.ofNullable(publishedDate);
    }

    @JsonProperty(START_DATE)
    public Optional<ZonedDateTime> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    @JsonProperty(STOP_DATE)
    public Optional<ZonedDateTime> getStopDate() {
        return Optional.ofNullable(stopDate);
    }

    @JsonProperty(PAUSE_DATE)
    public Optional<ZonedDateTime> getPauseDate() {
        return Optional.ofNullable(pauseDate);
    }

    @JsonProperty(END_DATE)
    public Optional<ZonedDateTime> getEndDate() {
        return Optional.ofNullable(endDate);
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
    public List<CampaignComponentResponse> getComponents() {
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
    public List<CampaignFlowStepResponse> getFlowSteps() {
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
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<VariantSelectionContext, String>>
        getVariantSelector() {
        return variantSelector;
    }

    @JsonProperty(VARIANTS)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> getVariants() {
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
