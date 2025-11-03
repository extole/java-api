package com.extole.common.variable.evaluator.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.extole.api.impl.campaign.SocketRuntimeFlatSetting;
import com.extole.api.impl.campaign.VariableKey;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Component;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;

public class SocketRuntimeFlatSettingImpl implements SocketRuntimeFlatSetting {

    private final RuntimeFlatSettingImpl runtimeFlatVariable;
    private final List<Id<Component>> installedIntoComponents;
    private final SettingType settingType;

    public SocketRuntimeFlatSettingImpl(
        String name,
        SettingType settingType,
        Id<RunningComponent> componentId,
        List<Id<Component>> installedIntoComponents,
        RunningCampaign runningCampaign) {
        if (settingType != SettingType.SOCKET
            && settingType != SettingType.MULTI_SOCKET) {
            throw new IllegalArgumentException("Invalid setting type for SocketRuntimeFlatSetting: " + settingType);
        }
        this.settingType = settingType;
        this.installedIntoComponents = installedIntoComponents;
        this.runtimeFlatVariable =
            new RuntimeFlatSettingImpl(settingType, name, Variable.DEFAULT_VALUE_KEY,
                Provided.optionalOf(installedIntoComponents), componentId, runningCampaign);
    }

    @Override
    public SettingType getType() {
        return settingType;
    }

    @Override
    public String getName() {
        return runtimeFlatVariable.getName();
    }

    @Override
    public VariableKey getVariableKey() {
        return runtimeFlatVariable.getVariableKey();
    }

    @Override
    public RuntimeEvaluatable<Object, Optional<Object>> getValue() {
        return runtimeFlatVariable.getValue();
    }

    @Override
    public Id<RunningComponent> getComponentId() {
        return runtimeFlatVariable.getComponentId();
    }

    @Override
    public RunningCampaign getCampaign() {
        return runtimeFlatVariable.getCampaign();
    }

    @Override
    public List<Id<Component>> getInstalledIntoComponents() {
        return installedIntoComponents;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        SocketRuntimeFlatSettingImpl that = (SocketRuntimeFlatSettingImpl) other;
        return Objects.equals(this.getVariableKey(), that.getVariableKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getVariableKey());
    }

}
