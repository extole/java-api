package com.extole.client.rest.impl.campaign.upload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentFacetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionApproveConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCancelRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreateMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreativeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDataIntelligenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDeclineConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDisplayConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEarnRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEmailConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFireAsPersonConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFulfillRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeStatusUpdateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRedeemRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRemoveMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRevokeRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionScheduleConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionShareEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionSignalConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionSignalV1Configuration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionStepSignalConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionWebhookConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAccessConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerClientDomainConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerDataIntelligenceEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerGroupConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasIdentityConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerLegacyLabelTargetingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerLegacyQualityConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerMaxMindConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerReferredByEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerRewardEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerScoreConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerSendRewardEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerShareConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerStepEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerTargetingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerZoneStateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepAppConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepMetricConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFrontendControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignJourneyEntryConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignLabelConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.QualityRuleConfiguration;
import com.extole.client.rest.campaign.configuration.RewardRuleConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.configuration.TransitionRuleConfiguration;
import com.extole.client.rest.impl.campaign.component.asset.UploadedAssetId;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.id.Id;
import com.extole.model.entity.QualityRuleType;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentAsset;
import com.extole.model.entity.campaign.CampaignComponentFacet;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepApp;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;
import com.extole.model.entity.campaign.CampaignJourneyEntry;
import com.extole.model.entity.campaign.CampaignLabel;
import com.extole.model.entity.campaign.CampaignLabelType;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.RuleActionType;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Socket;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetBuilder;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.CampaignControllerActionBuilder;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveBuilder;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardBuilder;
import com.extole.model.service.campaign.controller.action.create.membership.CampaignControllerActionCreateMembershipBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceBuilder;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineBuilder;
import com.extole.model.service.campaign.controller.action.display.CampaignControllerActionDisplayBuilder;
import com.extole.model.service.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardBuilder;
import com.extole.model.service.campaign.controller.action.email.CampaignControllerActionEmailBuilder;
import com.extole.model.service.campaign.controller.action.expression.CampaignControllerActionExpressionBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonBuilder;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateBuilder;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardBuilder;
import com.extole.model.service.campaign.controller.action.remove.membership.CampaignControllerActionRemoveMembershipBuilder;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBuilder;
import com.extole.model.service.campaign.controller.action.share.CampaignControllerActionShareEventBuilder;
import com.extole.model.service.campaign.controller.action.signal.CampaignControllerActionSignalBuilder;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1Builder;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalBuilder;
import com.extole.model.service.campaign.controller.action.webhook.CampaignControllerActionWebhookBuilder;
import com.extole.model.service.campaign.controller.data.intelligence.event.CampaignControllerTriggerDataIntelligenceEventBuilder;
import com.extole.model.service.campaign.controller.max.mind.CampaignControllerTriggerMaxMindBuilder;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuilder;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;
import com.extole.model.service.campaign.controller.trigger.access.CampaignControllerTriggerAccessBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventBuilder;
import com.extole.model.service.campaign.controller.trigger.client.domain.CampaignControllerTriggerClientDomainBuilder;
import com.extole.model.service.campaign.controller.trigger.event.CampaignControllerTriggerEventBuilder;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionBuilder;
import com.extole.model.service.campaign.controller.trigger.group.CampaignControllerTriggerGroupBuilder;
import com.extole.model.service.campaign.controller.trigger.has.identity.CampaignControllerTriggerHasIdentityBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepBuilder;
import com.extole.model.service.campaign.controller.trigger.legacy.label.targeting.CampaignControllerTriggerLegacyLabelTargetingBuilder;
import com.extole.model.service.campaign.controller.trigger.legacy.quality.CampaignControllerTriggerLegacyQualityBuilder;
import com.extole.model.service.campaign.controller.trigger.referred.by.event.CampaignControllerTriggerReferredByEventBuilder;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventBuilder;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreBuilder;
import com.extole.model.service.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventBuilder;
import com.extole.model.service.campaign.controller.trigger.share.CampaignControllerTriggerShareBuilder;
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventBuilder;
import com.extole.model.service.campaign.controller.trigger.targeting.CampaignControllerTriggerTargetingBuilder;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepBuilder;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppBuilder;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricBuilder;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryBuilder;
import com.extole.model.service.campaign.label.CampaignLabelBuilder;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.quality.rule.QualityRuleBuilder;
import com.extole.model.service.campaign.quality.rule.QualityRuleNotFoundException;
import com.extole.model.service.campaign.reward.rule.RewardRuleBuilder;
import com.extole.model.service.campaign.reward.rule.RewardRuleNotFoundException;
import com.extole.model.service.campaign.setting.SettingBuilder;
import com.extole.model.service.campaign.setting.SocketBuilder;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuilder;
import com.extole.model.service.campaign.step.data.StepDataBuilder;
import com.extole.model.service.campaign.transition.rule.TransitionRuleBuilder;
import com.extole.model.service.campaign.transition.rule.TransitionRuleNotFoundException;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;

