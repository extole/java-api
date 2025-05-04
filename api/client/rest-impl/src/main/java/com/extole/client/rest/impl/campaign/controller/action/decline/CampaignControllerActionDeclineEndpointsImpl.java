package com.extole.client.rest.impl.campaign.controller.action.decline;

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
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineCreateRequest;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineEndpoints;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineResponse;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineUpdateRequest;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerActionDecline;
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
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineBuilder;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineCauseTypeLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineEventTypeLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineLegacyActionIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineNoteLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePartnerEventIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePollingIdLengthException;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclinePollingNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionDeclineEndpointsImpl implements CampaignControllerActionDeclineEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionDeclineResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionDeclineEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionDeclineResponseMapper responseMapper,
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
    public CampaignControllerActionDeclineResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionDeclineCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDeclineValidationRestException, CampaignComponentValidationRestException,
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
            CampaignControllerActionDeclineBuilder actionBuilder = campaignBuilder.updateController(controller)
                .addAction(CampaignControllerActionType.DECLINE);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));

            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getLegacyActionId().ifPresent(legacyActionId -> actionBuilder.withLegacyActionId(legacyActionId));
            request.getPartnerEventId().ifPresent(partnerEventId -> actionBuilder.withPartnerEventId(partnerEventId));
            request.getEventType().ifPresent(eventType -> actionBuilder.withEventType(eventType));
            request.getNote().ifPresent(note -> actionBuilder.withNote(note));
            request.getCauseType().ifPresent(causeType -> actionBuilder.withCauseType(causeType));
            request.getPollingId().ifPresent(pollingId -> actionBuilder.withPollingId(pollingId));
            request.getPollingName().ifPresent(pollingName -> actionBuilder.withPollingName(pollingName));

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
        } catch (CampaignControllerActionDeclineLegacyActionIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.LEGACY_ACTION_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineEventTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.EVENT_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePartnerEventIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.PARTNER_EVENT_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineNoteLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.NOTE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineCauseTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.CAUSE_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_NAME_LENGTH)
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
    public CampaignControllerActionDeclineResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionDeclineUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDeclineValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionDecline action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getDeclineControllerAction(campaign, controllerId, actionId);
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
            CampaignControllerActionDeclineBuilder actionBuilder =
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
            request.getLegacyActionId().ifPresent(legacyActionId -> actionBuilder.withLegacyActionId(legacyActionId));
            request.getPartnerEventId().ifPresent(partnerEventId -> actionBuilder.withPartnerEventId(partnerEventId));
            request.getEventType().ifPresent(eventType -> actionBuilder.withEventType(eventType));
            request.getNote().ifPresent(note -> actionBuilder.withNote(note));
            request.getCauseType().ifPresent(causeType -> actionBuilder.withCauseType(causeType));
            request.getPollingId().ifPresent(pollingId -> actionBuilder.withPollingId(pollingId));
            request.getPollingName().ifPresent(pollingName -> actionBuilder.withPollingName(pollingName));

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
        } catch (CampaignControllerActionDeclineLegacyActionIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.LEGACY_ACTION_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineEventTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.EVENT_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePartnerEventIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.PARTNER_EVENT_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineNoteLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.NOTE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclineCauseTypeLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.CAUSE_TYPE_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingIdLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_ID_LENGTH)
                .withCause(e)
                .build();
        } catch (CampaignControllerActionDeclinePollingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionDeclineValidationRestException.class)
                .withErrorCode(CampaignControllerActionDeclineValidationRestException.POLLING_NAME_LENGTH)
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

    @Override
    public CampaignControllerActionDeclineResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionDecline action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getDeclineControllerAction(campaign, controllerId, actionId);
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
    public CampaignControllerActionDeclineResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionDecline action =
            campaignStepProvider.getDeclineControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
