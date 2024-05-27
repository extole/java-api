package com.extole.api.model.campaign;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface ControllerTrigger {

    String getId();

    String getType();

    BuildtimeEvaluatable<ControllerBuildtimeContext, String> getPhase();

    BuildtimeEvaluatable<ControllerBuildtimeContext, String> getName();

    BuildtimeEvaluatable<ControllerBuildtimeContext, String> getDescription();

    BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled();

    String getCreatedDate();

    BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getNegated();

}
