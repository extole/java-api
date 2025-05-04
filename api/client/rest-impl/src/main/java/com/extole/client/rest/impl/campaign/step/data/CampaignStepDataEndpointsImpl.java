package com.extole.client.rest.impl.campaign.step.data;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.step.data.BuiltStepDataResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.step.data.CampaignStepDataEndpoints;
import com.extole.client.rest.campaign.step.data.CampaignStepDataRestException;
import com.extole.client.rest.campaign.step.data.CampaignStepDataValidationRestException;
import com.extole.client.rest.campaign.step.data.StepDataCreateRequest;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.client.rest.campaign.step.data.StepDataUpdateRequest;
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
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignStep;
import com.extole.model.entity.campaign.built.BuiltStepData;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerLegacyActionIdStepDataDeletionException;
import com.extole.model.service.campaign.journey.entry.MultipleKeysOnJourneyEntryException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;
import com.extole.model.service.campaign.step.CampaignStepBuilder;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuilder;
import com.extole.model.service.campaign.step.data.StepDataDefaultValueExpressionLengthException;
import com.extole.model.service.campaign.step.data.StepDataDefaultValueInvalidSyntaxException;
import com.extole.model.service.campaign.step.data.StepDataDuplicateNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingNameException;
import com.extole.model.service.campaign.step.data.StepDataMissingValueException;
import com.extole.model.service.campaign.step.data.StepDataNameLengthException;
import com.extole.model.service.campaign.step.data.StepDataValueExpressionLengthException;
import com.extole.model.service.campaign.step.data.StepDataValueInvalidSyntaxException;