public class CampaignUploadContextImpl implements CampaignUploadContext {

    private final CampaignBuilder campaignBuilder;
    private final ImmutableMap<Id<CampaignComponent>, CampaignComponent> componentsById;
    private final ImmutableMap<Id<CampaignComponentAsset>, CampaignComponentAsset> assetsById;
    private final ImmutableMap<Id<CampaignStep>, CampaignController> controllersById;
    private final ImmutableMap<Id<CampaignStep>, FrontendController> frontendControllersById;
    private final ImmutableMap<Id<CampaignStep>, CampaignJourneyEntry> campaignJourneyEntryById;
    private final ImmutableMap<Id<CampaignControllerTrigger>, CampaignControllerTrigger> triggersById;
    private final ImmutableMap<Id<CampaignControllerAction>, CampaignControllerAction> actionsById;
    private final ImmutableMap<Id<CampaignFlowStepMetric>, CampaignFlowStepMetric> metricsById;
    private final ImmutableMap<Id<CampaignFlowStepApp>, CampaignFlowStepApp> appsById;
    private final ImmutableMap<Id<CampaignFlowStep>, CampaignFlowStep> flowStepsById;
    private final ImmutableMap<Id<StepData>, StepData> controllerDataById;
    private final ImmutableMap<Id<StepData>, StepData> frontendControllerDataById;
    private final ImmutableMap<Id<StepData>, StepData> journeyEntryDataById;
    private final ImmutableMap<String, CampaignLabel> labelByNames;
    private final Map<String, ByteSource> creatives;
    private final Map<UploadedAssetId, ByteSource> componentAssets;
    private final Map<Id<CampaignComponent>, Map<String, CampaignComponentFacet>> facetsByComponentAndFacetName;

    private final Map<CampaignComponentConfiguration, CampaignComponentBuilder> componentBuilders = new HashMap<>();
    private final Map<CampaignComponentSettingConfiguration, SettingBuilder> settingBuilders = new HashMap<>();
    private final Map<CampaignComponentVariableConfiguration, VariableBuilder> socketParameterBuilders =
        new HashMap<>();
    private final Map<CampaignComponentAssetConfiguration, CampaignComponentAssetBuilder> assetBuilders =
        new HashMap<>();
    private final Map<CampaignComponentFacetConfiguration, CampaignComponentFacetBuilder> facetBuilders =
        new HashMap<>();
    private final Map<CampaignControllerConfiguration, CampaignControllerBuilder> controllerBuilders = new HashMap<>();
    private final Map<CampaignFrontendControllerConfiguration, FrontendControllerBuilder> frontendControllerBuilder =
        new HashMap<>();
    private final Map<CampaignJourneyEntryConfiguration, CampaignJourneyEntryBuilder> journeyEntriesBuilders =
        new HashMap<>();

    private final Map<CampaignControllerTriggerConfiguration, CampaignControllerTriggerBuilder<?, ?>> triggerBuilders =
        new HashMap<>();
    private final Map<CampaignControllerActionConfiguration, CampaignControllerActionBuilder<?>> actionBuilders =
        new HashMap<>();
    private final Map<CampaignFlowStepConfiguration, CampaignFlowStepBuilder> flowStepBuilders =
        new HashMap<>();

    private final Map<CampaignFlowStepMetricConfiguration, CampaignFlowStepMetricBuilder> metricBuilders =
        new HashMap<>();
    private final Map<StepDataConfiguration, StepDataBuilder> dataBuilders = new HashMap<>();

    private final Map<CampaignFlowStepAppConfiguration, CampaignFlowStepAppBuilder> appBuilders = new HashMap<>();
    private final Map<CampaignLabelConfiguration, CampaignLabelBuilder> labelBuilders = new HashMap<>();

