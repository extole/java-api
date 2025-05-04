package com.extole.client.rest.impl.campaign.controller.action.data.intelligence;

import static com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE;
import static com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceValidationRestException.INVALID_EVENT_NAME;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceEndpoints;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceRequest;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceResponse;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
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
import com.extole.model.entity.campaign.CampaignControllerActionDataIntelligence;
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
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceBuilder;
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceEventNameLengthException;
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionDataIntelligenceEndpointsImpl
    implements CampaignControllerActionDataIntelligenceEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionDataIntelligenceResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerActionDataIntelligenceEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionDataIntelligenceResponseMapper responseMapper,
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
    public CampaignControllerActionDataIntelligenceResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionDataIntelligenceRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDataIntelligenceValidationRestException, CampaignComponentValidationRestException,
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

        CampaignControllerActionDataIntelligenceBuilder actionBuilder =
            campaignBuilder.updateController(controller)
                .addAction(CampaignControllerActionType.DATA_INTELLIGENCE);

        try {
            request.getQuality()
                .ifPresent(
                    quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled()
                .ifPresent(enabled -> actionBuilder.withEnabled(enabled));

            request.getComponentIds()
                .ifPresent(componentIds -> handleComponentIds(actionBuilder, componentIds));
            request.getComponentReferences().ifPresent(componentReferences -> componentReferenceRequestMapper
                .handleComponentReferences(actionBuilder, componentReferences));
            request.getIntelligenceProviderType()
                .ifPresent(providerType -> actionBuilder
                    .withIntelligenceProvider(Evaluatables.remapEnum(providerType, new TypeReference<>() {})));
            request.getEventName()
                .ifPresent(eventName -> actionBuilder.withEventName(eventName));
            request.getProfileRiskUpdateInterval()
                .ifPresent(profileRiskUpdateInterval -> actionBuilder
                    .withProfileRiskUpdateInterval(profileRiskUpdateInterval));

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
        } catch (CampaignControllerActionDataIntelligenceIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionDataIntelligenceValidationRestException.class)
                .withErrorCode(INVALID_EVENT_NAME)
                .addParameter("event_name", request.getEventName())
                .withCause(e).build();
        } catch (CampaignControllerActionDataIntelligenceEventNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionDataIntelligenceValidationRestException.class)
                .withErrorCode(EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", request.getEventName()).withCause(e).build();
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
    public List<CampaignControllerActionDataIntelligenceResponse> get(String accessToken, String campaignId,
        String version, String controllerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        List<CampaignControllerActionDataIntelligence> dataIntelligenceAction =
            campaignStepProvider.getDataIntelligenceControllerActions(campaign, controllerId);
        List<CampaignControllerActionDataIntelligenceResponse> response = new ArrayList<>();
        for (CampaignControllerActionDataIntelligence action : dataIntelligenceAction) {
            CampaignControllerActionDataIntelligenceResponse campaignControllerActionDataIntelligenceResponse =
                responseMapper.toResponse(action, ZoneOffset.UTC);
            response.add(campaignControllerActionDataIntelligenceResponse);
        }

        return response;
    }

    @Override
    public CampaignControllerActionDataIntelligenceResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionDataIntelligence action =
            campaignStepProvider.getDataIntelligenceControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public CampaignControllerActionDataIntelligenceResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionDataIntelligenceRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDataIntelligenceValidationRestException, CampaignComponentValidationRestException,
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

        CampaignControllerActionDataIntelligence action =
            campaignStepProvider.getDataIntelligenceControllerAction(campaign, controllerId, actionId);

        CampaignControllerActionDataIntelligenceBuilder actionBuilder =
            campaignBuilder.updateController(controller)
                .updateAction(action);

        try {
            request.getQuality()
                .ifPresent(
                    quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled()
                .ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds()
                .ifPresent(componentIds -> {
                    handleComponentIds(actionBuilder, componentIds);
                });
            request.getComponentReferences()
                .ifPresent(componentReferences -> {
                    componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
                });
            request.getIntelligenceProviderType()
                .ifPresent(providerType -> actionBuilder
                    .withIntelligenceProvider(Evaluatables.remapEnum(providerType, new TypeReference<>() {})));
            request.getEventName()
                .ifPresent(eventName -> actionBuilder.withEventName(eventName));
            request.getProfileRiskUpdateInterval()
                .ifPresent(profileRiskUpdateInterval -> actionBuilder
                    .withProfileRiskUpdateInterval(profileRiskUpdateInterval));

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
        } catch (CampaignControllerActionDataIntelligenceIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionDataIntelligenceValidationRestException.class)
                .withErrorCode(INVALID_EVENT_NAME)
                .addParameter("event_name", request.getEventName())
                .withCause(e).build();
        } catch (CampaignControllerActionDataIntelligenceEventNameLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionDataIntelligenceValidationRestException.class)
                .withErrorCode(EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", request.getEventName()).withCause(e).build();
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
    public CampaignControllerActionDataIntelligenceResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionDataIntelligence action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getDataIntelligenceControllerAction(campaign, controllerId, actionId);
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

            return responseMapper.toResponse(action,
                ZoneOffset.UTC);
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

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
