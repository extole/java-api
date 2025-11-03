package com.extole.client.rest.impl.campaign;

import static java.util.Collections.unmodifiableMap;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.campaign.CampaignLockType;
import com.extole.client.rest.campaign.CampaignResponse;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.CampaignVersionDescriptionResponse;
import com.extole.client.rest.campaign.component.CampaignComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignLabelConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.configuration.IncentiveConfiguration;
import com.extole.client.rest.campaign.configuration.QualityRuleConfiguration;
import com.extole.client.rest.campaign.configuration.RewardRuleConfiguration;
import com.extole.client.rest.campaign.configuration.TransitionRuleConfiguration;
import com.extole.client.rest.campaign.controller.response.CampaignStepResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepResponse;
import com.extole.client.rest.campaign.incentive.IncentiveResponse;
import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleResponse;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleType;
import com.extole.client.rest.campaign.incentive.reward.rule.ExpressionType;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleExpression;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleResponse;
import com.extole.client.rest.campaign.incentive.reward.rule.Rewardee;
import com.extole.client.rest.campaign.incentive.reward.rule.RuleDataMatcherType;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleResponse;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.response.CampaignStepResponseMapper;
import com.extole.client.rest.impl.campaign.controller.response.CampaignStepResponseMapperRepository;
import com.extole.client.rest.impl.campaign.flow.step.CampaignFlowStepRestMapper;
import com.extole.client.rest.impl.campaign.label.CampaignLabelRestMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.QualityRule;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignComponentReference;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.RewardRule;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.entity.campaign.TransitionRule;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionDescription;
import com.extole.model.service.campaign.component.ComponentAbsoluteNameFinder;
import com.extole.model.service.campaign.component.ComponentNotFoundException;
import com.extole.model.service.campaign.component.ComponentService;

@Component
public class CampaignRestMapper {

    private static final Comparator<CampaignStep> STEP_COMPARATOR = (firstStep, secondStep) -> {
        if (firstStep.getType() == StepType.JOURNEY_ENTRY) {
            return 0;
        }

        return 1;
    };

    private final CampaignComponentRestMapper componentRestMapper;
    private final CampaignFlowStepRestMapper flowStepRestMapper;
    private final CampaignLabelRestMapper labelRestMapper;
    private final ComponentService componentService;
    private final ComponentAbsoluteNameFinder componentAbsoluteNameFinder;
    private final CampaignStepResponseMapperRepository stepResponseMapperRepository;

    @Autowired
    public CampaignRestMapper(
        CampaignComponentRestMapper componentRestMapper,
        CampaignFlowStepRestMapper flowStepRestMapper,
        CampaignLabelRestMapper labelRestMapper,
        ComponentService componentService,
        ComponentAbsoluteNameFinder componentAbsoluteNameFinder,
        CampaignStepResponseMapperRepository stepResponseMapperRepository) {
        this.componentRestMapper = componentRestMapper;
        this.flowStepRestMapper = flowStepRestMapper;
        this.labelRestMapper = labelRestMapper;
        this.componentService = componentService;
        this.componentAbsoluteNameFinder = componentAbsoluteNameFinder;
        this.stepResponseMapperRepository = stepResponseMapperRepository;
    }

