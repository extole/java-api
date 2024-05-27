package com.extole.api.model.campaign;

import java.util.Map;

import javax.annotation.Nullable;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public interface Variable {

    String getName();

    @Nullable
    String getDisplayName();

    String getType();

    Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Object>>>
        getValues();

    String getSource();

    BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, String> getDescription();

    String[] getTags();

}
