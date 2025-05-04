package com.extole.api.model.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface FlowStepAppType {

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName();

}
