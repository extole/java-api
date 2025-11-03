package com.extole.common.variable.evaluator.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.extole.api.impl.campaign.FlatVariableMapper;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.component.RunningComponent;
import com.extole.running.service.component.RunningEnumListVariable;
import com.extole.running.service.component.RunningEnumVariable;
import com.extole.running.service.component.RunningPartnerEnumListVariable;
import com.extole.running.service.component.RunningPartnerEnumVariable;
import com.extole.running.service.component.RunningVariable;

public enum RuntimeFlatVariableMapperFactory {
    INSTANCE;

    private static final FlatVariableMapper DEFAULT_FLAT_VARIABLE_MAPPER =
        (RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
            RunningCampaign runningCampaign) -> new RuntimeFlatSettingImpl(variable.getSettingType(),
                variable.getName(), variableVariant, variable.getValue(variableVariant), componentId, runningCampaign);
    private static final Map<SettingType, FlatVariableMapper> RUNTIME_FLAT_VARIABLE_MAPPERS_BY_VARIABLE_TYPE =
        initializeRuntimeFlatVariableMappersByVariableType();

    public static RuntimeFlatVariableMapperFactory getInstance() {
        return INSTANCE;
    }

    public FlatVariableMapper create(SettingType settingType) {
        return RUNTIME_FLAT_VARIABLE_MAPPERS_BY_VARIABLE_TYPE.getOrDefault(settingType, DEFAULT_FLAT_VARIABLE_MAPPER);
    }

    private static Map<SettingType, FlatVariableMapper> initializeRuntimeFlatVariableMappersByVariableType() {
        Map<SettingType, FlatVariableMapper> mappersByType = new HashMap<>();

        mappersByType.put(SettingType.ENUM,
            (RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
                RunningCampaign runningCampaign) -> {
                RunningEnumVariable enumVariable = (RunningEnumVariable) variable;
                return new EnumFlatRuntimeSettingImpl(variable.getName(), variableVariant,
                    variable.getValue(variableVariant), componentId, enumVariable.getAllowedValues(),
                    runningCampaign);
            });
        mappersByType.put(SettingType.ENUM_LIST,
            (RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
                RunningCampaign runningCampaign) -> {
                RunningEnumListVariable enumListVariable = (RunningEnumListVariable) variable;
                return new EnumListRuntimeFlatSettingImpl(variable.getName(), variableVariant,
                    variable.getValue(variableVariant), componentId, enumListVariable.getAllowedValues(),
                    runningCampaign);
            });
        mappersByType.put(SettingType.PARTNER_ENUM,
            (RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
                RunningCampaign runningCampaign) -> {
                RunningPartnerEnumVariable partnerEnumVariable = (RunningPartnerEnumVariable) variable;
                return new PartnerEnumFlatRuntimeSettingImpl(variable.getName(), variableVariant,
                    variable.getValue(variableVariant), componentId, partnerEnumVariable.getOptions(),
                    runningCampaign);
            });
        mappersByType.put(SettingType.PARTNER_ENUM_LIST,
            (RunningVariable variable, String variableVariant, Id<RunningComponent> componentId,
                RunningCampaign runningCampaign) -> {
                RunningPartnerEnumListVariable partnerEnumListVariable = (RunningPartnerEnumListVariable) variable;
                return new PartnerEnumListRuntimeFlatSettingImpl(variable.getName(), variableVariant,
                    variable.getValue(variableVariant), componentId, partnerEnumListVariable.getOptions(),
                    runningCampaign);
            });

        return ImmutableMap.copyOf(mappersByType);
    }

}
