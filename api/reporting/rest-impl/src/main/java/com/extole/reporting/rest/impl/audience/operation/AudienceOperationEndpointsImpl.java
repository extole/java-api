package com.extole.reporting.rest.impl.audience.operation;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.api.audience.Audience;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.service.audience.AudienceService;
import com.extole.reporting.entity.report.audience.operation.AudienceOperation;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDetails;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationState;
import com.extole.reporting.rest.audience.operation.AudienceOperationCreateRequest;
import com.extole.reporting.rest.audience.operation.AudienceOperationDetailedResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationEndpoints;
import com.extole.reporting.rest.audience.operation.AudienceOperationQueryParams;
import com.extole.reporting.rest.audience.operation.AudienceOperationResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationRestException;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;
import com.extole.reporting.rest.audience.operation.AudienceOperationValidationRestException;
import com.extole.reporting.rest.audience.operation.CancelAudienceOperationRestException;
import com.extole.reporting.rest.audience.operation.RetryAudienceOperationRestException;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.service.audience.operation.AnotherAudienceOperationInProgressException;
import com.extole.reporting.service.audience.operation.AudienceOperationBuildException;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationCancellationDataSourceNotSupportedException;
import com.extole.reporting.service.audience.operation.AudienceOperationNotFoundException;
import com.extole.reporting.service.audience.operation.AudienceOperationQueryBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationRetryNotAllowedException;
import com.extole.reporting.service.audience.operation.AudienceOperationService;
import com.extole.reporting.service.audience.operation.MissingAudienceOperationDataSourceException;
import com.extole.reporting.service.audience.operation.MissingAudienceOperationTypeException;

