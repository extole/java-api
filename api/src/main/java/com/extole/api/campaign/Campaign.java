package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Campaign {

    String getCampaignId();

    String getCampaignName();

    String getProgramLabel();

    String getProgramType();

    @Nullable
    String getThemeName();

    String getDescription();

    String getCurrentState();

    @Nullable
    String getArchivedDate();

    String[] getTags();
}
