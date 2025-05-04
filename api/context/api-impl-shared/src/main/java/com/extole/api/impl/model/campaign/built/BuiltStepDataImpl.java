package com.extole.api.impl.model.campaign.built;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.model.campaign.built.BuiltStepData;
import com.extole.api.step.data.StepDataContext;
import com.extole.evaluateable.Evaluatables;
import com.extole.evaluateable.RuntimeEvaluatable;

final class BuiltStepDataImpl implements BuiltStepData {
    private final com.extole.model.entity.campaign.built.BuiltStepData builtStepData;

    BuiltStepDataImpl(com.extole.model.entity.campaign.built.BuiltStepData builtStepData) {
        this.builtStepData = builtStepData;
    }

    @Override
    public String getId() {
        return builtStepData.getId().getValue();
    }

    @Override
    public String getName() {
        return builtStepData.getName();
    }

    @Override
    public RuntimeEvaluatable<StepDataContext, Object> getValue() {
        return Evaluatables.remapClassToClass(builtStepData.getValue(), new TypeReference<>() {});
    }

    @Override
    public String getScope() {
        return builtStepData.getScope().name();
    }

    @Override
    public boolean isDimension() {
        return builtStepData.isDimension();
    }

    @Override
    public String[] getPersistTypes() {
        return builtStepData.getPersistTypes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public RuntimeEvaluatable<StepDataContext, Object> getDefaultValue() {
        return Evaluatables.remapClassToClass(builtStepData.getDefaultValue(), new TypeReference<>() {});
    }

    @Override
    public String getKeyType() {
        return builtStepData.getKeyType().name();
    }

    @Override
    public boolean getEnabled() {
        return builtStepData.getEnabled().booleanValue();
    }
}
