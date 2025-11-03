package com.extole.client.rest.impl.campaign.controller.trigger.audience.membership;

import static com.extole.client.rest.campaign.CampaignRestException.INVALID_CAMPAIGN_ID;
import static com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipValidationRestException.AUDIENCE_NOT_FOUND;
import static com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipValidationRestException.DISABLED_AUDIENCE;
import static com.extole.common.rest.exception.FatalRestRuntimeException.SOFTWARE_ERROR;
import static com.extole.model.entity.campaign.CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP;

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
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipCreateRequest;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipEndpoints;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipResponse;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipUpdateRequest;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerTriggerAudienceMembership;
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
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipDisabledAudienceException;
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipInvalidAudienceIdException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Provider
public class CampaignControllerTriggerAudienceMembershipEndpointsImpl
    implements CampaignControllerTriggerAudienceMembershipEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerTriggerAudienceMembershipResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerTriggerAudienceMembershipEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerTriggerAudienceMembershipResponseMapper responseMapper,
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
    public CampaignControllerTriggerAudienceMembershipResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerTriggerAudienceMembershipCreateRequest createRequest)
        throws CampaignRestException, UserAuthorizationRestException, CampaignControllerRestException,
        CampaignControllerTriggerAudienceMembershipValidationRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
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
            CampaignControllerTriggerAudienceMembershipBuilder triggerBuilder =
                campaignBuilder.updateStep(step)
                    .addTrigger(AUDIENCE_MEMBERSHIP);

            createRequest.getName().ifPresent(name -> triggerBuilder.withName(name));
            createRequest.getParentTriggerGroupName()
                .ifPresent(parentTriggerGroupName -> triggerBuilder.withParentTriggerGroupName(parentTriggerGroupName));
            createRequest.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            createRequest.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            createRequest.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            createRequest.getTriggerPhase().ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            createRequest.getHavingAnyAudienceId()
                .ifPresent(havingAnyAudienceId -> triggerBuilder.withHavingAnyAudienceId(havingAnyAudienceId));
            createRequest.getPersonId().ifPresent(personId -> triggerBuilder.withPersonId(personId));
            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            createRequest.getComponentReferences().ifPresent(componentReferences -> {
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
        } catch (CampaignControllerTriggerAudienceMembershipInvalidAudienceIdException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerAudienceMembershipValidationRestException.class)
                .withErrorCode(AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerAudienceMembershipDisabledAudienceException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerAudienceMembershipValidationRestException.class)
                .withErrorCode(DISABLED_AUDIENCE)
                .addParameter("audience_id", e.getAudienceId())
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
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId,
        CampaignControllerTriggerAudienceMembershipUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerAudienceMembershipValidationRestException,
        CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerAudienceMembership trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getAudienceMembershipStepTrigger(campaign, controllerId, triggerId);
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
            CampaignControllerTriggerAudienceMembershipBuilder triggerBuilder = campaignBuilder
                .updateStep(step)
                .updateTrigger(trigger);

            updateRequest.getName().ifPresent(name -> triggerBuilder.withName(name));
            updateRequest.getParentTriggerGroupName()
                .ifPresent(parentTriggerGroupName -> triggerBuilder.withParentTriggerGroupName(parentTriggerGroupName));
            updateRequest.getDescription().ifPresent(description -> triggerBuilder.withDescription(description));
            updateRequest.getEnabled().ifPresent(enabled -> triggerBuilder.withEnabled(enabled));
            updateRequest.getNegated().ifPresent(negated -> triggerBuilder.withNegated(negated));
            updateRequest.getTriggerPhase().ifPresent(
                phase -> triggerBuilder.withTriggerPhase(Evaluatables.remapEnum(phase, new TypeReference<>() {})));
            updateRequest.getHavingAnyAudienceId()
                .ifPresent(havingAnyAudienceId -> triggerBuilder.withHavingAnyAudienceId(havingAnyAudienceId));
            updateRequest.getPersonId().ifPresent(personId -> triggerBuilder.withPersonId(personId));
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(triggerBuilder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
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
        } catch (CampaignControllerTriggerAudienceMembershipInvalidAudienceIdException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerAudienceMembershipValidationRestException.class)
                .withErrorCode(AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (CampaignControllerTriggerAudienceMembershipDisabledAudienceException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerTriggerAudienceMembershipValidationRestException.class)
                .withErrorCode(DISABLED_AUDIENCE)
                .addParameter("audience_id", e.getAudienceId())
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
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignStep step;
        CampaignControllerTriggerAudienceMembership trigger;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, controllerId);
            trigger = campaignStepProvider.getAudienceMembershipStepTrigger(campaign, controllerId, triggerId);
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
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerTriggerAudienceMembershipResponse get(String accessToken, String campaignId,
        String version, String controllerId, String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTriggerAudienceMembership trigger =
            campaignStepProvider.getAudienceMembershipStepTrigger(campaign, controllerId, triggerId);

        return responseMapper.toResponse(trigger, ZoneOffset.UTC);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
