package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentComponentIdFilterConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentComponentIdVariableConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.ComponentIdVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentComponentIdVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentComponentIdVariableConfiguration> {

    @Override
    public CampaignComponentComponentIdVariableConfiguration mapToSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
        ComponentIdVariable variable = (ComponentIdVariable) setting;

        return new CampaignComponentComponentIdVariableConfiguration(variable.getName(),
            variable.getDisplayName(),
            SettingType.valueOf(variable.getType().name()),
            mapValuesToComponentPath(restMapperContext, variable),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            variable.getPriority(),
            new CampaignComponentComponentIdFilterConfiguration(variable.getFilter().getComponentTypes()));
    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.COMPONENT_ID);
    }

    private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        mapValuesToComponentPath(CampaignComponentRestMapperContext restMapperContext, ComponentIdVariable variable) {
        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> resultValues =
                Maps.newHashMap();
        for (Map.Entry<String,
            BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>> valueEntry : variable
                    .getValues().entrySet()) {
            Optional<String> providedValue = getProvidedValue(valueEntry.getValue());
            if (providedValue.isEmpty()) {
                resultValues.put(valueEntry.getKey(), valueEntry.getValue());
            } else {
                Id<CampaignComponent> componentId = Id.valueOf(providedValue.get());
                List<String> absoluteNames = restMapperContext.absoluteComponentNames().get(componentId);
                if (absoluteNames != null && !absoluteNames.isEmpty()) {
                    resultValues.put(valueEntry.getKey(), Provided.nestedOptionalOf(absoluteNames.get(0)));
                } else {
                    resultValues.put(valueEntry.getKey(), valueEntry.getValue());
                }
            }
        }
        return resultValues;
    }

    private Optional<String> getProvidedValue(
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> evaluatable) {
        return evaluatable instanceof Provided<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>> buildTimeEvaluatable
            && buildTimeEvaluatable.getValue() instanceof Provided<?, Optional<Object>> runtimeValue
            && runtimeValue.getValue().isPresent()
                ? Optional.of(runtimeValue.getValue().get().toString())
                : Optional.empty();
    }
}
