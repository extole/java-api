package com.extole.common.variable.evaluator.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import com.extole.api.impl.campaign.PartnerEnumListRuntimeFlatSetting;
import com.extole.api.impl.campaign.VariableKey;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.entity.campaign.SettingType;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;

public class PartnerEnumListRuntimeFlatSettingImpl implements PartnerEnumListRuntimeFlatSetting {

    private static final SettingType TYPE = SettingType.PARTNER_ENUM_LIST;

    private final RuntimeFlatSettingImpl runtimeFlatVariable;
    private final List<PartnerEnumListVariableOption> options;

    public PartnerEnumListRuntimeFlatSettingImpl(String name,
        String variant,
        RuntimeEvaluatable<Object, Optional<Object>> value,
        Id<RunningComponent> componentId,
        List<PartnerEnumListVariableOption> options,
        RunningCampaign runningCampaign) {
        this.runtimeFlatVariable = new RuntimeFlatSettingImpl(TYPE, name, variant, value, componentId, runningCampaign);
        this.options = ImmutableList.copyOf(options);
    }

    @Override
    public SettingType getType() {
        return TYPE;
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
    public List<PartnerEnumListVariableOption> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        PartnerEnumListRuntimeFlatSettingImpl that = (PartnerEnumListRuntimeFlatSettingImpl) other;
        return Objects.equals(this.getVariableKey(), that.getVariableKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getVariableKey());
    }
}
