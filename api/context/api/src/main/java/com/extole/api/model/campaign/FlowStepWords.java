package com.extole.api.model.campaign;

import com.extole.api.campaign.FlowStepBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface FlowStepWords {

    BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getSingularNounName();

    BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPluralNounName();

    BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getVerbName();

    BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getRateName();

    BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPersonCountingName();

}