    public CampaignResponse toCampaignResponse(Campaign campaign, ZoneId timeZone) {
        List<CampaignComponentResponse> components = campaign.getComponents().stream()
            .map(componentReference -> componentRestMapper.toComponentResponse(componentReference, timeZone))
            .collect(Collectors.toList());

        List<CampaignStepResponse> steps = campaign.getSteps().stream()
            .map(step -> {
                CampaignStepResponseMapper mapper = stepResponseMapperRepository.getMapper(step.getType());
                return mapper.toResponse(step, timeZone);
            })
            .sorted(Comparator.comparing(step -> step.getId()))
            .collect(Collectors.toList());

        List<CampaignFlowStepResponse> flowSteps = campaign.getFlowSteps().stream()
            .map(flowStep -> flowStepRestMapper.toFlowStepResponse(flowStep))
            .collect(Collectors.toList());

        List<CampaignLabelResponse> labels = campaign.getLabels().stream()
            .map(label -> labelRestMapper.toCampaignLabelResponse(label, timeZone))
            .sorted(Comparator.comparing(campaignLabelResponse -> campaignLabelResponse.getName()))
            .collect(Collectors.toList());

        Set<CampaignLockType> campaignLocks = campaign.getLocks().stream()
            .map(lockType -> CampaignLockType.valueOf(lockType.name())).collect(Collectors.toSet());

        IncentiveResponse incentive = toIncentiveResponse(campaign);

        return new CampaignResponse(
            campaign.getId().getValue(),
            campaign.getName(),
            campaign.getDescription(),
            incentive.getId(),
            campaign.getUpdatedDate().atZone(timeZone),
            campaign.getLastPublishedDate().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getStartDate().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getStopDate().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getPausedAt().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getEndedAt().map(date -> date.atZone(timeZone)).orElse(null),
            !campaign.isDraft(),
            CampaignState.valueOf(campaign.getState().toString()),
            components,
            steps,
            labels,
            campaign.getProgramLabel().getName(),
            incentive,
            campaign.getVersion(),
            campaign.getParentVersion().orElse(null),
            campaign.getProgramType(),
            campaign.getThemeName().orElse(null),
            flowSteps,
            campaignLocks,
            campaign.getTags(),
            campaign.getVariantSelector(),
            campaign.getVariants(),
            CampaignType.valueOf(campaign.getCampaignType().name()));
    }

    public CampaignConfiguration toCampaignConfiguration(ClientAuthorization authorization, Campaign campaign,
        ZoneId timeZone) {
        ExternalAbsoluteNames externalAbsoluteNames = loadExternalAbsoluteNames(authorization, campaign);

        Map<Id<CampaignComponent>, List<String>> allAbsoluteNames = Maps.newHashMap(externalAbsoluteNames.get());
        allAbsoluteNames.putAll(componentAbsoluteNameFinder.findAllAbsoluteNamesById(campaign.getComponents()));
        Map<Id<CampaignComponent>, String> absoluteNames =
            componentAbsoluteNameFinder.findAbsoluteNameById(campaign.getComponents());

        List<CampaignComponentConfiguration> componentConfigurations = campaign.getComponents().stream()
            .map(
                componentReference -> componentRestMapper.toComponentConfiguration(componentReference, timeZone,
                    allAbsoluteNames))
            .collect(Collectors.toList());

        List<CampaignStepConfiguration> stepConfigurations = campaign.getSteps().stream()
            .sorted(STEP_COMPARATOR)
            .map(step -> {
                CampaignStepResponseMapper mapper = stepResponseMapperRepository.getMapper(step.getType());
                return mapper.toConfiguration(step, timeZone, absoluteNames);
            })
            .collect(Collectors.toList());

        List<CampaignFlowStepConfiguration> flowStepConfigurations = campaign.getFlowSteps().stream()
            .map(flowStep -> flowStepRestMapper.toFlowStepConfiguration(flowStep, absoluteNames))
            .collect(Collectors.toList());

        List<CampaignLabelConfiguration> labelConfigurations = campaign.getLabels().stream()
            .map(label -> labelRestMapper.toCampaignLabelConfiguration(label, timeZone))
            .sorted(Comparator.comparing(campaignLabelResponse -> campaignLabelResponse.getName()))
            .collect(Collectors.toList());

        Set<com.extole.client.rest.campaign.configuration.CampaignLockType> campaignLocks = campaign.getLocks().stream()
            .map(lockType -> com.extole.client.rest.campaign.configuration.CampaignLockType.valueOf(lockType.name()))
            .collect(Collectors.toSet());

        IncentiveConfiguration incentiveConfiguration = toIncentiveConfiguration(campaign);

        return new CampaignConfiguration(
            campaign.getName(),
            campaign.getDescription(),
            incentiveConfiguration.getId(),
            campaign.getUpdatedDate().atZone(timeZone),
            campaign.getLastPublishedDate().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getStartDate().map(date -> date.atZone(timeZone)).orElse(null),
            campaign.getStopDate().map(date -> date.atZone(timeZone)).orElse(null),
            !campaign.isDraft(),
            com.extole.client.rest.campaign.configuration.CampaignState.valueOf(campaign.getState().toString()),
            componentConfigurations,
            stepConfigurations,
            labelConfigurations,
            campaign.getProgramLabel().getName(),
            incentiveConfiguration,
            campaign.getVersion(),
            campaign.getParentVersion().orElse(null),
            campaign.getProgramType(),
            campaign.getThemeName().orElse(null),
            flowStepConfigurations,
            campaignLocks,
            campaign.getTags(),
            campaign.getVariantSelector(),
            campaign.getVariants(),
            CampaignType.valueOf(campaign.getCampaignType().name()));
    }

