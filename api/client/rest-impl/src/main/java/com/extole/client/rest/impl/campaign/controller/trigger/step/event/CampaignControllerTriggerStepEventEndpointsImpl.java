package com.extole.client.rest.impl.campaign.controller.trigger.step.event;

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
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerStepEventResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventEndpoints;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventResponse;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignControllerRestExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerStepEventResponseMapper;
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
import com.extole.model.entity.campaign.CampaignControllerTriggerStepEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerStepEvent;
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
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventBuilder;
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventDataNameLengthException;
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventEventNameLengthException;
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventIllegalCharacterInEventNameException;
import com.extole.model.service.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventMissingConstraintException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;
import com.extole.model.service.campaign.step.CampaignStepBuilder;

@Provider
public class CampaignControllerTriggerStepEventEndpointsImpl implements CampaignControllerTriggerStepEventEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerStepEventResponseMapper responseMapper;
    private final BuiltCampaignControllerTriggerStepEventResponseMapper builtResponseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerStepEventEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerStepEventResponseMapper responseMapper,
        BuiltCampaignControllerTriggerStepEventResponseMapper builtResponseMapper,
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
    public CampaignControllerTriggerStepEventResponse create(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerStepEventCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerStepEventValidationRestException, CampaignControllerTriggerValidationRestException,
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
            CampaignStepBuilder<?, ?> controllerBuilder = campaignBuilder
                .updateStep(step);
            CampaignControllerTriggerStepEventBuilder triggerBuilder = controllerBuilder
                .addTrigger(CampaignControllerTriggerType.STEP_EVENT);
            request.getTriggerPhase()
                .ifPresent(
                    phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));

            request.getEventNames().ifPresent((value) -> triggerBuilder.withEventNames(value));
            request.getHavingAnyDataName().ifPresent((value) -> triggerBuilder.withHavingAnyDataName(value));
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
        } catch (CampaignControllerTriggerStepEventDataNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.DATA_NAME_INVALID_LENGTH)
                .addParameter("data_name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.EVENT_NAME_INVALID_LENGTH)
                .addParameter("event_name", e.getEventName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerStepEventValidationRestException.ILLEGAL_CHARACTER_IN_EVENT_NAME)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventMissingConstraintException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.MISSING_CONSTRAINT)
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
    public CampaignControllerTriggerStepEventResponse update(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerStepEventUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerStepEventValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerStepEvent trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getStepEventStepTrigger(campaign, controllerId, triggerId);
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
            CampaignStepBuilder<?, ?> controllerBuilder = campaignBuilder
                .updateStep(step);
            CampaignControllerTriggerStepEventBuilder triggerBuilder = controllerBuilder.updateTrigger(trigger);

            request.getTriggerPhase()
                .ifPresent(phase -> triggerBuilder
                    .withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            request.getEventNames().ifPresent((value) -> triggerBuilder.withEventNames(value));
            request.getHavingAnyDataName().ifPresent((value) -> triggerBuilder.withHavingAnyDataName(value));
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
        } catch (CampaignControllerTriggerStepEventDataNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.DATA_NAME_INVALID_LENGTH)
                .addParameter("data_name", e.getDataName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventEventNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.EVENT_NAME_INVALID_LENGTH)
                .addParameter("event_name", e.getEventName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventIllegalCharacterInEventNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(
                    CampaignControllerTriggerStepEventValidationRestException.ILLEGAL_CHARACTER_IN_EVENT_NAME)
                .addParameter("event_name", e.getEventName())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerStepEventMissingConstraintException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerTriggerStepEventValidationRestException.class)
                .withErrorCode(CampaignControllerTriggerStepEventValidationRestException.MISSING_CONSTRAINT)
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
    public CampaignControllerTriggerStepEventResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId) throws UserAuthorizationRestException,
        CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerStepEvent trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getStepEventStepTrigger(campaign, controllerId, triggerId);
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
            CampaignStepBuilder<?, ?> controllerBuilder = campaignBuilder
                .updateStep(step);
            controllerBuilder
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
    public CampaignControllerTriggerStepEventResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerStepEvent trigger =
            campaignStepProvider.getStepEventStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    @Override
    public BuiltCampaignControllerTriggerStepEventResponse getBuilt(String accessToken, String campaignId,
        String version, String controllerId, String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignControllerTriggerStepEvent trigger =
            campaignStepProvider.getStepEventBuiltStepTrigger(
                campaignProvider.buildCampaign(campaign), controllerId, triggerId);

        return builtResponseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
