package com.extole.api.model.campaign;

import java.util.List;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.data.StepDataContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public interface StepData {

    String getId();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName();

    BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Object>> getValue();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getScope();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> isDimension();

    BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> getPersistTypes();

    BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Object>>
        getDefaultValue();

    BuildtimeEvaluatable<CampaignBuildtimeContext, String> getKeyType();

    BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled();

}
