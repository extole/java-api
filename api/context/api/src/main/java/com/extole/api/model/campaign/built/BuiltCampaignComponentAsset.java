package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

public interface BuiltCampaignComponentAsset {

    String getId();

    String getName();

    String getFilename();

    String[] getTags();

    @Nullable
    String getDescription();

}