@Provider
public class AudienceOperationEndpointsImpl implements AudienceOperationEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceService audienceService;
    private final AudienceOperationService audienceOperationService;
    private final AudienceOperationResponseMapper audienceOperationResponseMapper;
    private final AudienceOperationStateResponseMapperRegistry audienceOperationStateResponseMapperRegistry;
    private final AudienceOperationDataSourceRequestUploaderRegistry audienceOperationDataSourceRequestUploaderRegistry;

    @Autowired
    public AudienceOperationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceService audienceService,
        AudienceOperationService audienceOperationService,
        AudienceOperationResponseMapper audienceOperationResponseMapper,
        AudienceOperationStateResponseMapperRegistry audienceOperationStateResponseMapperRegistry,
        AudienceOperationDataSourceRequestUploaderRegistry audienceOperationDataSourceRequestUploaderRegistry) {
        this.authorizationProvider = authorizationProvider;
        this.audienceService = audienceService;
        this.audienceOperationService = audienceOperationService;
        this.audienceOperationResponseMapper = audienceOperationResponseMapper;
        this.audienceOperationStateResponseMapperRegistry = audienceOperationStateResponseMapperRegistry;
        this.audienceOperationDataSourceRequestUploaderRegistry = audienceOperationDataSourceRequestUploaderRegistry;
    }

    @Override
    public AudienceOperationResponse get(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperation audienceOperation =
                audienceOperationService.getById(authorization, Id.valueOf(operationId.getValue()));

            validateAudienceId(audienceId, audienceOperation.getAudienceId(), audienceOperation.getId());

            return audienceOperationResponseMapper.toResponse(audienceOperation, timeZone);
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<AudienceOperationResponse> list(String accessToken, Id<Audience> audienceId,
        AudienceOperationQueryParams queryParams, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperationQueryBuilder queryBuilder = audienceOperationService.list(authorization);

            queryBuilder.withAudienceId(Id.valueOf(audienceId.getValue()));
            if (!queryParams.getStates().isEmpty()) {
                queryBuilder.withStates(queryParams.getStates().stream()
                    .map(com.extole.reporting.rest.audience.operation.AudienceOperationState::name)
                    .map(AudienceOperationState.State::valueOf)
                    .collect(Collectors.toSet()));
            }
            if (!queryParams.getTags().isEmpty()) {
                queryBuilder.withTags(queryParams.getTags());
            }
            queryParams.getType().map(AudienceOperationType::name)
                .map(com.extole.reporting.entity.report.audience.operation.AudienceOperationType::valueOf)
                .ifPresent(queryBuilder::withType);
            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            return queryBuilder.list()
                .stream()
                .sorted(Comparator.comparing(AudienceOperation::getCreatedDate).reversed())
                .map(audienceOperation -> audienceOperationResponseMapper.toResponse(audienceOperation, timeZone))
                .collect(Collectors.toList());
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationResponse create(String accessToken, Id<Audience> audienceId,
        AudienceOperationCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationValidationRestException, AudienceOperationRestException,
        PersonListAudienceOperationDataSourceValidationRestException,
        FileAssetAudienceOperationDataSourceValidationRestException,
        ReportAudienceOperationDataSourceValidationRestException,
        ActionAudienceOperationDataSourceValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (createRequest.getType() == null) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationValidationRestException.class)
                .withErrorCode(AudienceOperationValidationRestException.MISSING_TYPE)
                .build();
        }

        if (createRequest.getDataSource().getType() == null) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationValidationRestException.class)
                .withErrorCode(AudienceOperationValidationRestException.MISSING_DATA_SOURCE_TYPE)
                .build();
        }

        try {
            AudienceOperationBuilder audienceOperationBuilder =
                audienceOperationService.create(authorization, Id.valueOf(audienceId.getValue()),
                    com.extole.reporting.entity.report.audience.operation.AudienceOperationType
                        .valueOf(createRequest.getType().name()));

            createRequest.getTags().ifPresent(tags -> audienceOperationBuilder.withTags(tags));

            audienceOperationDataSourceRequestUploaderRegistry
                .getUploader(AudienceOperationDataSourceType.valueOf(createRequest.getDataSource().getType().name()))
                .upload(audienceOperationBuilder, createRequest.getDataSource());

            return audienceOperationResponseMapper.toResponse(audienceOperationBuilder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (MissingAudienceOperationTypeException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationValidationRestException.class)
                .withErrorCode(AudienceOperationValidationRestException.MISSING_TYPE)
                .withCause(e)
                .build();
        } catch (MissingAudienceOperationDataSourceException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationValidationRestException.class)
                .withErrorCode(AudienceOperationValidationRestException.MISSING_DATA_SOURCE)
                .withCause(e)
                .build();
        } catch (AnotherAudienceOperationInProgressException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.ANOTHER_OPERATION_IN_PROGRESS)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        } catch (AudienceOperationBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationStateResponse cancel(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException, CancelAudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperation audienceOperation =
                audienceOperationService.cancel(authorization, Id.valueOf(operationId.getValue()));

            validateAudienceId(audienceId, audienceOperation.getAudienceId(), audienceOperation.getId());

            return audienceOperationStateResponseMapperRegistry
                .getMapper(com.extole.reporting.entity.report.audience.operation.AudienceOperationType
                    .valueOf(audienceOperation.getType().name()))
                .toResponse(audienceOperation, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        } catch (AudienceOperationCancellationDataSourceNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(CancelAudienceOperationRestException.class)
                .withErrorCode(CancelAudienceOperationRestException.DATA_SOURCE_TYPE_NOT_SUPPORTED)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("data_source_type", e.getDataSourceType())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationStateResponse getState(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperation audienceOperation =
                audienceOperationService.getById(authorization, Id.valueOf(operationId.getValue()));

            validateAudienceId(audienceId, audienceOperation.getAudienceId(), audienceOperation.getId());

            return audienceOperationStateResponseMapperRegistry.getMapper(audienceOperation.getType())
                .toResponse(audienceOperation, timeZone);
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationStateDebugResponse getDebugState(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperationDetails audienceOperation =
                audienceOperationService.getDebugById(authorization, Id.valueOf(operationId.getValue()));

            validateAudienceId(audienceId, audienceOperation.getAudienceId(), audienceOperation.getId());

            return audienceOperationStateResponseMapperRegistry.getMapper(audienceOperation.getType())
                .toDebugResponse(audienceOperation, timeZone);
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationDetailedResponse getWithDetails(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceOperation audienceOperation =
                audienceOperationService.getById(authorization, Id.valueOf(operationId.getValue()));

            validateAudienceId(audienceId, audienceOperation.getAudienceId(), audienceOperation.getId());

            return audienceOperationResponseMapper.toDetailedResponse(audienceOperation, timeZone);
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationResponse retry(String accessToken, Id<Audience> audienceId,
        Id<com.extole.api.audience.operation.AudienceOperation> operationId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceOperationRestException, RetryAudienceOperationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AudienceOperation audienceOperation =
                audienceOperationService.retry(authorization, Id.valueOf(operationId.getValue()));
            return audienceOperationResponseMapper.toResponse(audienceOperation, timeZone);
        } catch (AudienceOperationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceOperationRetryNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(RetryAudienceOperationRestException.class)
                .withErrorCode(RetryAudienceOperationRestException.STATE_NOT_ALLOWED)
                .addParameter("operation_id", e.getOperationId())
                .addParameter("state", e.getState())
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    private void validateAudienceId(Id<Audience> givenAudienceId,
        Id<com.extole.model.entity.audience.Audience> actualAudienceId, Id<AudienceOperation> operationId)
        throws AudienceOperationRestException {
        if (!givenAudienceId.equals(actualAudienceId)) {
            throw RestExceptionBuilder.newBuilder(AudienceOperationRestException.class)
                .withErrorCode(AudienceOperationRestException.OPERATION_NOT_FOUND)
                .addParameter("operation_id", operationId)
                .build();
        }
    }

}
