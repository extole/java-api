package com.extole.api.model.campaign;

import java.math.BigDecimal;
import java.util.Set;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface FlowStep {

    String getId();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getFlowPath();

    BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> getSequence();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getStepName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconType();

    FlowStepMetric[] getMetrics();

    FlowStepApp[] getApps();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getTags();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconColor();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getDescription();

    FlowStepWords getWords();

    String getCreatedDate();

    String getUpdatedDate();

}
