package com.extole.client.rest.impl.campaign.controller.trigger.score;

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
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreEndpoints;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreResponse;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerTriggerScore;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
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
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreBuilder;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreCauseEventNameLengthException;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreIllegalCharacterInCauseEventNameException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerScoreEndpointsImpl implements CampaignControllerTriggerScoreEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerScoreResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerScoreEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerScoreResponseMapper responseMapper,
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
    public CampaignControllerTriggerScoreResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerScoreCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerScoreValidationRestException, CampaignControllerTriggerValidationRestException,
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
        if (request.getCauseEventName() == null) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerScoreValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerScoreValidationRestException.MISSING_CAUSE_EVENT_NAME)
                .build();
        }
        try {
            CampaignControllerTriggerScoreBuilder triggerBuilder = campaignBuilder
                .updateStep(step)
                .addTrigger(CampaignControllerTriggerType.SCORE);
            request.getTriggerPhase()
                .ifPresent(
                    phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            triggerBuilder
                .withCauseEventName(request.getCauseEventName());
            if (request.getScoreResult() != null) {
                triggerBuilder.withScoreResult(request.getScoreResult().name());
            }
            if (request.getChannel() != null) {
                triggerBuilder.withChannel(request.getChannel().toLowerCase());
            }
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
        } catch (CampaignControllerTriggerScoreCauseEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerScoreValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerScoreValidationRestException.CAUSE_EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("cause_event_name", e.getCauseEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerScoreIllegalCharacterInCauseEventNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerScoreValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerScoreValidationRestException.CAUSE_EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("cause_event_name", e.getCauseEventName())
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
    public CampaignControllerTriggerScoreResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerScoreUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerScore trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getScoreStepTrigger(campaign, controllerId, triggerId);
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
            CampaignControllerTriggerScoreBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .updateTrigger(trigger);

            request.getTriggerPhase()
                .ifPresent(
                    phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));

            if (request.getScoreResult() != null) {
                triggerBuilder.withScoreResult(request.getScoreResult());
            }
            if (request.getCauseEventName() != null) {
                triggerBuilder.withCauseEventName(request.getCauseEventName());
            }
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
    public CampaignControllerTriggerScoreResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId) throws UserAuthorizationRestException,
        CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerScore trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getScoreStepTrigger(campaign, controllerId, triggerId);
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
        } catch (BuildCampaignControllerException e) {
            throw BuildCampaignControllerRestExceptionMapper.getInstance().map(e, true);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException | CampaignStepBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerScoreResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerScore trigger =
            campaignStepProvider.getScoreStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