@Provider
public class CampaignStepDataEndpointsImpl implements CampaignStepDataEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignStepDataRestMapper campaignStepDataRestMapper;
    private final CampaignService campaignService;
    private final CampaignProvider campaignProvider;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignStepDataEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignStepDataRestMapper campaignStepDataRestMapper,
        CampaignService campaignService,
        CampaignProvider campaignProvider,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignStepDataRestMapper = campaignStepDataRestMapper;
        this.campaignService = campaignService;
        this.campaignProvider = campaignProvider;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepDataResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        StepDataCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignStepDataValidationRestException,
        CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            StepDataBuilder stepDataBuilder = applyRequestDataToTheStepDataBuilder(authorization, campaignId,
                expectedCurrentVersion, stepId, request);
            return campaignStepDataRestMapper.toStepDataResponse(stepDataBuilder.save());
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
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (StepDataDuplicateNameException | StepDataMissingNameException | StepDataMissingValueException
            | StepDataNameLengthException | StepDataValueExpressionLengthException
            | StepDataDefaultValueExpressionLengthException | MultipleKeysOnJourneyEntryException e) {
            throw handleDataExceptions(e);
        } catch (StepDataValueInvalidSyntaxException e) {
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
        } catch (StepDataDefaultValueInvalidSyntaxException e) {
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
        } catch (StepDataBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public StepDataResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        String stepDataId,
        StepDataUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignStepDataRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException,
        CampaignStepDataValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            StepDataBuilder stepDataBuilder = applyRequestDataToTheStepDataBuilder(authorization, campaignId,
                expectedCurrentVersion, stepId, stepDataId, request);

            return campaignStepDataRestMapper.toStepDataResponse(stepDataBuilder.save());
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "data")
                .addParameter("referencing_entity", stepDataId)
                .withCause(e)
                .build();
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
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (StepDataDuplicateNameException | StepDataMissingNameException | StepDataMissingValueException
            | StepDataNameLengthException | StepDataValueExpressionLengthException
            | StepDataDefaultValueExpressionLengthException | MultipleKeysOnJourneyEntryException e) {
            throw handleDataExceptions(e);
        } catch (StepDataValueInvalidSyntaxException | StepDataDefaultValueInvalidSyntaxException e) {
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
        } catch (StepDataBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public StepDataResponse delete(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        String stepDataId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, CampaignStepDataRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignStepDataValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));

        CampaignStep step = campaignStepProvider.getStep(campaign, stepId);
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

        try {
            CampaignStepBuilder<?, ?> stepBuilder = campaignBuilder.updateStep(step);
            StepData stepData = campaignStepProvider.getStepData(campaign, stepId, stepDataId);
            stepBuilder.removeStepData(stepData)
                .save();

            return campaignStepDataRestMapper.toStepDataResponse(stepData);
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
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignControllerLegacyActionIdStepDataDeletionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.LEGACY_ACTION_ID_STEP_DATA_DELETION)
                .addParameter("controller_id", e.getControllerId())
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
    public StepDataResponse get(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        String stepDataId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignStepDataRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), expectedCurrentVersion);
        StepData stepData = campaignStepProvider.getStepData(campaign, stepId, stepDataId);

        return campaignStepDataRestMapper.toStepDataResponse(stepData);
    }

    @Override
    public List<StepDataResponse> getAll(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), expectedCurrentVersion);
        CampaignStep step = campaignStepProvider.getStep(campaign, stepId);

        return campaignStepDataRestMapper.toStepDataResponses(step.getData());
    }

    @Override
    public BuiltStepDataResponse getBuilt(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        String stepDataId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignControllerRestException, CampaignStepDataRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            expectedCurrentVersion);
        BuiltStepData builtStepData = campaignStepProvider.getBuiltStepData(builtCampaign, stepId, stepDataId);

        return campaignStepDataRestMapper.toBuiltStepDataResponse(builtStepData);
    }

    @Override
    public List<BuiltStepDataResponse> getAllBuilt(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String stepId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignControllerRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId),
            expectedCurrentVersion);
        BuiltCampaignStep builtStep = campaignStepProvider.getBuiltStep(builtCampaign, stepId);

        return campaignStepDataRestMapper.toBuiltStepDataResponses(builtStep.getData());
    }

    private StepDataBuilder applyRequestDataToTheStepDataBuilder(
        Authorization authorization,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        StepDataCreateRequest request)
        throws CampaignRestException, CampaignControllerRestException, CampaignComponentValidationRestException {

        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
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

        StepDataBuilder stepDataBuilder = getStepDataBuilder(campaign, campaignBuilder, stepId);

        request.getName().ifDefined(value -> stepDataBuilder.withName(value));
        request.getValue().ifDefined(value -> stepDataBuilder.withValue(value));
        request.getScope().ifPresent(
            scope -> stepDataBuilder.withScope(Evaluatables.remapEnum(scope, new TypeReference<>() {})));
        request.isDimension().ifPresent(value -> stepDataBuilder.withDimension(value));
        request.getPersistTypes().ifPresent(
            persistTypes -> stepDataBuilder
                .withPersistTypes(Evaluatables.remapEnumCollection(persistTypes, new TypeReference<>() {})));
        request.getDefaultValue().ifPresent(value -> stepDataBuilder.withDefaultValue(value));
        request.getKeyType().ifPresent(
            keyType -> stepDataBuilder.withKeyType(Evaluatables.remapEnum(keyType, new TypeReference<>() {})));
        request.getEnabled().ifPresent(enabled -> stepDataBuilder.withEnabled(enabled));
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(stepDataBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(stepDataBuilder, componentReferences);
        });

        return stepDataBuilder;
    }

    private StepDataBuilder applyRequestDataToTheStepDataBuilder(
        Authorization authorization,
        String campaignId,
        String expectedCurrentVersion,
        String stepId,
        String stepDataId,
        StepDataUpdateRequest request)
        throws CampaignRestException, CampaignControllerRestException, CampaignStepDataRestException,
        CampaignComponentValidationRestException {

        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
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

        StepDataBuilder stepDataBuilder = getStepDataBuilder(campaign, campaignBuilder, stepId, stepDataId);

        request.getName().ifPresent(name -> name.ifDefined(definedName -> stepDataBuilder.withName(definedName)));
        request.getValue()
            .ifPresent(value -> value.ifDefined(definedValue -> stepDataBuilder.withValue(definedValue)));
        request.getScope().ifPresent(
            scope -> stepDataBuilder.withScope(Evaluatables.remapEnum(scope, new TypeReference<>() {})));
        request.isDimension().ifPresent(value -> stepDataBuilder.withDimension(value));
        request.getPersistTypes().ifPresent(
            persistTypes -> stepDataBuilder
                .withPersistTypes(Evaluatables.remapEnumCollection(persistTypes, new TypeReference<>() {})));
        request.getDefaultValue().ifPresent(value -> stepDataBuilder.withDefaultValue(value));
        request.getKeyType().ifPresent(
            keyType -> stepDataBuilder.withKeyType(Evaluatables.remapEnum(keyType, new TypeReference<>() {})));
        request.getEnabled().ifPresent(enabled -> stepDataBuilder.withEnabled(enabled));
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(stepDataBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(stepDataBuilder, componentReferences);
        });

        return stepDataBuilder;
    }

    private StepDataBuilder getStepDataBuilder(Campaign campaign, CampaignBuilder campaignBuilder, String stepId)
        throws CampaignControllerRestException {
        CampaignStep step = campaignStepProvider.getStep(campaign, stepId);
        CampaignStepBuilder<?, ?> stepBuilder = campaignBuilder.updateStep(step);

        return stepBuilder.addData();
    }

    private StepDataBuilder getStepDataBuilder(Campaign campaign, CampaignBuilder campaignBuilder, String stepId,
        String stepDataId)
        throws CampaignControllerRestException, CampaignStepDataRestException {
        CampaignStep step = campaignStepProvider.getStep(campaign, stepId);
        CampaignStepBuilder<?, ?> stepBuilder = campaignBuilder.updateStep(step);
        StepData stepData = campaignStepProvider.getStepData(campaign, stepId, stepDataId);

        return stepBuilder.updateStepData(stepData);
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder, List<Id<ComponentResponse>> componentIds) {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private CampaignStepDataValidationRestException handleDataExceptions(Exception exception) {
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
        if (exception instanceof MultipleKeysOnJourneyEntryException) {
            MultipleKeysOnJourneyEntryException multipleKeysOnJourneyEntryException =
                (MultipleKeysOnJourneyEntryException) exception;
            return RestExceptionBuilder.newBuilder(CampaignStepDataValidationRestException.class)
                .withErrorCode(CampaignStepDataValidationRestException.MULTIPLE_KEYS_NOT_ALLOWED)
                .addParameter("data_names", multipleKeysOnJourneyEntryException.getDataNames())
                .withCause(multipleKeysOnJourneyEntryException)
                .build();
        }

        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(exception)
            .build();
    }

}
