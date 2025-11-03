package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface BuiltCampaign {

    String getCampaignId();

    String getCampaignName();

    String getProgramLabel();

    String getProgramType();

    @Nullable
    String getThemeName();

    String getDescription();

    String getCurrentState();

    @Nullable
    String getStartDate();

    @Nullable
    String getStopDate();

    @Nullable
    String getArchivedDate();

    @Nullable
    String getDeletedDate();

    @Nullable
    String getLastPublishedDate();

    String getCampaignType();

    String[] getTags();
}
