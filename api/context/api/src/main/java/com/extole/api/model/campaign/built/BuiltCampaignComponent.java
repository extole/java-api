package com.extole.api.model.campaign.built;

import java.util.List;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.ComponentOrigin;

public interface BuiltCampaignComponent {

    String getId();

    List<String> getTypes();

    @Nullable
    ComponentOrigin getOrigin();

    String getName();

    @Nullable
    String getDescription();

    String[] getTags();

    BuiltVariable[] getVariables();

    BuiltCampaignComponentAsset[] getAssets();

    String getCreatedDate();

    String getUpdatedDate();

}
