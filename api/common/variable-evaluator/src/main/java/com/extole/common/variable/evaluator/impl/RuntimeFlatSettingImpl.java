package com.extole.common.variable.evaluator.impl;

import java.util.Objects;
import java.util.Optional;

import com.extole.api.impl.campaign.RuntimeFlatSetting;
import com.extole.api.impl.campaign.VariableKey;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;

public class RuntimeFlatSettingImpl implements RuntimeFlatSetting {

    private final SettingType type;
    private final String name;
    private final String variant;
    private final RuntimeEvaluatable<Object, Optional<Object>> value;
    private final Id<RunningComponent> componentId;
    private final RunningCampaign campaign;

    public RuntimeFlatSettingImpl(
        SettingType type,
        String name,
        String variant,
        RuntimeEvaluatable<Object, Optional<Object>> value,
        Id<RunningComponent> componentId,
        RunningCampaign campaign) {
        this.type = type;
        this.name = name;
        this.variant = variant;
        this.value = value;
        this.componentId = componentId;
        this.campaign = campaign;
    }

    @Override
    public SettingType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public VariableKey getVariableKey() {
        return VariableKey.of(name, variant);
    }

    @Override
    public RuntimeEvaluatable<Object, Optional<Object>> getValue() {
        return value;
    }

    @Override
    public Id<RunningComponent> getComponentId() {
        return componentId;
    }

    @Override
    public RunningCampaign getCampaign() {
        return campaign;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        RuntimeFlatSettingImpl that = (RuntimeFlatSettingImpl) other;
        return Objects.equals(this.getVariableKey(), that.getVariableKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getVariableKey());
    }

}
