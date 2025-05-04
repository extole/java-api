package com.extole.api.impl.model.campaign.built;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.campaign.built.BuiltVariable;
import com.extole.evaluateable.Evaluatables;
import com.extole.evaluateable.RuntimeEvaluatable;

public final class BuiltVariableImpl implements BuiltVariable {
    private final com.extole.model.entity.campaign.built.BuiltVariable variable;

    public BuiltVariableImpl(com.extole.model.entity.campaign.built.BuiltVariable variable) {
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
    public Map<String, RuntimeEvaluatable<Object, Object>> getSourcedValues() {
        return variable.getSourcedValues().entrySet()
            .stream().collect(Collectors.toUnmodifiableMap(e -> e.getKey(), e -> Evaluatables.remapClassToClass(
                e.getValue(), new TypeReference<>() {})));
    }

    @Override
    public String getSource() {
        return variable.getSource().name();
    }

    @Nullable
    @Override
    public String getDescription() {
        return variable.getDescription().orElse(null);
    }

    @Override
    public String[] getTags() {
        return variable.getTags().toArray(String[]::new);
    }
}
