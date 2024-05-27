package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface CampaignSummary {

    String getCampaignId();

    String getCampaignName();

    String getProgramLabel();

    String getProgramType();

    @Nullable
    String getThemeName();

    String getDescription();

    String getCurrentState();

    @Nullable
    String getFirstLaunchDate();

    @Nullable
    String getLastStoppedDate();

    @Nullable
    String getLastPausedDate();

    @Nullable
    String getLastArchivedDate();

    @Nullable
    String getLastEndedDate();

    String[] getTags();
}
