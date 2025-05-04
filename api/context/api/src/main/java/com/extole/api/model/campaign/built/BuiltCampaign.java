package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;
import com.extole.api.model.campaign.QualityRule;

@Schema
public interface BuiltCampaign extends EventEntity {
    String getId();

    String getName();

    String getState();

    BuiltCampaignStep[] getSteps();

    BuiltCampaignController[] getControllers();

    BuiltCampaignJourneyEntry[] getJourneyEntries();

    BuiltCampaignLabel[] getLabels();

    BuiltCampaignLabel getProgramLabel();

    Integer getVersion();

    Integer getBuildVersion();

    @Nullable
    Integer getParentVersion();

    boolean isDraft();

    BuiltCampaignFlowStep[] getFlowSteps();

    String[] getTags();

    @Nullable
    String getStartDate();

    @Nullable
    String getStopDate();

    String getDescription();

    String getUpdatedDate();

    @Nullable
    String getArchivedDate();

    @Nullable
    String getDeletedDate();

    @Nullable
    String getLastPublishedDate();

    BuiltRewardRule[] getRewardRules();

    QualityRule[] getQualityRules();

    BuiltTransitionRule[] getTransitionRules();

    BuiltCampaignComponent[] getComponents();

    String getProgramType();

    @Nullable
    String getThemeName();

    String getEditorId();

    String getEditorType();

    String[] getLocks();

}
