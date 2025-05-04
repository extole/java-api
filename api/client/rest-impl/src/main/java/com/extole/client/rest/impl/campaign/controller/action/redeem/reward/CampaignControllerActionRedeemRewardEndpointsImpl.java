package com.extole.client.rest.impl.campaign.controller.action.redeem.reward;

import java.time.ZoneOffset;
import java.util.List;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardCreateRequest;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardEndpoints;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardResponse;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardUpdateRequest;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.CampaignControllerActionRedeemReward;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardBuilder;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardInvalidSpelExpressionException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardMissingDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardMissingDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardMissingRewardIdException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardUndefinedSpelExpressionException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionRedeemRewardEndpointsImpl
    implements CampaignControllerActionRedeemRewardEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionRedeemRewardResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionRedeemRewardEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionRedeemRewardResponseMapper responseMapper,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.responseMapper = responseMapper;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignControllerActionRedeemRewardResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionRedeemRewardCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionRedeemRewardValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionRedeemRewardBuilder actionBuilder = campaignBuilder
                .updateController(controller)
                .addAction(CampaignControllerActionType.REDEEM_REWARD);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getRewardId().ifPresent(rewardId -> actionBuilder.withRewardId(rewardId));
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getPartnerEventId().ifPresent(partnerEventId -> actionBuilder.withPartnerEventId(partnerEventId));
            request.getEventTime().ifPresent(value -> actionBuilder.withEventTime(value));

            return responseMapper.toResponse(actionBuilder.save(), ZoneOffset.UTC);
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
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.UNDEFINED_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardMissingRewardIdException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.MISSING_REWARD_ID)
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionRedeemRewardResponse get(String accessToken,
        String campaignId,
        String version,
        String controllerId,
        String actionId) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionRedeemReward action =
            campaignStepProvider.getRedeemRewardControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public CampaignControllerActionRedeemRewardResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionRedeemReward action =
                campaignStepProvider.getRedeemRewardControllerAction(campaign, controllerId, actionId);

            campaignBuilder.updateController(controller)
                .removeAction(action)
                .save();

            return responseMapper.toResponse(action, ZoneOffset.UTC);
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
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException
            | InvalidComponentReferenceException | StepDataBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionRedeemRewardResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionRedeemRewardUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionRedeemRewardValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            CampaignControllerActionRedeemReward action =
                campaignStepProvider.getRedeemRewardControllerAction(campaign, controllerId, actionId);

            CampaignControllerActionRedeemRewardBuilder actionBuilder = campaignBuilder
                .updateController(controller)
                .updateAction(action);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));

            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getRewardId().ifPresent(rewardId -> actionBuilder.withRewardId(rewardId));
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getPartnerEventId().ifPresent(item -> actionBuilder.withPartnerEventId(item));
            request.getEventTime().ifPresent(value -> actionBuilder.withEventTime(value));

            return responseMapper.toResponse(actionBuilder.save(), ZoneOffset.UTC);
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
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardUndefinedSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.UNDEFINED_EXPRESSION)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardMissingDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getExpression())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardInvalidSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.INVALID_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionRedeemRewardMissingRewardIdException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionRedeemRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionRedeemRewardValidationRestException.MISSING_REWARD_ID)
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", actionId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion) throws CampaignRestException {
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }
        return campaignBuilder;
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
