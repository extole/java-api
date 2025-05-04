package com.extole.api.impl.model.campaign;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.api.model.campaign.Variable;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.evaluateable.RuntimeEvaluatable;

final class VariableImpl implements Variable {
    private final com.extole.model.entity.campaign.Variable variable;

    VariableImpl(com.extole.model.entity.campaign.Variable variable) {
        this.variable = variable;
    }

    @Override
    public String getName() {
        return variable.getName();
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return variable.getDisplayName().orElse(null);
    }

    @Override
    public String getType() {
        return variable.getType().name();
    }

    @Override
    public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Object>>>
        getValues() {
        return variable.getValues().entrySet()
            .stream().collect(Collectors.toUnmodifiableMap(e -> e.getKey(), e -> Evaluatables.remapClassToClass(
                e.getValue(),
                new TypeReference<BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Object>>>() {})));
    }

    @Override
    public String getSource() {
        return variable.getSource().name();
    }

    @Override
    public BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(variable.getDescription(), new TypeReference<>() {});
    }

    @Override
    public String[] getTags() {
        return variable.getTags().toArray(String[]::new);
    }
}
