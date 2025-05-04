package com.extole.client.rest.impl.campaign.controller.action.schedule;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleCreateRequest;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleEndpoints;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleResponse;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleUpdateRequest;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleValidationRestException;
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
import com.extole.model.entity.campaign.CampaignControllerActionSchedule;
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
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBackdatedDateException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeNameInvalidException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeNameLengthException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeValueInvalidException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataAttributeValueLengthException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDataInvalidSyntaxException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleDelaysAndDatesNotSupportedTogetherException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleIllegalCharacterInNameException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleInvalidDateException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleInvalidDelayException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleMissingScheduleNameException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleNameLengthException;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleNegativeDelayException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;

@Provider
public class CampaignControllerActionScheduleEndpointsImpl implements CampaignControllerActionScheduleEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionScheduleResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignControllerActionScheduleEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionScheduleResponseMapper responseMapper,
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
    public CampaignControllerActionScheduleResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        CampaignControllerActionScheduleCreateRequest request,
        ZoneId zoneId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionScheduleValidationRestException, CampaignComponentValidationRestException,
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
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }

        try {
            CampaignControllerActionScheduleBuilder actionBuilder = campaignBuilder
                .updateController(controller)
                .addAction(CampaignControllerActionType.SCHEDULE);

            request.getQuality().ifPresent(quality -> actionBuilder
                .withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(enabled -> actionBuilder.withEnabled(enabled));
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });
            request.getScheduleName().ifPresent(scheduleName -> actionBuilder.withScheduleName(scheduleName));
            request.isForce().ifPresent(force -> actionBuilder.withForce(force.booleanValue()));
            request.getDelays().ifPresent(delays -> actionBuilder.withScheduleDelays(delays));
            request.getDates().ifPresent(dates -> actionBuilder
                .withScheduleDates(dates.stream().map(date -> date.toInstant()).collect(Collectors.toList())));
            request.getData().ifPresent(data -> actionBuilder.withData(data));

            return responseMapper.toResponse(actionBuilder.save(), zoneId);
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
        } catch (CampaignControllerActionScheduleDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionScheduleMissingScheduleNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.MISSING_SCHEDULE_NAME)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.SCHEDULE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("schedule_name", e.getEvaluatable())
                .addParameter("evaluated_schedule_name", e.getEvaluatedScheduleName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.SCHEDULE_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("schedule_name", request.getScheduleName()).withCause(e).build();
        } catch (CampaignControllerActionScheduleNegativeDelayException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.NEGATIVE_SCHEDULE_DELAY)
                .addParameter("delay", e.getDelay())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleInvalidDelayException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DELAY)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleInvalidDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DATE)
                .addParameter("date", e.getScheduleDate())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleBackdatedDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.BACKDATED_DATE)
                .addParameter("date", e.getDate().toString())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDelaysAndDatesNotSupportedTogetherException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataInvalidSyntaxException e) {
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
    public CampaignControllerActionScheduleResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        CampaignControllerActionScheduleUpdateRequest request,
        ZoneId zoneId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionScheduleValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionSchedule action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getScheduleControllerAction(campaign, controllerId, actionId);
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
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        try {
            CampaignControllerActionScheduleBuilder actionBuilder =
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
            request.getScheduleName().ifPresent(scheduleName -> actionBuilder.withScheduleName(scheduleName));
            request.isForce().ifPresent(force -> actionBuilder.withForce(force.booleanValue()));
            request.getDelays().ifPresent(delays -> actionBuilder.withScheduleDelays(delays));
            request.getDates().ifPresent(dates -> actionBuilder
                .withScheduleDates(dates.stream().map(date -> date.toInstant()).collect(Collectors.toList())));
            request.getData().ifPresent(data -> actionBuilder.withData(data));

            return responseMapper.toResponse(actionBuilder.save(), zoneId);
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
        } catch (CampaignControllerActionScheduleDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_INVALID)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", e.getDataAttributeName())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionScheduleDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getDataAttributeName()).withCause(e).build();
        } catch (CampaignControllerActionScheduleMissingScheduleNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.MISSING_SCHEDULE_NAME)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.SCHEDULE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("schedule_name", e.getEvaluatable())
                .addParameter("evaluated_schedule_name", e.getEvaluatedScheduleName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionScheduleIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.SCHEDULE_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("schedule_name", request.getScheduleName())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleNegativeDelayException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.NEGATIVE_SCHEDULE_DELAY)
                .addParameter("delay", e.getDelay())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleInvalidDelayException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DELAY)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleInvalidDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.INVALID_SCHEDULE_DATE)
                .addParameter("date", e.getScheduleDate())
                .addParameter("message", e.getMessage())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleBackdatedDateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(CampaignControllerActionScheduleValidationRestException.BACKDATED_DATE)
                .addParameter("date", e.getDate().toString())
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDelaysAndDatesNotSupportedTogetherException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionScheduleValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionScheduleValidationRestException.DELAYS_AND_DATES_NOT_SUPPORTED_TOGETHER)
                .withCause(e).build();
        } catch (CampaignControllerActionScheduleDataInvalidSyntaxException e) {
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
    public CampaignControllerActionScheduleResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign;
        CampaignController controller;
        CampaignControllerActionSchedule action;
        CampaignBuilder campaignBuilder;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            controller = campaignStepProvider.getController(campaign, controllerId);
            action = campaignStepProvider.getScheduleControllerAction(campaign, controllerId, actionId);
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
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }

        try {
            campaignBuilder.updateController(controller)
                .removeAction(action)
                .save();

            return responseMapper.toResponse(action, timeZone);
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
    public CampaignControllerActionScheduleResponse get(String accessToken, String campaignId, String version,
        String controllerId, String actionId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionSchedule action =
            campaignStepProvider.getScheduleControllerAction(campaign, controllerId, actionId);

        return responseMapper.toResponse(action, timeZone);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

}
