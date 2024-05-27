package com.extole.api.model.campaign;

import java.util.List;
import java.util.Set;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface Controller extends Step {

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getScope();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getEnabledOnStates();

    ControllerAction[] getActions();

    BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> getSelectors();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getAliases();

    StepData[] getData();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getJourneyNames();

}