    public IncentiveResponse toIncentiveResponse(Campaign campaign) {
        List<QualityRuleResponse> qualityRules = campaign.getQualityRules().stream()
            .map(this::toQualityRuleResponse)
            .sorted(Comparator.comparing(QualityRuleResponse::getId))
            .collect(Collectors.toList());

        List<RewardRuleResponse> rewardRules = campaign.getRewardRules().stream()
            .map(this::toRewardRuleResponse)
            .sorted(Comparator.comparing(RewardRuleResponse::getId))
            .collect(Collectors.toList());

        List<TransitionRuleResponse> transitionRules = campaign.getTransitionRules().stream()
            .map(this::toTransitionRuleResponse)
            .sorted(Comparator.comparing(TransitionRuleResponse::getTransitionRuleId))
            .collect(Collectors.toList());

        return new IncentiveResponse(campaign.getIncentiveId().getValue(), qualityRules, rewardRules, transitionRules);
    }

    public IncentiveConfiguration toIncentiveConfiguration(Campaign campaign) {
        List<QualityRuleConfiguration> qualityRules = campaign.getQualityRules().stream()
            .sorted(Comparator.comparing(qualityRule -> qualityRule.getId().getValue()))
            .map(this::toQualityRuleConfiguration)
            .collect(Collectors.toList());

        List<RewardRuleConfiguration> rewardRules = campaign.getRewardRules().stream()
            .sorted(Comparator.comparing(rewardRule -> rewardRule.getId().getValue()))
            .map(this::toRewardRuleConfiguration)
            .collect(Collectors.toList());

        List<TransitionRuleConfiguration> transitionRules = campaign.getTransitionRules().stream()
            .sorted(Comparator.comparing(transitionRule -> transitionRule.getId().getValue()))
            .map(this::toTransitionRuleConfiguration)
            .collect(Collectors.toList());

        return new IncentiveConfiguration(campaign.getIncentiveId().getValue(), qualityRules, rewardRules,
            transitionRules);
    }

    private QualityRuleResponse toQualityRuleResponse(QualityRule qualityRule) {
        Set<RuleActionType> restRuleActionTypes = new TreeSet<>();
        for (com.extole.model.entity.campaign.RuleActionType actionType : qualityRule.getActionTypes()) {
            restRuleActionTypes.add(RuleActionType.valueOf(actionType.name()));
        }

        return new QualityRuleResponse(
            qualityRule.getId().getValue(),
            Boolean.valueOf(qualityRule.getEnabled()),
            QualityRuleType.valueOf(qualityRule.getRuleType().name()),
            restRuleActionTypes,
            qualityRule.getProperties());
    }

