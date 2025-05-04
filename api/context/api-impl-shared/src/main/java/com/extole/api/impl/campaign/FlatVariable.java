package com.extole.api.impl.campaign;

import java.util.Optional;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.model.entity.campaign.VariableSource;

public interface FlatVariable {

    VariableKey getVariableKey();

    BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> getValue();

    VariableSource getSource();

    ComponentWithVersion getOwner();

}
