package com.extole.client.rest.impl.campaign.controller;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.controller.BuiltCampaignStepResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerEndpoints;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignJourneyEntryValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignStepValidationRestException;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.create.CampaignStepCreateRequest;
import com.extole.client.rest.campaign.controller.response.CampaignStepResponse;
import com.extole.client.rest.campaign.controller.update.CampaignStepUpdateRequest;
import com.extole.client.rest.campaign.step.data.CampaignStepDataValidationRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.BuiltCampaignStepResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.BuiltCampaignStepResponseMapperRepository;
import com.extole.client.rest.impl.campaign.controller.create.CampaignStepCreateRequestMapper;
import com.extole.client.rest.impl.campaign.controller.create.CampaignStepCreateRequestMapperRepository;
import com.extole.client.rest.impl.campaign.controller.response.CampaignStepResponseMapper;
import com.extole.client.rest.impl.campaign.controller.response.CampaignStepResponseMapperRepository;
import com.extole.client.rest.impl.campaign.controller.update.CampaignStepUpdateRequestMapper;
import com.extole.client.rest.impl.campaign.controller.update.CampaignStepUpdateRequestMapperRepository;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidComponentReferenceSocketNameException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignStep;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerAliasLengthException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerDuplicateException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerIllegalCharacterInNameException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerInvalidEnabledOnStateException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerLegacyActionIdStepDataDeletionException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerMissingJourneyNameException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerNameLengthException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerNameMissingException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerNameSameAsDefaultControllerNameException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerTooManyJourneyNamesException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerDuplicateException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerNameLengthException;
import com.extole.model.service.campaign.frontend.controller.FrontendControllerNameSameAsDefaultControllerNameException;
import com.extole.model.service.campaign.frontend.controller.IllegalCharacterInFrontendControllerNameException;
import com.extole.model.service.campaign.journey.entry.DuplicateJourneyEntryException;
import com.extole.model.service.campaign.journey.entry.JourneyKeyNameLengthException;
import com.extole.model.service.campaign.journey.entry.NullJourneyKeyNameException;
import com.extole.model.service.campaign.journey.entry.NullJourneyKeyValueException;
import com.extole.model.service.campaign.label.CampaignLabelBuildException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.step.data.StepDataDefaultValueExpressionLengthException;
import com.extole.model.service.campaign.step.data.StepDataDuplicateNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingValueException;
import com.extole.model.service.campaign.step.data.StepDataNameLengthException;
import com.extole.model.service.campaign.step.data.StepDataValueExpressionLengthException;
import com.extole.model.service.campaign.step.exception.CampaignStepInvalidJourneyNameException;
import com.extole.model.service.campaign.step.exception.CampaignStepJourneyNameLengthException;
import com.extole.model.service.campaign.step.exception.CampaignStepNullJourneyNameException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignControllerEndpointsImpl implements CampaignControllerEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final CampaignStepCreateRequestMapperRepository stepCreateRequestMapperRepository;
    private final CampaignStepUpdateRequestMapperRepository stepUpdateRequestMapperRepository;
    private final CampaignStepResponseMapperRepository stepResponseMapperRepository;
    private final BuiltCampaignStepResponseMapperRepository builtStepResponseMapperRepository;

    @Inject
    public CampaignControllerEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignProvider campaignProvider,
        CampaignStepCreateRequestMapperRepository stepCreateRequestMapperRepository,
        CampaignStepUpdateRequestMapperRepository stepUpdateRequestMapperRepository,
        CampaignStepResponseMapperRepository stepResponseMapperRepository,
        BuiltCampaignStepResponseMapperRepository builtStepResponseMapperRepository) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.stepCreateRequestMapperRepository = stepCreateRequestMapperRepository;
        this.stepUpdateRequestMapperRepository = stepUpdateRequestMapperRepository;
        this.stepResponseMapperRepository = stepResponseMapperRepository;
        this.builtStepResponseMapperRepository = builtStepResponseMapperRepository;
    }

    @SuppressWarnings("MethodLength")
    @Override
    public CampaignStepResponse create(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignStepCreateRequest createRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignStepDataValidationRestException, CampaignJourneyEntryValidationRestException,
        CampaignStepValidationRestException, CampaignFrontendControllerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignBuilder campaignBuilder;
        try {
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

        CampaignStepCreateRequestMapper createRequestMapper =
            stepCreateRequestMapperRepository.getCreateRequestMapper(createRequest.getType());

        try {
            CampaignStep step = createRequestMapper.create(authorization, campaignBuilder, createRequest);

            CampaignStepResponseMapper responseMapper = stepResponseMapperRepository.getMapper(step.getType());
            return responseMapper.toResponse(step, timeZone);
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
        } catch (CampaignControllerNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignControllerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.DUPLICATE_CONTROLLER)
                .addParameter("controller_name", e.getControllerName())
                .addParameter("first_controller_id", e.getFirstControllerId())
                .addParameter("first_controller_journey_names", e.getFirstControllerJourneyNames())
                .addParameter("second_controller_id", e.getSecondControllerId())
                .addParameter("second_controller_journey_names", e.getSecondControllerJourneyNames())
                .withCause(e)
                .build();
        } catch (CampaignControllerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerInvalidEnabledOnStateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_ENABLED_ON_STATE)
                .addParameter("enabled_on_states", e.getEnabledOnStates())
                .withCause(e)
                .build();
        } catch (FrontendControllerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.DUPLICATE_FRONTEND_CONTROLLER)
                .addParameter("controller_name", e.getControllerName())
                .addParameter("second_controller_id", e.getSecondControllerId())
                .addParameter("journey_names", e.getIntersectedJourneyNames())
                .withCause(e)
                .build();
        } catch (CampaignControllerNameSameAsDefaultControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_SAME_AS_A_DEFAULT_CONTROLLER_NAME)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerMissingJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.MISSING_JOURNEY_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTooManyJourneyNamesException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.TOO_MANY_JOURNEY_NAMES)
                .addParameter("journey_names", e.getJourneyNames())
                .addParameter("actual_count", Integer.valueOf(e.getActualCount()))
                .addParameter("max_count", Integer.valueOf(e.getMaxCount()))
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "controller")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerAliasLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_STEP_ALIAS)
                .addParameter("alias", e.getControllerAlias())
                .withCause(e)
                .build();
        } catch (StepDataDuplicateNameException | StepDataMissingNameException | StepDataMissingValueException
            | StepDataNameLengthException | StepDataValueExpressionLengthException
            | StepDataDefaultValueExpressionLengthException
            | CampaignControllerLegacyActionIdStepDataDeletionException e) {
            throw handleDataExceptions(e);
        } catch (DuplicateJourneyEntryException | NullJourneyKeyNameException | JourneyKeyNameLengthException
            | NullJourneyKeyValueException e) {
            throw handleJourneyEntryExceptions(e);
        } catch (CampaignStepJourneyNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.JOURNEY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("journey_name", e.getJourneyName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignStepInvalidJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.JOURNEY_NAME_INVALID)
                .addParameter("journey_name", e.getJourneyName())
                .withCause(e)
                .build();
        } catch (CampaignStepNullJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.NULL_JOURNEY_NAME)
                .withCause(e)
                .build();
        } catch (FrontendControllerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (FrontendControllerNameSameAsDefaultControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.RESERVED_NAME)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .withCause(e)
                .build();
        } catch (IllegalCharacterInFrontendControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignStepBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings("MethodLength")
    @Override
    public CampaignStepResponse update(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        CampaignStepUpdateRequest updateRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        CampaignControllerRestException, CampaignComponentValidationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignStepDataValidationRestException,
        CampaignJourneyEntryValidationRestException, CampaignStepValidationRestException,
        CampaignFrontendControllerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignStep step;
        CampaignBuilder campaignBuilder;
        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, stepId);
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion).ifPresent(campaignBuilder::withExpectedVersion);

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

        CampaignStepUpdateRequestMapper updateRequestMapper =
            stepUpdateRequestMapperRepository.getUpdateRequestMapper(step.getType());
        CampaignStepResponseMapper responseMapper = stepResponseMapperRepository.getMapper(step.getType());

        try {
            CampaignStep updatedStep = updateRequestMapper.update(authorization, campaignBuilder, step, updateRequest);
            return responseMapper.toResponse(updatedStep, timeZone);
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
        } catch (CampaignControllerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.DUPLICATE_CONTROLLER)
                .addParameter("controller_name", e.getControllerName())
                .addParameter("first_controller_id", e.getFirstControllerId())
                .addParameter("first_controller_journey_names", e.getFirstControllerJourneyNames())
                .addParameter("second_controller_id", e.getSecondControllerId())
                .addParameter("second_controller_journey_names", e.getSecondControllerJourneyNames())
                .withCause(e)
                .build();
        } catch (CampaignControllerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerInvalidEnabledOnStateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_ENABLED_ON_STATE)
                .addParameter("enabled_on_states", e.getEnabledOnStates())
                .withCause(e)
                .build();
        } catch (FrontendControllerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.DUPLICATE_FRONTEND_CONTROLLER)
                .addParameter("controller_name", e.getControllerName())
                .addParameter("second_controller_id", e.getSecondControllerId())
                .addParameter("journey_names", e.getIntersectedJourneyNames())
                .withCause(e)
                .build();
        } catch (CampaignControllerNameSameAsDefaultControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.NAME_SAME_AS_A_DEFAULT_CONTROLLER_NAME)
                .addParameter("name", e.getControllerName())
                .withCause(e)
                .build();
        } catch (CampaignControllerMissingJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.MISSING_JOURNEY_NAME)
                .withCause(e)
                .build();
        } catch (CampaignControllerTooManyJourneyNamesException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.TOO_MANY_JOURNEY_NAMES)
                .addParameter("journey_names", e.getJourneyNames())
                .addParameter("actual_count", Integer.valueOf(e.getActualCount()))
                .addParameter("max_count", Integer.valueOf(e.getMaxCount()))
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "controller")
                .addParameter("referencing_entity", stepId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerAliasLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.INVALID_STEP_ALIAS)
                .addParameter("alias", e.getControllerAlias())
                .withCause(e)
                .build();
        } catch (StepDataDuplicateNameException | StepDataMissingNameException | StepDataMissingValueException
            | StepDataNameLengthException | StepDataValueExpressionLengthException
            | StepDataDefaultValueExpressionLengthException
            | CampaignControllerLegacyActionIdStepDataDeletionException e) {
            throw handleDataExceptions(e);
        } catch (DuplicateJourneyEntryException | NullJourneyKeyNameException | JourneyKeyNameLengthException
            | NullJourneyKeyValueException e) {
            throw handleJourneyEntryExceptions(e);
        } catch (CampaignStepJourneyNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.JOURNEY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("journey_name", e.getJourneyName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignStepInvalidJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.JOURNEY_NAME_INVALID)
                .addParameter("journey_name", e.getJourneyName())
                .withCause(e)
                .build();
        } catch (CampaignStepNullJourneyNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepValidationRestException.class)
                .withErrorCode(CampaignStepValidationRestException.NULL_JOURNEY_NAME)
                .withCause(e)
                .build();
        } catch (FrontendControllerNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (FrontendControllerNameSameAsDefaultControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.RESERVED_NAME)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .withCause(e)
                .build();
        } catch (IllegalCharacterInFrontendControllerNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("controller_name", e.getName())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignStepBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignStepResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException {

        CampaignBuilder campaignBuilder;
        CampaignStep step;
        Campaign campaign;

        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            step = campaignStepProvider.getStep(campaign, stepId);
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

        CampaignStepResponseMapper responseMapper = stepResponseMapperRepository.getMapper(step.getType());

        try {
            campaignBuilder.removeStep(step).save();
            return responseMapper.toResponse(step, timeZone);
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
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignLabelBuildException
            | CampaignServiceNameMissingException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            // TODO move the handling of these exceptions in the proper builder as part of ENG-19069
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignStepResponse get(String accessToken, String campaignId, String version, String stepId,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignStep step = campaignStepProvider.getStep(campaign, stepId);

        CampaignStepResponseMapper responseMapper = stepResponseMapperRepository.getMapper(step.getType());
        return responseMapper.toResponse(step, timeZone);
    }

    @Override
    public <RESPONSE extends CampaignStepResponse> List<RESPONSE> list(String accessToken, String campaignId,
        String version, Optional<StepType> type, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        ImmutableList.Builder<CampaignStepResponse> responseListBuilder = ImmutableList.builder();
        List<CampaignStep> filteredSteps = campaign.getSteps().stream()
            .filter(step -> type.isEmpty()
                || step.getType().equals(com.extole.model.entity.campaign.StepType.valueOf(type.get().name())))
            .collect(Collectors.toUnmodifiableList());

        for (CampaignStep step : filteredSteps) {
            CampaignStepResponseMapper responseMapper = stepResponseMapperRepository.getMapper(step.getType());

            CampaignStepResponse stepResponse = responseMapper.toResponse(step, timeZone);
            responseListBuilder.add(stepResponse);
        }

        return (List<RESPONSE>) responseListBuilder.build();
    }

    @Override
    public BuiltCampaignStepResponse getBuilt(String accessToken, String campaignId, String version,
        String stepId, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaign builtCampaign = campaignProvider.buildCampaign(campaign);

        BuiltCampaignStep step = campaignStepProvider.getBuiltStep(builtCampaign, stepId);
        BuiltCampaignStepResponseMapper responseMapper = builtStepResponseMapperRepository.getMapper(step.getType());

        return responseMapper.toResponse(step, timeZone);
    }

    @Override
    public <RESPONSE extends BuiltCampaignStepResponse> List<RESPONSE> listBuilt(String accessToken, String campaignId,
        String version, Optional<StepType> type, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaign builtCampaign = campaignProvider.buildCampaign(campaign);

        List<BuiltCampaignStep> filteredSteps = builtCampaign.getSteps().stream()
            .filter(step -> type.isEmpty()
                || step.getType().equals(com.extole.model.entity.campaign.StepType.valueOf(type.get().name())))
            .collect(Collectors.toUnmodifiableList());
        ImmutableList.Builder<BuiltCampaignStepResponse> responseListBuilder = ImmutableList.builder();

        for (BuiltCampaignStep step : filteredSteps) {
            BuiltCampaignStepResponseMapper responseMapper =
                builtStepResponseMapperRepository.getMapper(step.getType());

            BuiltCampaignStepResponse stepResponse = responseMapper.toResponse(step, timeZone);
            responseListBuilder.add(stepResponse);
        }

        return (List<RESPONSE>) responseListBuilder.build();
    }

    public CampaignStepDataValidationRestException handleDataExceptions(Exception exception) {
        if (exception instanceof StepDataDuplicateNameException) {
            StepDataDuplicateNameException stepDataDuplicateNameException = (StepDataDuplicateNameException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.DUPLICATE_DATA_NAME)
                .addParameter("name", stepDataDuplicateNameException.getName())
                .withCause(stepDataDuplicateNameException)
                .build();
        }
        if (exception instanceof StepDataMissingNameException) {
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.MISSING_DATA_NAME)
                .withCause(exception)
                .build();
        }
        if (exception instanceof StepDataMissingValueException) {
            StepDataMissingValueException stepDataMissingValueException = (StepDataMissingValueException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.MISSING_DATA_VALUE)
                .addParameter("name", stepDataMissingValueException.getName())
                .withCause(stepDataMissingValueException)
                .build();
        }
        if (exception instanceof StepDataNameLengthException) {
            StepDataNameLengthException stepDataNameLengthException = (StepDataNameLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.DATA_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", stepDataNameLengthException.getName())
                .withCause(stepDataNameLengthException)
                .build();
        }
        if (exception instanceof StepDataValueExpressionLengthException) {
            StepDataValueExpressionLengthException stepDataValueExpressionLengthException =
                (StepDataValueExpressionLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.DATA_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", stepDataValueExpressionLengthException.getExpression())
                .addParameter("name", stepDataValueExpressionLengthException.getName())
                .withCause(stepDataValueExpressionLengthException)
                .build();
        }
        if (exception instanceof StepDataDefaultValueExpressionLengthException) {
            StepDataDefaultValueExpressionLengthException stepDataDefaultValueExpressionLengthException =
                (StepDataDefaultValueExpressionLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(
                    CampaignStepDataValidationRestException.DATA_DEFAULT_VALUE_EXPRESSION_LENGTH_OUT_OF_RANGE)
                .addParameter("expression", stepDataDefaultValueExpressionLengthException.getExpression())
                .addParameter("name", stepDataDefaultValueExpressionLengthException.getName())
                .withCause(stepDataDefaultValueExpressionLengthException)
                .build();
        }

        if (exception instanceof CampaignControllerLegacyActionIdStepDataDeletionException) {
            CampaignControllerLegacyActionIdStepDataDeletionException legacyActionIdStepDataDeletionException =
                (CampaignControllerLegacyActionIdStepDataDeletionException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.LEGACY_ACTION_ID_STEP_DATA_DELETION)
                .addParameter("controller_id", legacyActionIdStepDataDeletionException.getControllerId())
                .withCause(legacyActionIdStepDataDeletionException)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(exception)
            .build();
    }

    public CampaignJourneyEntryValidationRestException handleJourneyEntryExceptions(Exception exception) {
        if (exception instanceof DuplicateJourneyEntryException) {
            DuplicateJourneyEntryException duplicateJourneyEntryException = (DuplicateJourneyEntryException) exception;
            return RestExceptionBuilder.newBuilder(CampaignJourneyEntryValidationRestException.class)
                .withErrorCode(CampaignJourneyEntryValidationRestException.DUPLICATE_JOURNEY_ENTRY)
                .addParameter("journey_name", duplicateJourneyEntryException.getJourneyName())
                .withCause(exception)
                .build();
        }

        if (exception instanceof NullJourneyKeyNameException) {
            return RestExceptionBuilder.newBuilder(CampaignJourneyEntryValidationRestException.class)
                .withErrorCode(CampaignJourneyEntryValidationRestException.NULL_JOURNEY_KEY_NAME)
                .withCause(exception)
                .build();
        }

        if (exception instanceof JourneyKeyNameLengthException) {
            JourneyKeyNameLengthException journeyKeyNameLengthException = (JourneyKeyNameLengthException) exception;
            return RestExceptionBuilder.newBuilder(CampaignJourneyEntryValidationRestException.class)
                .withErrorCode(CampaignJourneyEntryValidationRestException.JOURNEY_KEY_NAME_INVALID_LENGTH)
                .addParameter("name", journeyKeyNameLengthException.getName())
                .addParameter("max_length", Integer.valueOf(journeyKeyNameLengthException.getMaxLength()))
                .withCause(exception)
                .build();
        }

        if (exception instanceof NullJourneyKeyValueException) {
            return RestExceptionBuilder.newBuilder(CampaignJourneyEntryValidationRestException.class)
                .withErrorCode(CampaignJourneyEntryValidationRestException.NULL_JOURNEY_KEY_VALUE)
                .withCause(exception)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(exception)
            .build();
    }

}