    private RewardRuleResponse toRewardRuleResponse(RewardRule rewardRule) {
        RuleDataMatcherType dataAttributeType = null;
        if (rewardRule.getDataAttributeMatcherType() != null) {
            dataAttributeType = RuleDataMatcherType.valueOf(rewardRule.getDataAttributeMatcherType().name());
        }
        return new RewardRuleResponse(
            rewardRule.getId().getValue(),
            Rewardee.valueOf(rewardRule.getRewardee().name()),
            rewardRule.getRewardSupplierId().getValue(),
            rewardRule.getReferralsPerReward(),
            rewardRule.getRewardCountLimit(),
            rewardRule.getRewardCountSinceMonth(),
            rewardRule.getRewardCountSinceDays(),
            rewardRule.getRewardValueLimit(),
            rewardRule.getRewardValueSinceMonth(),
            rewardRule.getRewardValueSinceDays(),
            Boolean.valueOf(rewardRule.isUniqueFriendRequired()),
            Boolean.valueOf(rewardRule.isReferralLoopAllowed()),
            rewardRule.getRewardSlots(),
            rewardRule.getMinCartValue(),
            RuleActionType.valueOf(rewardRule.getRuleActionType().name()),
            Boolean.valueOf(rewardRule.isEmailRequired()),
            rewardRule.getDataAttributeName(),
            rewardRule.getDataAttributeValue(),
            dataAttributeType,
            rewardRule.getExpression().map(rule -> new RewardRuleExpression(
                rule.getValue(), ExpressionType.valueOf(rule.getType().name()))).orElse(null),
            rewardRule.getRewardEveryXFriendActions(),
            Boolean.valueOf(rewardRule.isRewardCountingBasedOnPartnerUserId()));
    }

    private TransitionRuleResponse toTransitionRuleResponse(TransitionRule transitionRule) {
        return new TransitionRuleResponse(
            transitionRule.getId().getValue(),
            RuleActionType.valueOf(transitionRule.getActionType().name()),
            Boolean.valueOf(transitionRule.getApproveLowQuality()),
            Boolean.valueOf(transitionRule.getApproveHighQuality()),
            Long.valueOf(transitionRule.getTransitionPeriod().toMillis()));
    }

    private QualityRuleConfiguration toQualityRuleConfiguration(QualityRule qualityRule) {
        Set<com.extole.client.rest.campaign.configuration.RuleActionType> restRuleActionTypes = new TreeSet<>();
        for (com.extole.model.entity.campaign.RuleActionType actionType : qualityRule.getActionTypes()) {
            restRuleActionTypes
                .add(com.extole.client.rest.campaign.configuration.RuleActionType.valueOf(actionType.name()));
        }
        return new QualityRuleConfiguration(
            Omissible.of(Id.valueOf(qualityRule.getId().getValue())),
            Boolean.valueOf(qualityRule.getEnabled()),
            com.extole.client.rest.campaign.configuration.QualityRuleType.valueOf(qualityRule.getRuleType().name()),
            restRuleActionTypes,
            qualityRule.getProperties());
    }

    private RewardRuleConfiguration toRewardRuleConfiguration(RewardRule rewardRule) {
        com.extole.client.rest.campaign.configuration.RuleDataMatcherType dataAttributeType = null;
        if (rewardRule.getDataAttributeMatcherType() != null) {
            dataAttributeType = com.extole.client.rest.campaign.configuration.RuleDataMatcherType
                .valueOf(rewardRule.getDataAttributeMatcherType().name());
        }

        return new RewardRuleConfiguration(
            Omissible.of(Id.valueOf(rewardRule.getId().getValue())),
            com.extole.client.rest.campaign.configuration.Rewardee.valueOf(rewardRule.getRewardee().name()),
            rewardRule.getRewardSupplierId().getValue(),
            rewardRule.getReferralsPerReward(),
            rewardRule.getRewardCountLimit(),
            rewardRule.getRewardCountSinceMonth(),
            rewardRule.getRewardCountSinceDays(),
            rewardRule.getRewardValueLimit(),
            rewardRule.getRewardValueSinceMonth(),
            rewardRule.getRewardValueSinceDays(),
            Boolean.valueOf(rewardRule.isUniqueFriendRequired()),
            Boolean.valueOf(rewardRule.isReferralLoopAllowed()),
            rewardRule.getRewardSlots(),
            rewardRule.getMinCartValue(),
            com.extole.client.rest.campaign.configuration.RuleActionType.valueOf(rewardRule.getRuleActionType().name()),
            Boolean.valueOf(rewardRule.isEmailRequired()),
            rewardRule.getDataAttributeName(),
            rewardRule.getDataAttributeValue(),
            dataAttributeType,
            rewardRule.getExpression()
                .map(rule -> new com.extole.client.rest.campaign.configuration.RewardRuleExpression(
                    rule.getValue(),
                    com.extole.client.rest.campaign.configuration.ExpressionType.valueOf(rule.getType().name())))
                .orElse(null),
            rewardRule.getRewardEveryXFriendActions(),
            Boolean.valueOf(rewardRule.isRewardCountingBasedOnPartnerUserId()));
    }

