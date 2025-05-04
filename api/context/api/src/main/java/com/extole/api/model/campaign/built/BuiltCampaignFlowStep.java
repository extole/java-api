package com.extole.api.model.campaign.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

public interface BuiltCampaignFlowStep {

    String getId();

    String getFlowPath();

    BigDecimal getSequence();

    String getStepName();

    String getIconType();

    BuiltCampaignFlowStepMetric[] getMetrics();

    BuiltCampaignFlowStepApp[] getApps();

    String[] getTags();

    String getName();

    String getIconColor();

    @Nullable
    String getDescription();

    BuiltCampaignFlowStepWords getWords();

    String getCreatedDate();

    String getUpdatedDate();

}