    private final Map<QualityRuleConfiguration, QualityRuleBuilder> qualityRuleBuilders = new HashMap<>();

    private final Map<RewardRuleConfiguration, RewardRuleBuilder> rewardRuleBuilders = new HashMap<>();

    private final Map<TransitionRuleConfiguration, TransitionRuleBuilder> transitionRuleBuilders = new HashMap<>();
    private final boolean isNewCampaign;

    public CampaignUploadContextImpl(CampaignBuilder campaignBuilder,
        Campaign campaign,
        Map<String, ByteSource> creatives,
        Map<UploadedAssetId, ByteSource> componentAssets) {
        this.campaignBuilder = campaignBuilder;
        this.componentsById = campaign.getComponents()
            .stream()
            .collect(ImmutableMap.toImmutableMap(CampaignComponent::getId, Function.identity()));
        this.assetsById = campaign.getComponents()
            .stream()
            .flatMap(campaignComponent -> campaignComponent.getAssets().stream())
            .collect(ImmutableMap.toImmutableMap(CampaignComponentAsset::getId, Function.identity()));
        this.controllersById = campaign.getControllers()
            .stream()
            .collect(ImmutableMap.toImmutableMap(CampaignController::getId, Function.identity()));
        this.frontendControllersById = campaign.getFrontendControllers()
            .stream()
            .collect(ImmutableMap.toImmutableMap(FrontendController::getId, Function.identity()));
        this.campaignJourneyEntryById = campaign.getJourneyEntries()
            .stream()
            .collect(ImmutableMap.toImmutableMap(CampaignJourneyEntry::getId, Function.identity()));
        this.triggersById = campaign.getSteps().stream()
            .flatMap(step -> step.getTriggers().stream())
            .collect(ImmutableMap.toImmutableMap(CampaignControllerTrigger::getId, Function.identity()));
        this.actionsById = campaign.getActionableSteps().stream()
            .flatMap(step -> step.getActions().stream())
            .collect(ImmutableMap.toImmutableMap(CampaignControllerAction::getId, Function.identity()));
        this.flowStepsById = campaign.getFlowSteps()
            .stream()
            .collect(ImmutableMap.toImmutableMap(CampaignFlowStep::getId, Function.identity()));
        this.metricsById = campaign.getFlowSteps()
            .stream()
            .flatMap(campaignFlowStep -> campaignFlowStep.getMetrics().stream())
            .collect(ImmutableMap.toImmutableMap(CampaignFlowStepMetric::getId, Function.identity()));
        this.appsById = campaign.getFlowSteps()
            .stream()
            .flatMap(campaignFlowStep -> campaignFlowStep.getApps().stream())
            .collect(ImmutableMap.toImmutableMap(CampaignFlowStepApp::getId, Function.identity()));
        this.labelByNames = campaign.getLabels()
            .stream()
            .collect(ImmutableMap.toImmutableMap(CampaignLabel::getName, Function.identity()));
        this.controllerDataById = campaign.getControllers()
            .stream()
            .flatMap(campaignController -> campaignController.getData().stream())
            .collect(ImmutableMap.toImmutableMap(StepData::getId, Function.identity()));
        this.frontendControllerDataById = campaign.getFrontendControllers()
            .stream()
            .flatMap(frontendController -> frontendController.getData().stream())
            .collect(ImmutableMap.toImmutableMap(StepData::getId, Function.identity()));
        this.journeyEntryDataById = campaign.getJourneyEntries()
            .stream()
            .flatMap(journeyEntry -> journeyEntry.getData().stream())
            .collect(ImmutableMap.toImmutableMap(StepData::getId, Function.identity()));
        this.creatives = creatives;
        this.componentAssets = componentAssets;
        this.facetsByComponentAndFacetName = Collections.unmodifiableMap(
            campaign.getComponents().stream()
                .reduce(new HashMap<>(), (accumulator, current) -> {
                    Map<String, CampaignComponentFacet> facetsByName =
                        accumulator.computeIfAbsent(current.getId(), (key) -> KeyCaseInsensitiveMap.create());
                    current.getFacets().forEach(facet -> facetsByName.put(facet.getName(), facet));
                    return accumulator;
                }, (a, b) -> a));
        this.isNewCampaign = false;
    }

