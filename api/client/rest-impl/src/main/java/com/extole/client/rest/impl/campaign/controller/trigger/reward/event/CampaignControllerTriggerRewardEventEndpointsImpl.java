package com.extole.client.rest.impl.campaign.controller.trigger.reward.event;

import static com.extole.client.rest.campaign.CampaignRestException.INVALID_CAMPAIGN_ID;
import static com.extole.common.rest.exception.FatalRestRuntimeException.SOFTWARE_ERROR;
import static com.extole.model.entity.campaign.CampaignControllerTriggerType.REWARD_EVENT;

import java.time.ZoneOffset;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventEndpoints;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventResponse;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerTriggerRewardEvent;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerDescriptionLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameLengthException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerNameMissingException;
import com.extole.model.service.campaign.controller.trigger.TriggerTypeNotSupportedException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerRewardEventEmptyRewardStatesException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventBuilder;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventEventNameLengthException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventTagInvalidException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerRewardEventEndpointsImpl
    implements CampaignControllerTriggerRewardEventEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerRewardEventResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerRewardEventEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerRewardEventResponseMapper responseMapper,
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
    public CampaignControllerTriggerRewardEventResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerRewardEventCreateRequest request)
        throws CampaignRestException, UserAuthorizationRestException, CampaignControllerRestException,
        CampaignControllerTriggerRewardEventValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
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
                .withErrorCode(INVALID_CAMPAIGN_ID)
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
            CampaignControllerTriggerRewardEventBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .addTrigger(REWARD_EVENT);

            fillCommonAttributes(request, triggerBuilder);

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
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwTriggerRewardEventRelatedBuildCampaignRestExceptionIfPossible(
                    (BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerRewardEventResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerRewardEventCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerRewardEventValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerRewardEvent trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getRewardEventStepTrigger(campaign, controllerId, triggerId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(INVALID_CAMPAIGN_ID)
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
            CampaignControllerTriggerRewardEventBuilder triggerBuilder = campaignBuilder
                .updateStep(step)
                .updateTrigger(trigger);

            fillCommonAttributes(request, triggerBuilder);

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
        } catch (BuildCampaignException e) {
            if (e instanceof BuildCampaignEvaluatableException) {
                throwTriggerRewardEventRelatedBuildCampaignRestExceptionIfPossible(
                    (BuildCampaignEvaluatableException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerRewardEventResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerRewardEvent trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getRewardEventStepTrigger(campaign, controllerId, triggerId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(INVALID_CAMPAIGN_ID)
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
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerRewardEventResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerRewardEvent trigger =
            campaignStepProvider.getRewardEventStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    private void fillCommonAttributes(CampaignControllerTriggerRewardEventCreateRequest request,
        CampaignControllerTriggerRewardEventBuilder triggerBuilder)
        throws CampaignComponentValidationRestException {
        request.getTriggerPhase()
            .ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));

        request.getEventNames().ifPresent(value -> triggerBuilder.withEventNames(value));
        request.getRewardStates().ifPresent(value -> triggerBuilder.withRewardStates(
            Evaluatables.remapClassToClass(value, new TypeReference<>() {})));
        request.getTags().ifPresent(value -> triggerBuilder.withTags(value));

        request.getName().ifPresent(name -> triggerBuilder.withName(name));
        request.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
        request.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
        request.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(triggerBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(triggerBuilder, componentReferences);
        });
    }

    private void throwTriggerRewardEventRelatedBuildCampaignRestExceptionIfPossible(
        BuildCampaignEvaluatableException exception)
        throws CampaignControllerTriggerRewardEventValidationRestException {
        Throwable cause = exception.getCause();
        if (cause instanceof CampaignControllerTriggerRewardEventEventNameLengthException) {
            CampaignControllerTriggerRewardEventEventNameLengthException e =
                (CampaignControllerTriggerRewardEventEventNameLengthException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerRewardEventValidationRestException.EVENT_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException) {
            CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException e =
                (CampaignControllerTriggerRewardEventIllegalCharacterInEventNameException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerRewardEventValidationRestException.EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerRewardEventEmptyRewardStatesException) {
            CampaignControllerRewardEventEmptyRewardStatesException e =
                (CampaignControllerRewardEventEmptyRewardStatesException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerRewardEventValidationRestException.INVALID_REWARD_STATES)
                .withCause(e)
                .build();
        } else if (cause instanceof CampaignControllerTriggerRewardEventTagInvalidException) {
            CampaignControllerTriggerRewardEventTagInvalidException e =
                (CampaignControllerTriggerRewardEventTagInvalidException) cause;
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerRewardEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerRewardEventValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
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
