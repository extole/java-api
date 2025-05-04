package com.extole.client.rest.impl.campaign.controller.action.earn.reward;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardCreateRequest;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardEndpoints;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardResponse;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardUpdateRequest;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerActionEarnReward;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
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
import com.extole.model.service.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardBuilder;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardEmptyTagException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardInvalidDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardInvalidDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardInvalidTagException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardMissingDataAttributeNameException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardMissingDataAttributeValueException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardMissingRewardNameException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardMissingTagsException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardRewardInvalidSupplierIdException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardRewardNameTooLongException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardTagsInvalidLengthException;
import com.extole.model.service.campaign.controller.action.earn.reward.exception.CampaignControllerActionEarnRewardTooLongSpelExpressionException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionEarnRewardEndpointsImpl
    implements CampaignControllerActionEarnRewardEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionEarnRewardResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionEarnRewardEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionEarnRewardResponseMapper responseMapper,
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
    public CampaignControllerActionEarnRewardResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionEarnRewardCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionEarnRewardValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionEarnRewardBuilder actionBuilder = campaignBuilder
                .updateController(controller)
                .addAction(CampaignControllerActionType.EARN_REWARD);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(actionBuilder::withEnabled);

            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getRewardName().ifPresent(actionBuilder::withRewardName);
            request.getRewardSupplierId().ifPresent(actionBuilder::withRewardSupplierId);
            request.getTags().ifPresent(actionBuilder::withTags);

            if (!request.getValueOfEventBeingRewarded().isOmitted()) {
                if (!request.getData().isOmitted()) {
                    Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                        RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data =
                            Maps.newHashMap(request.getData().getValue());
                    if (!data.containsKey("earned_event_value")) {
                        data.put("earned_event_value", request.getValueOfEventBeingRewarded().getValue());
                    }
                    actionBuilder.withData(data);
                } else {
                    actionBuilder.withData(
                        ImmutableMap.of("earned_event_value", request.getValueOfEventBeingRewarded().getValue()));
                }
            } else {
                request.getData().ifPresent(actionBuilder::withData);
            }

            request.getEventTime().ifPresent(value -> actionBuilder.withEventTime(value));
            request.getRewardActionId().ifPresent(value -> actionBuilder.withRewardActionId(value));

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
        } catch (CampaignControllerActionEarnRewardRewardNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.REWARD_NAME_TOO_LONG)
                .addParameter("reward_name", request.getRewardName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingRewardNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.MISSING_REWARD_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardEmptyTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.EMPTY_TAG)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingTagsException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAGS)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardTagsInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAGS_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
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
        } catch (CampaignControllerActionEarnRewardRewardInvalidSupplierIdException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionEarnRewardResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionEarnReward action =
            campaignStepProvider.getEarnRewardControllerAction(campaign, controllerId, actionId);
        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public CampaignControllerActionEarnRewardResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionEarnRewardUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionEarnRewardValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignController controller = campaignStepProvider.getController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            CampaignControllerActionEarnReward action =
                campaignStepProvider.getEarnRewardControllerAction(campaign, controllerId, actionId);

            CampaignControllerActionEarnRewardBuilder actionBuilder = campaignBuilder
                .updateController(controller)
                .updateAction(action);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(actionBuilder::withEnabled);
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getRewardName().ifPresent(actionBuilder::withRewardName);
            request.getRewardSupplierId().ifPresent(actionBuilder::withRewardSupplierId);
            request.getTags().ifPresent(actionBuilder::withTags);

            if (!request.getValueOfEventBeingRewarded().isOmitted()) {
                if (!request.getData().isOmitted()) {
                    Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                        RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data =
                            Maps.newHashMap(request.getData().getValue());
                    if (!data.containsKey("earned_event_value")) {
                        data.put("earned_event_value", request.getValueOfEventBeingRewarded().getValue());
                    }
                    actionBuilder.withData(data);
                } else {
                    Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                        RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data =
                            Maps.newHashMap(action.getData());
                    data.put("earned_event_value", request.getValueOfEventBeingRewarded().getValue());
                    actionBuilder.withData(data);
                }
            } else {
                request.getData().ifPresent(actionBuilder::withData);
            }

            request.getEventTime().ifPresent(value -> actionBuilder.withEventTime(value));
            request.getRewardActionId().ifPresent(value -> actionBuilder.withRewardActionId(value));

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
        } catch (CampaignControllerActionEarnRewardRewardNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.REWARD_NAME_TOO_LONG)
                .addParameter("reward_name", request.getRewardName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingRewardNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.MISSING_REWARD_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardTooLongSpelExpressionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.TOO_LONG_EXPRESSION)
                .addParameter("expression", e.getEvaluatable())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardEmptyTagException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.EMPTY_TAG)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingTagsException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAGS)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardTagsInvalidLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.INVALID_TAGS_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardMissingDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidDataAttributeNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionEarnRewardInvalidDataAttributeValueException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionEarnRewardValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getAttributeName())
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
        } catch (CampaignControllerActionEarnRewardRewardInvalidSupplierIdException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionEarnRewardValidationRestException.class)
                .withErrorCode(CampaignControllerActionEarnRewardValidationRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionEarnRewardResponse delete(String accessToken,
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
            CampaignControllerActionEarnReward action =
                campaignStepProvider.getEarnRewardControllerAction(campaign, controllerId, actionId);

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

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion)
        throws CampaignRestException {
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
