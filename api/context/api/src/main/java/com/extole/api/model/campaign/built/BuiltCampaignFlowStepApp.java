package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

public interface BuiltCampaignFlowStepApp {

    String getId();

    String getName();

    @Nullable
    String getDescription();

    String getType();

}
