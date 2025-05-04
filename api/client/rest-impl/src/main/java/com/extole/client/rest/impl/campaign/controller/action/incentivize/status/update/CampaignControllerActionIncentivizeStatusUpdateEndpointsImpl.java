package com.extole.client.rest.impl.campaign.controller.action.incentivize.status.update;

import java.time.ZoneOffset;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.controller.action.incentivize.status.update.BuiltCampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateCreateRequest;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateEndpoints;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateUpdateRequest;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.action.BuiltCampaignControllerActionIncentivizeStatusUpdateResponseMapper;
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
import com.extole.model.entity.campaign.CampaignControllerActionIncentivizeStatusUpdate;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivizeStatusUpdate;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeDataInvalidSyntaxException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateDataInvalidSyntaxException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataNameInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataNameLengthInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataValueInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.DataValueLengthInvalidIncentivizeStatusUpdateActionException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionIncentivizeStatusUpdateEndpointsImpl
    implements CampaignControllerActionIncentivizeStatusUpdateEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionIncentivizeStatusUpdateResponseMapper responseMapper;
    private final BuiltCampaignControllerActionIncentivizeStatusUpdateResponseMapper builtResponseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionIncentivizeStatusUpdateEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionIncentivizeStatusUpdateResponseMapper responseMapper,
        BuiltCampaignControllerActionIncentivizeStatusUpdateResponseMapper builtResponseMapper,
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
    public CampaignControllerActionIncentivizeStatusUpdateResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionIncentivizeStatusUpdateCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException {
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
            CampaignControllerActionIncentivizeStatusUpdateBuilder actionBuilder =
                campaignBuilder.updateController(controller)
                    .addAction(CampaignControllerActionType.INCENTIVIZE_STATUS_UPDATE);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getLegacyActionId().ifPresent(legacyActionId -> actionBuilder
                .withLegacyActionId(legacyActionId));
            request.getEventType().ifPresent(eventType -> actionBuilder.withEventType(Evaluatables.remapNestedOptional(
                eventType, new TypeReference<>() {})));
            request.getPartnerEventId().ifPresent(partnerEventId -> actionBuilder.withPartnerEventId(partnerEventId));
            request.getMessage().ifPresent(message -> actionBuilder.withMessage(message));
            request.getReviewStatus().ifPresent(reviewStatus -> actionBuilder
                .withReviewStatus(reviewStatus));
            request.isMoveToPending()
                .ifPresent(moveToPending -> actionBuilder.withMoveToPending(moveToPending));
            request.getData().ifPresent(data -> actionBuilder.withData(data));

            return mapToResponse(actionBuilder.save());
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
        } catch (CampaignControllerActionIncentivizeStatusUpdateDataInvalidSyntaxException e) {
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
        } catch (DataNameInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionIncentivizeStatusUpdateResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionIncentivizeStatusUpdateUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignControllerActionIncentivizeStatusUpdateValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionIncentivizeStatusUpdate action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getIncentivizeStatusUpdateControllerAction(campaign, controllerId, actionId);
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
            CampaignControllerActionIncentivizeStatusUpdateBuilder actionBuilder =
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
            request.getLegacyActionId().ifPresent(legacyActionId -> actionBuilder
                .withLegacyActionId(legacyActionId));
            request.getEventType().ifPresent(eventType -> actionBuilder.withEventType(Evaluatables.remapNestedOptional(
                eventType, new TypeReference<>() {})));
            request.getMessage().ifPresent(message -> actionBuilder.withMessage(message));
            request.getReviewStatus()
                .ifPresent(reviewStatus -> actionBuilder
                    .withReviewStatus(reviewStatus));
            request.getPartnerEventId().ifPresent(partnerEventId -> actionBuilder.withPartnerEventId(partnerEventId));
            request.isMoveToPending()
                .ifPresent(moveToPending -> actionBuilder.withMoveToPending(moveToPending));
            request.getData().ifPresent(data -> actionBuilder.withData(data));

            return mapToResponse(actionBuilder.save());
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
        } catch (DataNameInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidIncentivizeStatusUpdateActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionIncentivizeStatusUpdateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionIncentivizeStatusUpdateValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionIncentivizeStatusUpdateResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionIncentivizeStatusUpdate action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getIncentivizeStatusUpdateControllerAction(campaign, controllerId, actionId);
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

            return mapToResponse(action);
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
    public CampaignControllerActionIncentivizeStatusUpdateResponse get(String accessToken,
        String campaignId,
        String version,
        String controllerId,
        String actionId) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionIncentivizeStatusUpdate action =
            campaignStepProvider.getIncentivizeStatusUpdateControllerAction(campaign, controllerId, actionId);

        return mapToResponse(action);
    }

    @Override
    public BuiltCampaignControllerActionIncentivizeStatusUpdateResponse getBuilt(String accessToken,
        String campaignId,
        String version,
        String controllerId,
        String actionId) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignControllerActionIncentivizeStatusUpdate action =
            campaignStepProvider.getIncentivizeStatusUpdateBuiltControllerAction(campaign, controllerId, actionId);

        return mapToBuiltResponse(action);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private CampaignControllerActionIncentivizeStatusUpdateResponse mapToResponse(
        CampaignControllerActionIncentivizeStatusUpdate action) {
        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    private BuiltCampaignControllerActionIncentivizeStatusUpdateResponse mapToBuiltResponse(
        BuiltCampaignControllerActionIncentivizeStatusUpdate action) {
        return builtResponseMapper.toResponse(action, ZoneOffset.UTC);
    }

}
