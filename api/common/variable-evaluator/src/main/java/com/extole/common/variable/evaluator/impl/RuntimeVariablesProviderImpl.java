package com.extole.common.variable.evaluator.impl;

import static com.extole.model.entity.campaign.Setting.COMPONENT_ID_SETTING_NAME;
import static com.extole.model.entity.campaign.Setting.COMPONENT_NAME_SETTING_NAME;
import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.api.impl.campaign.FlatSettingMapper;
import com.extole.api.impl.campaign.FlatVariableMapper;
import com.extole.api.impl.campaign.RuntimeFlatSetting;
import com.extole.api.impl.campaign.VariableKey;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.variable.evaluator.RuntimeVariableEvaluationService;
import com.extole.common.variable.evaluator.RuntimeVariablesProvider;
import com.extole.common.variable.evaluator.VariableEvaluator;
import com.extole.common.variable.evaluator.VariableRuntimeContextFactory;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.running.service.campaign.provider.RunningCampaignService;
import com.extole.running.service.component.RunningComponent;
import com.extole.running.service.component.RunningSetting;
import com.extole.running.service.component.RunningSocket;
import com.extole.running.service.component.RunningVariable;
import com.extole.sandbox.Sandbox;
import com.extole.sandbox.SandboxModel;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;

@Component
public class RuntimeVariablesProviderImpl implements RuntimeVariablesProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeVariablesProviderImpl.class);
    private static final String DEFAULT_VARIANT = "default";

    private static final Map<SettingType, FlatSettingMapper> RUNTIME_FLAT_SETTING_MAPPERS_BY_VARIABLE_TYPE =
        initializeRuntimeFlatVariableMappersByVariableType(List.of(
            SettingType.SOCKET,
            SettingType.MULTI_SOCKET));

    private final RuntimeVariableEvaluationService runtimeVariableEvaluationService;
    private final VariableRuntimeContextFactory variableRuntimeContextFactory;
    private final RunningCampaignService runningCampaignService;
    private final SandboxService sandboxService;

    @Autowired
    public RuntimeVariablesProviderImpl(
        RuntimeVariableEvaluationService runtimeVariableEvaluationService,
        VariableRuntimeContextFactory variableRuntimeContextFactory,
        RunningCampaignService runningCampaignService,
        SandboxService sandboxService) {
        this.runtimeVariableEvaluationService = runtimeVariableEvaluationService;
        this.variableRuntimeContextFactory = variableRuntimeContextFactory;
        this.runningCampaignService = runningCampaignService;
        this.sandboxService = sandboxService;
    }

    @Override
    public VariableEvaluator provide(Id<ClientHandle> clientId, Id<?> elementId,
        Sandbox sandbox, String variant, Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier) {
        Optional<RunningCampaign> campaign = findRunningCampaign(clientId, sandbox, componentIdSupplier);
        return this.provideVariableEvaluator(campaign, variant, componentIdSupplier,
            (evaluator) -> variableRuntimeContextFactory.createContextSupplier(evaluator));
    }

    @Override
    public VariableEvaluator provide(Id<ClientHandle> clientId, RunningCampaign campaign,
        String variant, Id<?> elementId, Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier,
        VariableContextSupplier variableContextSupplier) {
        return provideVariableEvaluator(Optional.of(campaign), variant, componentIdSupplier,
            variableContextSupplier);
    }

    private LoopPreventingVariableEvaluatorImpl provideVariableEvaluator(Optional<RunningCampaign> campaign,
        String variant, Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier,
        VariableContextSupplier contextSupplier) {

        return new LoopPreventingVariableEvaluatorImpl(contextSupplier, runtimeVariableEvaluationService, variant,
            getVariables(campaign, componentIdSupplier));
    }

    private Map<VariableKey, RuntimeFlatSetting> getVariables(Optional<RunningCampaign> campaign,
        Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier) {
        Optional<Id<CampaignComponent>> campaignComponentId = componentIdSupplier.get();
        Map<VariableKey, RuntimeFlatSetting> variableKeyRuntimeFlatSettingMap = new HashMap<>(campaign.map(
            runningCampaign -> campaignComponentId
                .map(currentComponentId -> selectVariablesWithVariant(currentComponentId, runningCampaign))
                .orElse(new HashMap<>()))
            .orElse(emptyMap()));
        if (campaignComponentId.isPresent() && campaign.isPresent()) {
            VariableKey idVariableKey = VariableKey.of(COMPONENT_ID_SETTING_NAME, Variable.DEFAULT_VALUE_KEY);
            VariableKey nameVariableKey = VariableKey.of(COMPONENT_NAME_SETTING_NAME, Variable.DEFAULT_VALUE_KEY);
            RuntimeFlatSetting componentId = getRuntimeFlatSetting(campaign.get(), campaignComponentId.get(),
                idVariableKey, campaignComponentId.get());
            Optional<RunningComponent> runningComponent = campaign.get()
                .getComponents()
                .stream()
                .filter(component -> component.getId().equals(campaignComponentId.get()))
                .findFirst();
            RuntimeFlatSetting componentName = getRuntimeFlatSetting(campaign.get(), campaignComponentId.get(),
                nameVariableKey, runningComponent.map(component -> component.getName()).orElse(""));
            variableKeyRuntimeFlatSettingMap.put(componentId.getVariableKey(), componentId);
            variableKeyRuntimeFlatSettingMap.put(componentName.getVariableKey(), componentName);
        }

        return variableKeyRuntimeFlatSettingMap;
    }

    private static RuntimeFlatSettingImpl getRuntimeFlatSetting(RunningCampaign campaign,
        Id<CampaignComponent> campaignComponentId, VariableKey variableKey, Object settingValue) {
        return new RuntimeFlatSettingImpl(SettingType.STRING, variableKey.getName(), variableKey.getKey(),
            Provided.optionalOf(settingValue), Id.valueOf(campaignComponentId.getValue()), campaign);
    }

    private Map<VariableKey, RuntimeFlatSetting> selectVariablesWithVariant(Id<CampaignComponent> componentId,
        RunningCampaign runningCampaign) {
        Map<VariableKey, RuntimeFlatSetting> variantWithVariables = new HashMap<>();
        for (RunningComponent component : runningCampaign.getComponents()) {
            if (!componentId.equals(component.getId())) {
                continue;
            }
            for (RunningSetting setting : component.getSettings()) {
                if (setting instanceof RunningVariable variable) {
                    variable.getValues().keySet().forEach(variableVariant -> {
                        VariableKey variableKey = VariableKey.of(variable.getName(), variableVariant);
                        FlatVariableMapper mapper =
                            RuntimeFlatVariableMapperFactory.getInstance().create(variable.getSettingType());
                        variantWithVariables.put(variableKey,
                            mapper.map(variable, variableVariant, component.getId(), runningCampaign));
                    });
                } else if (setting instanceof RunningSocket variable) {
                    VariableKey variableKey = VariableKey.of(variable.getName(), DEFAULT_VARIANT);
                    getMapperByType(variable.getSettingType())
                        .ifPresent(mapper -> {
                            variantWithVariables.put(variableKey,
                                mapper.map(variable, component.getId(), runningCampaign));
                        });
                }
            }
        }
        return variantWithVariables;
    }

    private static Optional<FlatSettingMapper> getMapperByType(SettingType type) {
        return Optional.ofNullable(RUNTIME_FLAT_SETTING_MAPPERS_BY_VARIABLE_TYPE.get(type));
    }

    private static Map<SettingType, FlatSettingMapper>
        initializeRuntimeFlatVariableMappersByVariableType(List<SettingType> socketSettingTypes) {
        Map<SettingType, FlatSettingMapper> mappersByType = new HashMap<>();
        for (SettingType socketSettingType : socketSettingTypes) {
            mappersByType.put(socketSettingType,
                (RunningSetting variable, Id<RunningComponent> componentId, RunningCampaign runningCampaign) -> {
                    RunningSocket multiSocketVariable = (RunningSocket) variable;
                    List<Id<com.extole.model.entity.campaign.Component>> installedIntoComponents =
                        runningCampaign.getComponents()
                            .stream()
                            .filter(component -> component.getInstalledIntoSocket().isPresent())
                            .filter(component -> component.getComponentIds().contains(componentId))
                            .filter(component -> component.getInstalledIntoSocket()
                                .get().equals(multiSocketVariable.getName()))
                            .map(component -> Id.<com.extole.model.entity.campaign.Component>valueOf(
                                component.getId().getValue()))
                            .collect(Collectors.toList());
                    return new SocketRuntimeFlatSettingImpl(variable.getName(),
                        socketSettingType == SettingType.MULTI_SOCKET
                            ? SettingType.MULTI_SOCKET
                            : SettingType.SOCKET,
                        componentId, installedIntoComponents, runningCampaign);
                });
        }

        return ImmutableMap.copyOf(mappersByType);
    }

    private Optional<RunningCampaign> findRunningCampaign(Id<ClientHandle> clientId, Sandbox sandbox,
        Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier) {
        Optional<Id<CampaignComponent>> campaignComponentId = componentIdSupplier.get();
        List<RunningCampaign> campaigns =
            runningCampaignService.getCampaigns(clientId, resolveSandbox(clientId, sandbox));
        return campaignComponentId.flatMap(componentId -> campaigns.stream()
            .filter(campaign -> campaign.getComponents()
                .stream()
                .anyMatch(component -> component.getId().equals(componentId)))
            .findFirst());
    }

    private SandboxModel resolveSandbox(Id<ClientHandle> clientId, Sandbox sandbox) {
        Id<SandboxModel> sandboxId = Id.valueOf(sandbox.getSandboxId());
        try {
            return sandboxService.getById(clientId, sandboxId);
        } catch (SandboxNotFoundException e) {
            LOG.error("Sandbox not found: {}, clientId:{}", sandboxId, clientId, e);
            return sandboxService.getProductionSandbox(clientId, sandbox.getContainer());
        }
    }

}
