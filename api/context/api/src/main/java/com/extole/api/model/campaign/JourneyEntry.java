package com.extole.api.model.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface JourneyEntry extends Step {

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getJourneyName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> getPriority();
}
