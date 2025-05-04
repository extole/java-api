package com.extole.client.rest.impl.campaign.controller.action.signal;

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
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalCreateRequest;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalEndpoints;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalResponse;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalUpdateRequest;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerActionSignal;
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
import com.extole.model.service.campaign.controller.action.signal.CampaignControllerActionSignalBuilder;
import com.extole.model.service.campaign.controller.action.signal.CampaignControllerActionSignalDataInvalidSyntaxException;
import com.extole.model.service.campaign.controller.action.signal.DataNameInvalidSignalActionException;
import com.extole.model.service.campaign.controller.action.signal.DataNameLengthInvalidSignalActionException;
import com.extole.model.service.campaign.controller.action.signal.DataValueInvalidSignalActionException;
import com.extole.model.service.campaign.controller.action.signal.DataValueLengthInvalidSignalActionException;
import com.extole.model.service.campaign.controller.action.signal.NameLengthInvalidSignalActionException;
import com.extole.model.service.campaign.controller.action.signal.SignalPollingIdLengthInvalidSignalActionException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionSignalEndpointsImpl implements CampaignControllerActionSignalEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionSignalResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionSignalEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionSignalResponseMapper responseMapper,
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
    public CampaignControllerActionSignalResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionSignalCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionSignalValidationRestException, CampaignComponentValidationRestException,
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
            CampaignControllerActionSignalBuilder actionBuilder = campaignBuilder.updateController(controller)
                .addAction(CampaignControllerActionType.SIGNAL);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getSignalPollingId()
                .ifPresent(signalPollingId -> actionBuilder.withSignalPollingId(signalPollingId));
            request.getName().ifPresent(name -> actionBuilder.withName(name));
            request.getData().ifPresent(data -> actionBuilder.withData(data));

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
        } catch (DataNameInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (SignalPollingIdLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.SIGNAL_POLLING_ID_LENGTH_INVALID)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (NameLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.NAME_LENGTH_INVALID)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
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
        } catch (CampaignControllerActionSignalDataInvalidSyntaxException e) {
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
    public CampaignControllerActionSignalResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionSignalUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionSignalValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionSignal action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getSignalControllerAction(campaign, controllerId, actionId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        try {
            CampaignControllerActionSignalBuilder actionBuilder =
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
            request.getSignalPollingId()
                .ifPresent(signalPollingId -> actionBuilder.withSignalPollingId(signalPollingId));
            request.getName().ifPresent(name -> actionBuilder.withName(name));
            request.getData().ifPresent(data -> {
                if (data.isEmpty()) {
                    actionBuilder.clearData();
                } else {
                    actionBuilder.withData(data);
                }
            });

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
        } catch (DataNameInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (DataValueInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_VALUE_INVALID)
                .addParameter("name", e.getDataName())
                .withCause(e)
                .build();
        } catch (DataNameLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_NAME_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (DataValueLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.DATA_VALUE_LENGTH_INVALID)
                .addParameter("name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (SignalPollingIdLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.SIGNAL_POLLING_ID_LENGTH_INVALID)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (NameLengthInvalidSignalActionException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionSignalValidationRestException.class)
                .withErrorCode(CampaignControllerActionSignalValidationRestException.NAME_LENGTH_INVALID)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
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
        } catch (CampaignControllerActionSignalDataInvalidSyntaxException e) {
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
    public CampaignControllerActionSignalResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionSignal action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getSignalControllerAction(campaign, controllerId, actionId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
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
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public CampaignControllerActionSignalResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionSignal action =
            campaignStepProvider.getSignalControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