    public CampaignUploadContextImpl(CampaignBuilder campaignBuilder, Map<String, ByteSource> creatives,
        Map<UploadedAssetId, ByteSource> componentAssets) {
        this.campaignBuilder = campaignBuilder;
        this.creatives = creatives;
        this.componentAssets = componentAssets;
        this.facetsByComponentAndFacetName = ImmutableMap.of();
        this.componentsById = ImmutableMap.of();
        this.assetsById = ImmutableMap.of();
        this.controllersById = ImmutableMap.of();
        this.frontendControllersById = ImmutableMap.of();
        this.campaignJourneyEntryById = ImmutableMap.of();
        this.triggersById = ImmutableMap.of();
        this.actionsById = ImmutableMap.of();
        this.flowStepsById = ImmutableMap.of();
        this.metricsById = ImmutableMap.of();
        this.appsById = ImmutableMap.of();
        this.labelByNames = ImmutableMap.of();
        this.controllerDataById = ImmutableMap.of();
        this.frontendControllerDataById = ImmutableMap.of();
        this.journeyEntryDataById = ImmutableMap.of();
        this.isNewCampaign = true;
    }

    @Override
    public Map<String, ByteSource> getCreatives() {
        return creatives;
    }

    @Override
    public Map<UploadedAssetId, ByteSource> getAssets() {
        return componentAssets;
    }

