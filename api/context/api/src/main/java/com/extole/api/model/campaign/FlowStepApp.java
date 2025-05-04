package com.extole.api.model.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface FlowStepApp {

    String getId();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getDescription();

    FlowStepAppType getType();
}
