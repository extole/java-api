package com.extole.api.model.campaign;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public interface ControllerAction {

    String getId();

    String getType();

    String getQuality();

    BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled();

    String getCreatedDate();
}