    @Override
    public CampaignComponentBuilder get(CampaignComponentConfiguration component) {
        return componentBuilders.compute(component, (key, existing) -> {
            if (existing != null) {
                return existing;
            }
            if (isNewCampaign) {
                return campaignBuilder.addComponent();
            }

            if (component.getId().isOmitted()) {
                if (component.getName().equalsIgnoreCase(CampaignComponent.ROOT)) {
                    return componentsById.values()
                        .stream()
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(CampaignComponent.ROOT))
                        .findFirst().map(campaignComponent -> campaignBuilder.updateComponent(campaignComponent))
                        .get();
                }
                return campaignBuilder.addComponent();
            }
            return campaignBuilder.updateComponent(componentsById.get(component.getId().getValue()));
        });
    }

    @Override
    public SettingBuilder get(CampaignComponentConfiguration component,
        CampaignComponentSettingConfiguration setting) {
        return settingBuilders.compute(setting,
            (key, existing) -> {
                if (existing != null) {
                    return existing;
                }

                if (isNewCampaign) {
                    return get(component).addSetting(SettingType.valueOf(setting.getType().name()));
                }

                if (componentsById.containsKey(component.getId().getValue())) {
                    Optional<Setting> existingSetting =
                        componentsById.get(component.getId().getValue()).getSettings().stream()
                            .filter(candidate -> candidate.getName().equalsIgnoreCase(setting.getName()))
                            .findFirst();
                    if (existingSetting.isPresent()) {
                        return get(component).updateSetting(existingSetting.get());
                    }
                }
                if (component.getName().equalsIgnoreCase(CampaignComponent.ROOT)) {
                    Optional<Setting> existingSetting = componentsById.values()
                        .stream()
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(CampaignComponent.ROOT))
                        .flatMap(root -> root.getSettings().stream())
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(setting.getName()))
                        .findFirst();
                    if (existingSetting.isPresent()) {
                        return get(component).updateSetting(existingSetting.get());
                    }
                }
                return get(component).addSetting(SettingType.valueOf(setting.getType().name()));
            });
    }

    @Override
    public VariableBuilder get(CampaignComponentConfiguration component, CampaignComponentSocketConfiguration socket,
        CampaignComponentVariableConfiguration variable) {
        return socketParameterBuilders.compute(variable,
            (key, existing) -> {
                if (existing != null) {
                    return existing;
                }

                if (isNewCampaign) {
                    return ((SocketBuilder) get(component, socket))
                        .addParameter(SettingType.valueOf(variable.getType().name()));
                }

                if (componentsById.containsKey(component.getId().getValue())) {
                    Optional<Variable> existingVariable =
                        componentsById.get(component.getId().getValue()).getSettings().stream()
                            .filter(candidate -> candidate instanceof Socket)
                            .filter(candidate -> candidate.getName().equalsIgnoreCase(socket.getName()))
                            .flatMap(candidate -> ((Socket) candidate).getParameters().stream())
                            .filter(candidate -> candidate.getName().equalsIgnoreCase(variable.getName()))
                            .findFirst();
                    if (existingVariable.isPresent()) {
                        return ((SocketBuilder) get(component, socket)).updateParameter(existingVariable.get());
                    }
                }
                if (component.getName().equalsIgnoreCase(CampaignComponent.ROOT)) {
                    Optional<Variable> existingVariable = componentsById.values()
                        .stream()
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(CampaignComponent.ROOT))
                        .flatMap(root -> root.getSettings().stream())
                        .filter(candidate -> candidate instanceof Socket)
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(socket.getName()))
                        .flatMap(candidate -> ((Socket) candidate).getParameters().stream())
                        .filter(candidate -> candidate.getName().equalsIgnoreCase(variable.getName()))
                        .findFirst();
                    if (existingVariable.isPresent()) {
                        return ((SocketBuilder) get(component, socket)).updateParameter(existingVariable.get());
                    }
                }
                return ((SocketBuilder) get(component, socket))
                    .addParameter(SettingType.valueOf(variable.getType().name()));
            });
    }

    @Override
    public CampaignComponentAssetBuilder get(CampaignComponentConfiguration component,
        CampaignComponentAssetConfiguration asset) {
        return assetBuilders.compute(asset, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            CampaignComponentBuilder campaignComponentBuilder = get(component);

            if (asset.getId().isOmitted() || isNewCampaign) {
                return campaignComponentBuilder.addAsset();
            }
            return campaignComponentBuilder.updateAsset(assetsById.get(asset.getId().getValue()));
        });

    }

    @Override
    public CampaignComponentFacetBuilder get(CampaignComponentConfiguration component,
        CampaignComponentFacetConfiguration facet) {

        return facetBuilders.compute(facet, (key, existing) -> {
            if (existing != null) {
                return existing;
            }
            CampaignComponentBuilder campaignComponentBuilder = get(component);
            if (isNewCampaign || component.getId().isOmitted()
                || !facetsByComponentAndFacetName.containsKey(component.getId().getValue())
                || !facetsByComponentAndFacetName.get(component.getId().getValue()).containsKey(facet.getName())) {
                return campaignComponentBuilder.addFacet();
            }
            CampaignComponentFacet facetToUpdate =
                facetsByComponentAndFacetName.get(component.getId().getValue()).get(facet.getName());
            return campaignComponentBuilder.updateFacet(facetToUpdate);
        });
    }

    @Override
    public CampaignControllerBuilder get(CampaignControllerConfiguration controller) {
        return controllerBuilders.compute(controller, (key, existing) -> {
            if (existing != null) {
                return existing;
            }
            if (controller.getId().isOmitted() || isNewCampaign) {
                return campaignBuilder.addController();
            }
            return campaignBuilder.updateController(controllersById.get(controller.getId().getValue()));
        });
    }

    @Override
    public FrontendControllerBuilder get(CampaignFrontendControllerConfiguration controller) {
        return frontendControllerBuilder.compute(controller, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (controller.getId().isOmitted() || isNewCampaign) {
                return campaignBuilder.addFrontendController();
            }
            return campaignBuilder.updateFrontendController(frontendControllersById.get(controller.getId().getValue()));
        });
    }

    @Override
    public CampaignJourneyEntryBuilder get(CampaignJourneyEntryConfiguration controller) {
        return journeyEntriesBuilders.compute(controller, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (controller.getId().isOmitted() || isNewCampaign) {
                return campaignBuilder.addJourneyEntry();
            }
            return campaignBuilder.updateJourneyEntry(campaignJourneyEntryById.get(controller.getId().getValue()));
        });
    }

    @Override
    public CampaignFlowStepBuilder get(CampaignFlowStepConfiguration flowStep) {
        return flowStepBuilders.compute(flowStep, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (flowStep.getFlowStepId().isOmitted() || isNewCampaign) {
                return campaignBuilder.addFlowStep();
            }
            return campaignBuilder.updateFlowStep(flowStepsById.get(flowStep.getFlowStepId().getValue()));
        });
    }

    @Override
    public CampaignFlowStepMetricBuilder get(CampaignFlowStepConfiguration flowStep,
        CampaignFlowStepMetricConfiguration metric) {
        return metricBuilders.compute(metric, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (metric.getId().isOmitted() || isNewCampaign) {
                return get(flowStep).addFlowStepMetric();
            }
            return get(flowStep).updateFlowStepMetric(metricsById.get(metric.getId().getValue()));
        });
    }

    @Override
    public CampaignFlowStepAppBuilder get(CampaignFlowStepConfiguration flowStep,
        CampaignFlowStepAppConfiguration app) {
        return appBuilders.compute(app, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (app.getId().isOmitted() || isNewCampaign) {
                return get(flowStep).addFlowStepApp();
            }
            return get(flowStep).updateFlowStepApp(appsById.get(app.getId().getValue()));
        });
    }

    @Override
    public CampaignLabelBuilder get(CampaignLabelConfiguration label) {
        return labelBuilders.compute(label, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (labelByNames.containsKey(label.getName())) {
                CampaignLabelBuilder campaignLabelBuilder =
                    campaignBuilder.updateLabel(labelByNames.get(label.getName()));
                return new CampaignLabelBuilder() {
                    @Override
                    public CampaignLabelBuilder withName(String name) {
                        return this;
                    }

                    @Override
                    public CampaignLabelBuilder withType(CampaignLabelType type) {
                        return campaignLabelBuilder.withType(type);
                    }

                    @Override
                    public CampaignLabel save() throws ConcurrentCampaignUpdateException,
                        CampaignLabelMissingNameException, CampaignLabelDuplicateNameException,
                        BuildCampaignEvaluatableException, StaleCampaignVersionException {
                        return campaignLabelBuilder.save();
                    }
                };
            }
            return campaignBuilder.addLabel();
        });
    }

    @Override
    public QualityRuleBuilder get(QualityRuleConfiguration qualityRule) {
        return qualityRuleBuilders.compute(qualityRule, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (qualityRule.getId().isOmitted() || isNewCampaign) {
                return campaignBuilder.updateQualityRule(QualityRuleType.valueOf(qualityRule.getRuleType().name()));
            }
            try {
                return campaignBuilder.updateQualityRule(Id.valueOf(qualityRule.getId().getValue().getValue()));
            } catch (QualityRuleNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public RewardRuleBuilder get(RewardRuleConfiguration rewardRule) {
        return rewardRuleBuilders.compute(rewardRule, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (rewardRule.getId().isOmitted() || isNewCampaign) {
                try {
                    return campaignBuilder.addRewardRule(Id.valueOf(rewardRule.getRewardSupplierId()));
                } catch (RewardSupplierNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                return campaignBuilder.updateRewardRule(Id.valueOf(rewardRule.getId().getValue().getValue()));
            } catch (RewardRuleNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public TransitionRuleBuilder get(TransitionRuleConfiguration transitionRule) {
        return transitionRuleBuilders.compute(transitionRule, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (transitionRule.getTransitionRuleId().isOmitted() || isNewCampaign) {
                RuleActionType actionType = RuleActionType.valueOf(transitionRule.getActionType().name());
                return campaignBuilder.updateTransitionRule(actionType).orElseGet(() -> campaignBuilder
                    .addTransitionRuleBuilder(actionType));
            }
            try {
                return campaignBuilder
                    .updateTransitionRule(Id.valueOf(transitionRule.getTransitionRuleId().getValue().getValue()));
            } catch (TransitionRuleNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CampaignControllerTriggerZoneStateBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerZoneStateConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerReferredByEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerReferredByEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerExpressionBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerExpressionConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerHasIdentityBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasIdentityConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerClientDomainBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerClientDomainConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerHasPriorStepBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorStepConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerAudienceMembershipEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerSendRewardEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerSendRewardEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerRewardEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerRewardEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerMaxMindBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerMaxMindConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerAccessBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerAccessConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerLegacyLabelTargetingBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerLegacyLabelTargetingConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerShareBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerShareConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerLegacyQualityBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerLegacyQualityConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerScoreBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerScoreConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerHasPriorRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorRewardConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerDataIntelligenceEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerDataIntelligenceEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerAudienceMembershipConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerGroupBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerGroupConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerStepEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerStepEventConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerTriggerTargetingBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerTargetingConfiguration trigger) {
        return getTriggerBuilder(step, trigger);
    }

    @Override
    public CampaignControllerActionIncentivizeStatusUpdateBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeStatusUpdateConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionFireAsPersonBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionFireAsPersonConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionIncentivizeBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionExpressionBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionExpressionConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionSignalV1Builder get(CampaignStepConfiguration step,
        CampaignControllerActionSignalV1Configuration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionFulfillRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionFulfillRewardConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionDataIntelligenceBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDataIntelligenceConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionRevokeRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRevokeRewardConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionScheduleBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionScheduleConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionDeclineBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDeclineConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionDisplayBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDisplayConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionRemoveMembershipBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRemoveMembershipConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionCreateMembershipBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCreateMembershipConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionShareEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionShareEventConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionStepSignalBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionStepSignalConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionRedeemRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRedeemRewardConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionSignalBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionSignalConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionWebhookBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionWebhookConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionCancelRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCancelRewardConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionCreativeBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCreativeConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionEarnRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionEarnRewardConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionEmailBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionEmailConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public CampaignControllerActionApproveBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionApproveConfiguration action) {
        return getActionBuilder(step, action);
    }

    @Override
    public StepDataBuilder get(CampaignControllerConfiguration controller, StepDataConfiguration data) {
        return dataBuilders.compute(data, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (data.getId().isOmitted() || isNewCampaign) {
                return get(controller).addData();
            }
            return get(controller).updateStepData(controllerDataById.get(data.getId().getValue()));
        });
    }

    @Override
    public StepDataBuilder get(CampaignFrontendControllerConfiguration controller, StepDataConfiguration data) {
        return dataBuilders.compute(data, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (data.getId().isOmitted() || isNewCampaign) {
                return get(controller).addData();
            }
            return get(controller).updateStepData(frontendControllerDataById.get(data.getId().getValue()));
        });
    }

    @Override
    public StepDataBuilder get(CampaignJourneyEntryConfiguration journeyEntry, StepDataConfiguration data) {
        return dataBuilders.compute(data, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (data.getId().isOmitted() || isNewCampaign) {
                return get(journeyEntry).addData();
            }
            return get(journeyEntry).updateStepData(journeyEntryDataById.get(data.getId().getValue()));
        });
    }

    private <T extends CampaignControllerActionBuilder<A>, A extends CampaignControllerAction> T getActionBuilder(
        CampaignStepConfiguration step, CampaignControllerActionConfiguration action) {
        return (T) actionBuilders.compute(action, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            if (action.getActionId().isOmitted() || isNewCampaign) {
                CampaignControllerActionType actionType =
                    CampaignControllerActionType.valueOf(action.getActionType().name());
                if (step instanceof CampaignFrontendControllerConfiguration) {
                    return get((CampaignFrontendControllerConfiguration) step).addAction(actionType);
                }
                if (step instanceof CampaignControllerConfiguration) {
                    return get((CampaignControllerConfiguration) step).addAction(actionType);
                }
            }
            A campaignControllerAction = (A) actionsById.get(action.getActionId().getValue());

            if (step instanceof CampaignFrontendControllerConfiguration) {
                return get((CampaignFrontendControllerConfiguration) step).updateAction(campaignControllerAction);
            }
            if (step instanceof CampaignControllerConfiguration) {
                return get((CampaignControllerConfiguration) step).updateAction(campaignControllerAction);
            }

            throw new UnsupportedOperationException("Unsupported step type: " + step.getClass().getName());
        });
    }

    private <T extends CampaignControllerTrigger, B extends CampaignControllerTriggerBuilder<B, T>> B getTriggerBuilder(
        CampaignStepConfiguration step, CampaignControllerTriggerConfiguration trigger) {
        return (B) triggerBuilders.compute(trigger, (key, existing) -> {
            if (existing != null) {
                return existing;
            }

            try {
                if (trigger.getTriggerId().isOmitted() || isNewCampaign) {
                    return getStepBuilder(step).<T, B>addTrigger(
                        CampaignControllerTriggerType.valueOf(trigger.getTriggerType().name()));
                }
            } catch (TriggerTypeNotSupportedException e) {
                throw new IllegalStateException(e);
            }
            T campaignControllerTrigger = (T) triggersById.get(trigger.getTriggerId().getValue());
            return getStepBuilder(step).<T, B>updateTrigger(campaignControllerTrigger);

        });
    }

    private CampaignStepBuilder<?, ?> getStepBuilder(CampaignStepConfiguration step) {
        if (step instanceof CampaignControllerConfiguration) {
            return get((CampaignControllerConfiguration) step);
        }
        if (step instanceof CampaignFrontendControllerConfiguration) {
            return get((CampaignFrontendControllerConfiguration) step);
        }
        if (step instanceof CampaignJourneyEntryConfiguration) {
            return get((CampaignJourneyEntryConfiguration) step);
        }
        throw new UnsupportedOperationException("Unsupported step type: " + step.getClass().getName());
    }

}
