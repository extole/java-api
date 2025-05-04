package com.extole.client.rest.impl.campaign.controller.action.incentivize;

import static com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.controller.action.incentivize.BuiltCampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeCreateRequest;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeEndpoints;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeUpdateRequest;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.action.BuiltCampaignControllerActionIncentivizeResponseMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerActionIncentivize;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.IncentivizeActionOverrideType;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivize;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidExpressionException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidLengthException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionNameInvalidValueException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeActionTypeMissingException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeDataInvalidSyntaxException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeOverrideValueInvalidException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeOverrideValueLengthException;
import com.extole.model.service.campaign.controller.action.incentivize.DataNameInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataNameLengthInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataValueInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.action.incentivize.DataValueLengthInvalidIncentivizeActionException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionIncentivizeEndpointsImpl implements CampaignControllerActionIncentivizeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionIncentivizeResponseMapper responseMapper;
    private final BuiltCampaignControllerActionIncentivizeResponseMapper builtResponseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionIncentivizeEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionIncentivizeResponseMapper responseMapper,
        BuiltCampaignControllerActionIncentivizeResponseMapper builtResponseMapper,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.responseMapper = responseMapper;
        this.builtResponseMapper = builtResponseMapper;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public CampaignControllerActionIncentivizeResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionIncentivizeCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionIncentivizeValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
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

        try {
            CampaignControllerActionIncentivizeBuilder actionBuilder =
                campaignBuilder.updateController(controller)
                    .addAction(CampaignControllerActionType.INCENTIVIZE);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getIncentivizeActionType().ifPresent(type -> actionBuilder
                .withIncentivizeActionType(Evaluatables.remapEnum(type, new TypeReference<>() {})));
            request.getOverrides().ifPresent(overrides -> {
                Map<IncentivizeActionOverrideType, String> remapped = new HashMap<>();
                overrides.forEach((key, value) -> remapped
                    .put(IncentivizeActionOverrideType.valueOf(key.name()), value));
                actionBuilder.withOverrides(remapped);
            });
            request.getActionName().ifPresent(actionName -> actionBuilder.withActionName(actionName));
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getReviewStatus().ifPresent(reviewStatus -> actionBuilder.withReviewStatus(
                Evaluatables.remapEnum(reviewStatus, new TypeReference<>() {})));

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
        } catch (DataNameInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_INVALID)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionTypeMissingException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_TYPE_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidValueException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_VALUE)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidExpressionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION)
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
        } catch (CampaignControllerActionIncentivizeDataInvalidSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.EXPRESSION_INVALID_SYNTAX)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", e.getEntityId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .addParameter("description", e.getMessage())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionIncentivizeResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionIncentivizeUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionIncentivizeValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionIncentivize action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getIncentivizeControllerAction(campaign, controllerId, actionId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
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

        try {
            CampaignControllerActionIncentivizeBuilder actionBuilder =
                campaignBuilder.updateController(controller)
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
            request.getIncentivizeActionType().ifPresent(type -> actionBuilder
                .withIncentivizeActionType(Evaluatables.remapEnum(type, new TypeReference<>() {})));
            request.getOverrides().ifPresent(overrides -> {
                Map<IncentivizeActionOverrideType, String> remapped = new HashMap<>();
                overrides.forEach((key, value) -> remapped
                    .put(IncentivizeActionOverrideType.valueOf(key.name()), value));
                actionBuilder.withOverrides(remapped);
            });
            request.getActionName().ifPresent(actionName -> actionBuilder.withActionName(actionName));
            request.getData().ifPresent(data -> actionBuilder.withData(data));
            request.getReviewStatus().ifPresent(reviewStatus -> actionBuilder.withReviewStatus(
                Evaluatables.remapEnum(reviewStatus, new TypeReference<>() {})));

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
        } catch (DataNameInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidIncentivizeActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_INVALID)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeOverrideValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.OVERRIDE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getOverrideType())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidValueException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_VALUE)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeValidationRestException.INCENTIVIZE_ACTION_NAME_INVALID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionIncentivizeActionNameInvalidExpressionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeValidationRestException.class)
                .withErrorCode(INCENTIVIZE_ACTION_NAME_INVALID_EXPRESSION)
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
        } catch (CampaignControllerActionIncentivizeDataInvalidSyntaxException e) {
            throw RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.EXPRESSION_INVALID_SYNTAX)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", e.getEntityId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .addParameter("description", e.getMessage())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionIncentivizeResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionIncentivize action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getIncentivizeControllerAction(campaign, controllerId, actionId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
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

        try {
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
    public CampaignControllerActionIncentivizeResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionIncentivize action =
            campaignStepProvider.getIncentivizeControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public BuiltCampaignControllerActionIncentivizeResponse getBuilt(String accessToken, String campaignId,
        String version, String controllerId, String actionId) throws UserAuthorizationRestException,
        CampaignRestException, CampaignControllerRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignControllerActionIncentivize action =
            campaignStepProvider.getIncentivizeBuiltControllerAction(campaign, controllerId, actionId);

        return builtResponseMapper.toResponse(action, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
