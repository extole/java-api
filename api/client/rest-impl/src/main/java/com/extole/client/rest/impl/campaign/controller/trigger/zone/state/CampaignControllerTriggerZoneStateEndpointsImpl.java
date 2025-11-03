package com.extole.client.rest.impl.campaign.controller.trigger.zone.state;

import java.time.ZoneOffset;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignControllerRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateEndpoints;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateResponse;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignControllerRestExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.client.rest.impl.campaign.controller.trigger.TriggerTypeNotSupportedRestExceptionMapper;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignControllerTriggerZoneState;
import com.extole.model.entity.campaign.CampaignStep;
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
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerDescriptionLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameMissingException;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateBuilder;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateMissingZoneNameAndStepNameException;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateStepNameExpressionLengthException;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateZoneNameExpressionLengthException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerZoneStateEndpointsImpl implements CampaignControllerTriggerZoneStateEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerZoneStateResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerZoneStateEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerZoneStateResponseMapper responseMapper,
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
    public CampaignControllerTriggerZoneStateResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerZoneStateCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerZoneStateValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        BuildCampaignControllerRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignStep step;
        CampaignBuilder campaignBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
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
                .withCause(e)
                .build();
        }

        try {
            CampaignControllerTriggerZoneStateBuilder triggerBuilder = campaignBuilder
                .updateStep(step)
                .addTrigger(CampaignControllerTriggerType.ZONE_STATE);
            request.getTriggerPhase()
                .ifPresent(phase -> triggerBuilder
                    .withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            request.getZoneName()
                .ifPresent(zoneName -> triggerBuilder
                    .withZoneName(zoneName));
            request.getStepName()
                .ifPresent(stepName -> triggerBuilder
                    .withStepName(stepName));
            request.isInvertMappingState()
                .ifPresent(invertMappingState -> triggerBuilder
                    .withInvertMappingState(invertMappingState.booleanValue()));
            request.getName().ifPresent(name -> triggerBuilder.withName(name));
            request.getParentTriggerGroupName()
                .ifPresent(parentTriggerGroupName -> triggerBuilder.withParentTriggerGroupName(parentTriggerGroupName));
            request.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            request.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            request.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(triggerBuilder, componentReferences);
            });
            return responseMapper.toResponse(triggerBuilder.save(), ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.MISSING_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.INVALID_NAME_LENGTH)
                .addParameter("name", e.getName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateZoneNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.ZONE_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateStepNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.STEP_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateMissingZoneNameAndStepNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.MISSING_ZONE_NAME_AND_STEP_NAME)
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "trigger")
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
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (TriggerTypeNotSupportedException e) {
            throw TriggerTypeNotSupportedRestExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignControllerException e) {
            throw BuildCampaignControllerRestExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerZoneStateResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerZoneStateUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerZoneStateValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignControllerTriggerValidationRestException,
        BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerZoneState trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getZoneStateStepTrigger(campaign, controllerId, triggerId);
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
                .withCause(e)
                .build();
        }

        try {
            CampaignControllerTriggerZoneStateBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .updateTrigger(trigger);

            request.getTriggerPhase()
                .ifPresent(phase -> triggerBuilder
                    .withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            request.getZoneName()
                .ifPresent(zoneName -> triggerBuilder.withZoneName(zoneName));
            request.getStepName()
                .ifPresent(stepName -> triggerBuilder.withStepName(stepName));
            request.isInvertMappingState()
                .ifPresent(invertMappingState -> triggerBuilder
                    .withInvertMappingState(invertMappingState.booleanValue()));
            request.getName().ifPresent(name -> triggerBuilder.withName(name));
            request.getParentTriggerGroupName()
                .ifPresent(parentTriggerGroupName -> triggerBuilder.withParentTriggerGroupName(parentTriggerGroupName));
            request.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            request.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            request.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(triggerBuilder, componentReferences);
            });

            return responseMapper.toResponse(triggerBuilder.save(), ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateZoneNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.ZONE_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerZoneStateStepNameExpressionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerZoneStateValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerZoneStateValidationRestException.STEP_NAME_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", e.getExpression())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "trigger")
                .addParameter("referencing_entity", triggerId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.MISSING_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerValidationRestException.INVALID_NAME_LENGTH)
                .addParameter("name", e.getName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignControllerException e) {
            throw BuildCampaignControllerRestExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerZoneStateResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId) throws UserAuthorizationRestException,
        CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerZoneState trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getZoneStateStepTrigger(campaign, controllerId, triggerId);
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
                .withCause(e)
                .build();
        }

        try {
            campaignBuilder.updateStep(step)
                .removeTrigger(trigger)
                .save();

            return responseMapper.toResponse(trigger, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (BuildCampaignControllerException e) {
            throw BuildCampaignControllerRestExceptionMapper.getInstance().map(e, true);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (InvalidComponentReferenceException | CampaignStepBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerZoneStateResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerZoneState trigger =
            campaignStepProvider.getZoneStateStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
