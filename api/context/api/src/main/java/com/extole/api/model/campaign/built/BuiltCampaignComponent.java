package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

public interface BuiltCampaignComponent {

    String getId();

    @Nullable
    String getType();

    String getComponentVersion();

    String getName();

    @Nullable
    String getDescription();

    String[] getTags();

    BuiltVariable[] getVariables();

    BuiltCampaignComponentAsset[] getAssets();

    String getCreatedDate();

    String getUpdatedDate();

}
