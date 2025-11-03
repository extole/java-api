package com.extole.client.rest.impl.campaign.incentive.transition.rule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.incentive.CampaignIncentiveRestException;
import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.client.rest.campaign.incentive.transition.rule.CampaignTransitionRuleEndpoints;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleCreationRequest;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleCreationValidationRestException;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleRequest;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleResponse;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleRestException;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.TransitionRule;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.IllegalValueInTransitionPeriodException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.campaign.transition.rule.TransitionRuleBuilder;
import com.extole.model.service.campaign.transition.rule.TransitionRuleNotFoundException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignTransitionRuleEndpointsImpl implements CampaignTransitionRuleEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;

    @Autowired
    public CampaignTransitionRuleEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider) {
        this.authorizationProvider = authorizationProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
    }

    @Override
    public List<TransitionRuleResponse> list(String accessToken, String campaignId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignService.getPublishedOrDraftAnyStateCampaign(userAuthorization,
                Id.valueOf(campaignId));
            List<TransitionRuleResponse> transitionRuleResponse = new ArrayList<>();
            for (TransitionRule transitionRule : campaign.getTransitionRules()) {
                transitionRuleResponse.add(transitionRuleToResponse(transitionRule));
            }
            return transitionRuleResponse;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        }
    }

    @Override
    public TransitionRuleResponse get(String accessToken, String campaignId, String transitionRuleId)
        throws UserAuthorizationRestException, TransitionRuleRestException,
        CampaignIncentiveRestException {

        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignService.getPublishedOrDraftAnyStateCampaign(userAuthorization,
                Id.valueOf(campaignId));
            TransitionRule transitionRule = campaign.getTransitionRules().stream()
                .filter(incentiveTransitionRule -> incentiveTransitionRule.getId().getValue()
                    .equals(transitionRuleId))
                .findFirst().orElseThrow(() -> new TransitionRuleNotFoundException(
                    "Campaign '" + campaignId + "' has no reward rule with id '" + transitionRuleId + "'."));
            return transitionRuleToResponse(transitionRule);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (TransitionRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleRestException.class)
                .withErrorCode(TransitionRuleRestException.TRANSITION_RULE_NOT_FOUND)
                .addParameter("campaign_id", campaignId)
                .addParameter("transition_rule_id", transitionRuleId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public TransitionRuleResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        TransitionRuleCreationRequest transitionRule)
        throws UserAuthorizationRestException, CampaignRestException, TransitionRuleCreationValidationRestException,
        TransitionRuleValidationRestException, CampaignIncentiveRestException, BuildCampaignRestException,
        CampaignUpdateRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder;
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            if (transitionRule.getActionType() == null) {
                throw RestExceptionBuilder.newBuilder(TransitionRuleCreationValidationRestException.class)
                    .withErrorCode(TransitionRuleCreationValidationRestException.ACTION_TYPE_REQUIRED)
                    .addParameter("action_type", transitionRule.getActionType()).build();
            }
            if (transitionRule.getTransitionPeriodMilliseconds() == null) {
                throw RestExceptionBuilder.newBuilder(TransitionRuleCreationValidationRestException.class)
                    .withErrorCode(
                        TransitionRuleCreationValidationRestException.TRANSITION_PERIOD_MILLISECONDS_REQUIRED)
                    .addParameter("transition_period_milliseconds", transitionRule.getTransitionPeriodMilliseconds())
                    .build();
            }
            Duration transitionPeriod = Duration.ofMillis(transitionRule.getTransitionPeriodMilliseconds().longValue());
            TransitionRuleBuilder transitionRuleBuilder = campaignBuilder.addTransitionRuleBuilder(
                com.extole.model.entity.campaign.RuleActionType.valueOf(transitionRule.getActionType().name()))
                .withTransitionPeriod(transitionPeriod);
            if (transitionRule.getApproveHighQuality() != null) {
                transitionRuleBuilder.withApproveHighQuality(transitionRule.getApproveHighQuality().booleanValue());
            }
            if (transitionRule.getApproveLowQuality() != null) {
                transitionRuleBuilder.withApproveLowQuality(transitionRule.getApproveLowQuality().booleanValue());
            }

            return transitionRuleToResponse(transitionRuleBuilder.save());

        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (TransitionRuleAlreadyExistsForActionType e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleValidationRestException.class)
                .withErrorCode(TransitionRuleValidationRestException.ACTION_TYPE_ALREADY_EXISTS)
                .addParameter("action_type", transitionRule.getActionType()).withCause(e).build();
        } catch (IllegalValueInTransitionPeriodException e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleValidationRestException.class)
                .withErrorCode(TransitionRuleValidationRestException.TRANSITION_PERIOD_MILLISECONDS_INVALID)
                .addParameter("transition_period_milliseconds", transitionRule.getTransitionPeriodMilliseconds())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public TransitionRuleResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String transitionRuleId,
        TransitionRuleRequest transitionRule)
        throws UserAuthorizationRestException, CampaignRestException, TransitionRuleRestException,
        TransitionRuleValidationRestException, CampaignIncentiveRestException, BuildCampaignRestException,
        CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder;
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            TransitionRuleBuilder transitionRuleBuilder =
                campaignBuilder.updateTransitionRule(Id.valueOf(transitionRuleId));
            if (transitionRule.getActionType() != null) {
                transitionRuleBuilder.withActionType(com.extole.model.entity.campaign.RuleActionType
                    .valueOf(transitionRule.getActionType().name()));
            }
            if (transitionRule.getApproveHighQuality() != null) {
                transitionRuleBuilder.withApproveHighQuality(transitionRule.getApproveHighQuality().booleanValue());
            }
            if (transitionRule.getApproveLowQuality() != null) {
                transitionRuleBuilder.withApproveLowQuality(transitionRule.getApproveLowQuality().booleanValue());
            }
            if (transitionRule.getTransitionPeriodMilliseconds() != null) {
                transitionRuleBuilder.withTransitionPeriod(
                    Duration.ofMillis(transitionRule.getTransitionPeriodMilliseconds().longValue()));
            }
            return transitionRuleToResponse(transitionRuleBuilder.save());
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (TransitionRuleAlreadyExistsForActionType e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleValidationRestException.class)
                .withErrorCode(TransitionRuleValidationRestException.ACTION_TYPE_ALREADY_EXISTS)
                .addParameter("action_type", transitionRule.getActionType()).withCause(e).build();
        } catch (IllegalValueInTransitionPeriodException e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleValidationRestException.class)
                .withErrorCode(TransitionRuleValidationRestException.TRANSITION_PERIOD_MILLISECONDS_INVALID)
                .addParameter("transition_period_milliseconds", transitionRule.getTransitionPeriodMilliseconds())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (TransitionRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleRestException.class)
                .withErrorCode(TransitionRuleRestException.TRANSITION_RULE_NOT_FOUND)
                .addParameter("transition_rule_id", transitionRuleId)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public TransitionRuleResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String transitionRuleId)
        throws UserAuthorizationRestException, CampaignRestException, TransitionRuleRestException,
        CampaignIncentiveRestException, BuildCampaignRestException, CampaignUpdateRestException {

        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            TransitionRule transitionRule = campaignService
                .getPublishedOrDraftAnyStateCampaign(userAuthorization, Id.valueOf(campaignId)).getTransitionRules()
                .stream().filter(rule -> rule.getId().getValue().equals(transitionRuleId)).findFirst()
                .orElseThrow(() -> new TransitionRuleNotFoundException("id " + transitionRuleId));
            CampaignBuilder campaignBuilder = campaignService.editCampaign(userAuthorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
            campaignBuilder
                .removeTransitionRule(Id.valueOf(transitionRuleId))
                .save();

            return transitionRuleToResponse(transitionRule);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (TransitionRuleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(TransitionRuleRestException.class)
                .withErrorCode(TransitionRuleRestException.TRANSITION_RULE_NOT_FOUND)
                .addParameter("campaign_id", campaignId)
                .addParameter("transition_rule_id", transitionRuleId)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException
            | StepDataBuildException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public void deleteAllTransitionRules(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            campaignBuilder
                .removeTransitionRules()
                .save();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignLabelMissingNameException | CampaignLabelDuplicateNameException
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | CampaignComponentFacetsNotFoundException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException
            | CampaignScheduleException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private TransitionRuleResponse transitionRuleToResponse(TransitionRule transitionRule) {
        return new TransitionRuleResponse(transitionRule.getId().getValue(),
            RuleActionType.valueOf(transitionRule.getActionType().name()),
            Boolean.valueOf(transitionRule.getApproveLowQuality()),
            Boolean.valueOf(transitionRule.getApproveHighQuality()),
            Long.valueOf(transitionRule.getTransitionPeriod().toMillis()));
    }

}