    private TransitionRuleConfiguration toTransitionRuleConfiguration(TransitionRule transitionRule) {
        return new TransitionRuleConfiguration(
            Omissible.of(Id.valueOf(transitionRule.getId().getValue())),
            com.extole.client.rest.campaign.configuration.RuleActionType.valueOf(transitionRule.getActionType().name()),
            Boolean.valueOf(transitionRule.getApproveLowQuality()),
            Boolean.valueOf(transitionRule.getApproveHighQuality()),
            Long.valueOf(transitionRule.getTransitionPeriod().toMillis()));
    }

    public CampaignVersionDescriptionResponse toCampaignVersionDescription(
        CampaignVersionDescription campaignDescription, ZoneId timeZone) {
        Optional<CampaignVersion> parentVersion = campaignDescription.getParentVersion();

        String editorType = campaignDescription.getEditorType() != null
            ? campaignDescription.getEditorType().name()
            : "";

        return new CampaignVersionDescriptionResponse(
            campaignDescription.getVersion().getValue().toString(),
            parentVersion.map(campaignVersion -> campaignVersion.getValue().toString()),
            campaignDescription.getCreatedAt().atZone(timeZone),
            campaignDescription.getPublishedAt().map(date -> date.atZone(timeZone)),
            campaignDescription.getLastPublishedAt().map(date -> date.atZone(timeZone)),
            campaignDescription.getStartDate().map(date -> date.atZone(timeZone)),
            campaignDescription.getStopDate().map(date -> date.atZone(timeZone)),
            campaignDescription.getPauseDate().map(date -> date.atZone(timeZone)),
            campaignDescription.getEndDate().map(date -> date.atZone(timeZone)),
            campaignDescription.getMessage(),
            campaignDescription.getEditorId().getValue(),
            campaignDescription.getEditorId().getValue(),
            editorType);
    }

    private ExternalAbsoluteNames loadExternalAbsoluteNames(ClientAuthorization authorization, Campaign campaign) {
        Map<Id<CampaignComponent>, List<String>> idToNameMappings = Maps.newHashMap();
        List<Campaign> parents = getParents(authorization, campaign);

        parents.forEach(candidate -> componentAbsoluteNameFinder.findAllAbsoluteNamesById(candidate.getComponents())
            .forEach((componentId, absoluteNames) -> {
                idToNameMappings.put(componentId, absoluteNames.stream()
                    .map(absoluteName -> candidate.getName() + ":" + absoluteName)
                    .collect(Collectors.toList()));
            }));
        return () -> unmodifiableMap(idToNameMappings);
    }

    private List<Campaign> getParents(ClientAuthorization authorization, Campaign campaign) {
        List<Campaign> parents = Lists.newArrayList();

        return getParentsRecursively(authorization, campaign, parents);
    }

    private List<Campaign> getParentsRecursively(ClientAuthorization authorization, Campaign campaign,
        List<Campaign> parents) {
        CampaignComponent root = campaign.getComponents()
            .stream()
            .filter(candidate -> candidate.getName().equalsIgnoreCase(CampaignComponent.ROOT))
            .findFirst()
            .get();

        if (!root.getComponentReferences().isEmpty()) {
            for (CampaignComponentReference externalReference : root.getComponentReferences()) {
                Campaign parent = getCampaignByComponentId(authorization, externalReference.getComponentId());
                parents.add(parent);
                return getParentsRecursively(authorization, parent, parents);
            }
        }
        return parents;
    }

    private Campaign getCampaignByComponentId(ClientAuthorization authorization, Id<CampaignComponent> componentId) {
        try {
            return componentService.get(authorization, componentId).getCampaign();
        } catch (AuthorizationException | ComponentNotFoundException e) {
            throw new RuntimeException("Should never happen", e);
        }
    }

    public interface ExternalAbsoluteNames {

        Map<Id<CampaignComponent>, List<String>> get();

    }

}
