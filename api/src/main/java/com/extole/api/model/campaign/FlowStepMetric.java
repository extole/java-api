package com.extole.api.model.campaign;

import java.util.Set;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface FlowStepMetric {

    String getId();

    BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getName();

    BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getDescription();

    BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getExpression();

    BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> getTags();

    BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getUnit();
}
