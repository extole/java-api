package com.extole.api.model.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.model.EventEntity;

@Schema
public interface Campaign extends EventEntity {
    String getId();

    String getName();

    String getState();

    Step[] getSteps();

    Controller[] getControllers();

    JourneyEntry[] getJourneyEntries();

    Label[] getLabels();

    Label getProgramLabel();

    int getVersion();

    @Nullable
    Integer getParentVersion();

    boolean isDraft();

    FlowStep[] getFlowSteps();

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

    RewardRule[] getRewardRules();

    QualityRule[] getQualityRules();

    TransitionRule[] getTransitionRules();

    Component[] getComponents();

    String getProgramType();

    @Nullable
    String getThemeName();

    String getEditorId();

    String getEditorType();

    String[] getLocks();

    String[] getTags();

}
