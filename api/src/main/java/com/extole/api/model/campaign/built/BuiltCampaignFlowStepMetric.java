package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

public interface BuiltCampaignFlowStepMetric {

    String getId();

    String getName();

    @Nullable
    String getDescription();

    String getExpression();

    String[] getTags();

    String getUnit();

}
