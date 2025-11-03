package com.extole.client.rest.campaign.summary;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.CampaignLockType;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.common.lang.ToString;

public class CampaignSummaryResponse {

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
    private static final String LABELS = "labels";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String PROGRAM_TYPE = "program_type";
    private static final String THEME_NAME = "theme_name";
    private static final String TAGS = "tags";
    private static final String CAMPAIGN_LOCKS = "campaign_locks";
    private static final String VARIANTS = "variants";
    private static final String CAMPAIGN_TYPE = "campaign_type";
    private static final String ROOT_COMPONENT_TYPE = "root_component_type";

    private final String id;
    private final String name;
    private final String description;
    private final ZonedDateTime updatedDate;
    private final Optional<ZonedDateTime> publishedDate;
    private final Optional<ZonedDateTime> startDate;
    private final Optional<ZonedDateTime> stopDate;
    private final Optional<ZonedDateTime> pauseDate;
    private final Optional<ZonedDateTime> endDate;
    private final boolean isPublished;
    private final CampaignState state;
    private final List<CampaignLabelResponse> labelResponses;
    private final String programLabel;
    private final String programType;
    private final String themeName;
    private final Set<String> tags;
    private final List<CampaignLockType> campaignLocks;
    private final List<String> variants;
    private final CampaignType campaignType;
    private final Optional<String> rootComponentType;

    @JsonCreator
    public CampaignSummaryResponse(@JsonProperty(CAMPAIGN_ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(PUBLISHED_DATE) Optional<ZonedDateTime> publishedDate,
        @JsonProperty(START_DATE) Optional<ZonedDateTime> startDate,
        @JsonProperty(STOP_DATE) Optional<ZonedDateTime> stopDate,
        @JsonProperty(PAUSE_DATE) Optional<ZonedDateTime> pauseDate,
        @JsonProperty(END_DATE) Optional<ZonedDateTime> endDate,
        @JsonProperty(IS_PUBLISHED) boolean isPublished,
        @JsonProperty(STATE) CampaignState state,
        @JsonProperty(LABELS) List<CampaignLabelResponse> labelResponses,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(PROGRAM_TYPE) String programType,
        @JsonProperty(THEME_NAME) String themeName,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(CAMPAIGN_LOCKS) List<CampaignLockType> campaignLocks,
        @JsonProperty(VARIANTS) List<String> variants,
        @JsonProperty(CAMPAIGN_TYPE) CampaignType campaignType,
        @JsonProperty(ROOT_COMPONENT_TYPE) Optional<String> rootComponentType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.updatedDate = updatedDate;
        this.publishedDate = publishedDate;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.pauseDate = pauseDate;
        this.endDate = endDate;
        this.isPublished = isPublished;
        this.state = state;
        this.labelResponses = labelResponses != null ? labelResponses : Collections.emptyList();
        this.programLabel = programLabel;
        this.programType = programType;
        this.themeName = themeName;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : Collections.emptySet();
        this.campaignLocks = campaignLocks;
        this.variants = variants != null ? ImmutableList.copyOf(variants) : Collections.emptyList();
        this.campaignType = campaignType;
        this.rootComponentType = rootComponentType;
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

    @JsonProperty(LABELS)
    public List<CampaignLabelResponse> getLabels() {
        return labelResponses;
    }

    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(PROGRAM_TYPE)
    public String getProgramType() {
        return programType;
    }

    @JsonProperty(THEME_NAME)
    public String getThemeName() {
        return themeName;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(CAMPAIGN_LOCKS)
    public List<CampaignLockType> getCampaignLocks() {
        return campaignLocks;
    }

    @JsonProperty(VARIANTS)
    public List<String> getVariants() {
        return variants;
    }

    @JsonProperty(CAMPAIGN_TYPE)
    public CampaignType getCampaignType() {
        return campaignType;
    }

    @JsonProperty(ROOT_COMPONENT_TYPE)
    public Optional<String> getRootComponentType() {
        return rootComponentType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
