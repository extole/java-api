package com.extole.api.model.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface Step {

    String getType();

    String getId();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled();

    ControllerTrigger[] getTriggers();

    String getCreatedDate();

    String getUpdatedDate();

}
