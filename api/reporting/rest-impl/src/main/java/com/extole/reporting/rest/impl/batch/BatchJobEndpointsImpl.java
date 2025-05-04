package com.extole.reporting.rest.impl.batch;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.batch.BatchJob;
import com.extole.reporting.entity.batch.BatchJobStatus;
import com.extole.reporting.entity.batch.column.BatchJobColumnPojo;
import com.extole.reporting.entity.batch.column.BatchJobColumnValidationPolicy;
import com.extole.reporting.entity.batch.column.FullNameMatchBatchJobColumnPojo;
import com.extole.reporting.entity.batch.column.PatternNameMatchBatchJobColumnPojo;
import com.extole.reporting.rest.batch.BatchJobCreateRequest;
import com.extole.reporting.rest.batch.BatchJobEndpoints;
import com.extole.reporting.rest.batch.BatchJobLocalEndpoints;
import com.extole.reporting.rest.batch.BatchJobProgressRestException;
import com.extole.reporting.rest.batch.BatchJobQueryParams;
import com.extole.reporting.rest.batch.BatchJobResponse;
import com.extole.reporting.rest.batch.BatchJobRestException;
import com.extole.reporting.rest.batch.BatchJobScope;
import com.extole.reporting.rest.batch.BatchJobStatusResponse;
import com.extole.reporting.rest.batch.BatchJobUpdateRequest;
import com.extole.reporting.rest.batch.BatchJobValidationRestException;
import com.extole.reporting.rest.batch.column.request.BatchJobColumnRequest;
import com.extole.reporting.rest.batch.column.request.FullNameMatchBatchJobColumnRequest;
import com.extole.reporting.rest.batch.column.request.PatternNameMatchBatchJobColumnRequest;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.FileAssetBatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.impl.batch.data.source.request.BatchJobDataSourceRequestMappersRepository;
import com.extole.reporting.service.batch.BatchJobBuilder;
import com.extole.reporting.service.batch.BatchJobDataSourceMissingException;
import com.extole.reporting.service.batch.BatchJobDeleteNotAllowedException;
import com.extole.reporting.service.batch.BatchJobEventNameInvalidException;
import com.extole.reporting.service.batch.BatchJobInvalidStateTransitionException;
import com.extole.reporting.service.batch.BatchJobListQueryBuilder;
import com.extole.reporting.service.batch.BatchJobLockedException;
import com.extole.reporting.service.batch.BatchJobNameInvalidException;
import com.extole.reporting.service.batch.BatchJobNotFoundException;
import com.extole.reporting.service.batch.BatchJobService;
import com.extole.reporting.service.batch.BatchJobTagInvalidException;
import com.extole.reporting.service.batch.BatchJobUnauthorizedScopesException;
import com.extole.reporting.service.batch.UpdateBatchJob;
import com.extole.reporting.service.batch.data.source.BatchJobDataSourceBuildException;
import com.extole.reporting.service.batch.data.source.FileAssetBatchJobDataSourceFormatNotSupportedException;

@Provider
public class BatchJobEndpointsImpl implements BatchJobEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final BatchJobService batchJobService;
    private final BatchJobResponseMapper batchResponseMapper;
    private final BatchJobDataSourceRequestMappersRepository batchJobDataSourceRequestMappersRepository;
    private final BatchJobLocalEndpointsProvider batchJobEndpointsProvider;

    @Inject
    public BatchJobEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        BatchJobService batchJobService,
        BatchJobResponseMapper responseMapper,
        BatchJobDataSourceRequestMappersRepository batchJobDataSourceRequestMappersRepository,
        @Context HttpHeaders httpHeaders,
        BatchJobLocalEndpointsProvider batchJobEndpointsProvider) {
        this.authorizationProvider = authorizationProvider;
        this.batchJobService = batchJobService;
        this.batchResponseMapper = responseMapper;
        this.batchJobDataSourceRequestMappersRepository = batchJobDataSourceRequestMappersRepository;
        this.batchJobEndpointsProvider = batchJobEndpointsProvider;
    }

    @Override
    public BatchJobResponse create(String accessToken, BatchJobCreateRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobValidationRestException,
        BatchJobDataSourceValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJobBuilder batchJobBuilder = batchJobService.create(authorization);
            BatchJob batchJob = fillParameters(request, batchJobBuilder);
            return batchResponseMapper.toResponse(authorization, batchJob, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobResponse update(String accessToken, String batchJobId, BatchJobUpdateRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException,
        BatchJobValidationRestException, BatchJobRestException, BatchJobDataSourceValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            UpdateBatchJob.Builder builder = UpdateBatchJob.builder(Id.valueOf(batchJobId));
            request.getName().ifPresent(value -> builder.withName(value));
            request.getScopes().ifPresent(value -> builder.withScopes(mapRestScopesToEntityScopes(value)));
            request.getTags().ifPresent(tags -> builder.withTags(tags));
            return batchResponseMapper.toResponse(authorization, batchJobService.update(authorization, builder.build()),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchJobId)
                .withCause(e)
                .build();
        } catch (BatchJobLockedException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.LOCKED)
                .addParameter("batch_job_id", batchJobId)
                .withCause(e)
                .build();
        } catch (BatchJobNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.NAME_INVALID)
                .withCause(e)
                .build();
        } catch (BatchJobUnauthorizedScopesException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.UNAUTHORIZED_SCOPES)
                .withCause(e)
                .build();
        } catch (BatchJobTagInvalidException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.TAG_INVALID)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobResponse cancel(String accessToken, String batchId, ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJob batchJob = batchJobService.cancel(authorization, Id.valueOf(batchId));
            return batchResponseMapper.toResponse(authorization, batchJob, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobLockedException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.LOCKED)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobInvalidStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.INVALID_STATE_TRANSITION)
                .addParameter("batch_job_id", batchId)
                .addParameter("current_status", e.getInitialStatus())
                .addParameter("target_status", e.getTargetStatus())
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobResponse expire(String accessToken, String batchId, ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJob batchJob = batchJobService.expire(authorization, Id.valueOf(batchId));
            return batchResponseMapper.toResponse(authorization, batchJob, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobLockedException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.LOCKED)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobInvalidStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.INVALID_STATE_TRANSITION)
                .addParameter("batch_job_id", batchId)
                .addParameter("current_status", e.getInitialStatus())
                .addParameter("target_status", e.getTargetStatus())
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobResponse delete(String accessToken, String batchId, ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJob batchJob = batchJobService.delete(authorization, Id.valueOf(batchId));
            return batchResponseMapper.toResponse(authorization, batchJob, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobLockedException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.LOCKED)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (BatchJobDeleteNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.DELETE_NOT_ALLOWED)
                .addParameter("batch_job_id", batchId)
                .addParameter("status", batchResponseMapper.toResponseStatus(e.getStatus()))
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BatchJobResponse> list(String accessToken, BatchJobQueryParams requestParams, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJobListQueryBuilder listFilterBuilder = batchJobService.list(authorization);
            if (requestParams.getName().isPresent()) {
                listFilterBuilder.withName(requestParams.getName().get());
            }
            if (requestParams.getStatus().isPresent()) {
                listFilterBuilder.withStatuses(requestParams.getStatus().get().stream()
                    .map(status -> BatchJobStatus.valueOf(status.name()))
                    .collect(Collectors.toSet()));
            }
            if (requestParams.getUserId().isPresent()) {
                listFilterBuilder.withUserId(Id.valueOf(requestParams.getUserId().get()));
            }
            if (requestParams.getOffset() != null) {
                listFilterBuilder.withOffset(requestParams.getOffset().intValue());
            }
            if (requestParams.getLimit() != null) {
                listFilterBuilder.withLimit(requestParams.getLimit().intValue());
            }
            if (requestParams.getTags().isPresent()) {
                listFilterBuilder.withTags(requestParams.getTags().get());
            }
            if (requestParams.getEventName().isPresent()) {
                listFilterBuilder.withEventName(requestParams.getEventName().get());
            }
            return listFilterBuilder.execute().stream()
                .map(batchJob -> batchResponseMapper.toResponse(authorization, batchJob, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobResponse get(String accessToken, String batchId, ZoneId timeZone)
        throws UserAuthorizationRestException, BatchJobRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BatchJob batchJob = batchJobService.getById(authorization, Id.valueOf(batchId));
            return batchResponseMapper.toResponse(authorization, batchJob, timeZone);
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public BatchJobStatusResponse getBatchJobStatus(String accessToken, String batchId)
        throws UserAuthorizationRestException, BatchJobRestException, BatchJobProgressRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            batchJobService.getById(authorization, Id.valueOf(batchId));

            List<BatchJobStatusResponse> jobStatuses = new ArrayList<>();
            int topicNotFoundExceptions = 0;
            List<BatchJobLocalEndpoints> batchJobLocalEndpoints = batchJobEndpointsProvider.getLocalEndpoints();
            for (BatchJobLocalEndpoints localEndpoint : batchJobLocalEndpoints) {
                try {
                    BatchJobStatusResponse localJobStatus = localEndpoint.getLocalBatchJobStatus(accessToken, batchId);
                    jobStatuses.add(localJobStatus);
                } catch (ExtoleRestException e) {
                    if (e.getErrorCode().equals(BatchJobProgressRestException.PROGRESS_NOT_FOUND.getName())) {
                        topicNotFoundExceptions++;
                    }
                }
            }

            if (topicNotFoundExceptions == batchJobLocalEndpoints.size()) {
                throw RestExceptionBuilder.newBuilder(BatchJobProgressRestException.class)
                    .withErrorCode(BatchJobProgressRestException.PROGRESS_NOT_FOUND)
                    .addParameter("batch_job_id", batchId)
                    .build();
            }
            return jobStatuses.stream()
                .reduce(new BatchJobStatusResponse(0, 0),
                    (status1, status2) -> new BatchJobStatusResponse(
                        status1.getEventsProcessed() + status2.getEventsProcessed(),
                        status1.getEventsProcessed() + status2.getEventsToProcess()));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (BatchJobNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobRestException.class)
                .withErrorCode(BatchJobRestException.NOT_FOUND)
                .addParameter("batch_job_id", batchId)
                .withCause(e)
                .build();
        }
    }

    private BatchJob fillParameters(BatchJobCreateRequest request, BatchJobBuilder batchJobBuilder)
        throws BatchJobValidationRestException, BatchJobDataSourceValidationRestException {
        try {
            request.getName().ifPresent(name -> batchJobBuilder.withName(name));
            request.getTags().ifPresent(tags -> batchJobBuilder.withTags(tags));
            request.getScopes().ifPresent(scopes -> batchJobBuilder.withScopes(mapRestScopesToEntityScopes(scopes)));
            request.getEventName().ifPresent(name -> batchJobBuilder.withEventName(name));
            request.getDefaultEventName().ifPresent(name -> batchJobBuilder.withDefaultEventName(name));
            request.getEventData().ifPresent(data -> batchJobBuilder.withEventData(data));
            request.getEventColumns().ifPresent(columns -> batchJobBuilder.withEventColumns(columns));
            request.getColumns().ifPresent(
                eventColumns -> batchJobBuilder.withColumns(eventColumns.stream()
                    .map(this::mapToColumnPojo)
                    .collect(Collectors.toSet())));
            if (request.getDataSource() != null) {
                batchJobDataSourceRequestMappersRepository.getDataSourceMapper(request.getDataSource().getType())
                    .upload(batchJobBuilder, request.getDataSource());
            }
            return batchJobBuilder.save();
        } catch (BatchJobEventNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.EVENT_NAME_INVALID)
                .withCause(e)
                .build();
        } catch (BatchJobTagInvalidException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.TAG_INVALID)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        } catch (BatchJobNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.NAME_INVALID)
                .withCause(e)
                .build();
        } catch (BatchJobUnauthorizedScopesException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.UNAUTHORIZED_SCOPES)
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceMissingException e) {
            throw RestExceptionBuilder.newBuilder(BatchJobValidationRestException.class)
                .withErrorCode(BatchJobValidationRestException.DATA_SOURCE_EMPTY)
                .withCause(e)
                .build();
        } catch (FileAssetBatchJobDataSourceFormatNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetBatchJobDataSourceValidationRestException.class)
                .withErrorCode(FileAssetBatchJobDataSourceValidationRestException.FILE_ASSET_FORMAT_NOT_SUPPORTED)
                .addParameter("format", e.getFormat())
                .addParameter("supported_formats", e.getSupportedFormats().toString())
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceBuildException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private BatchJobColumnPojo mapToColumnPojo(BatchJobColumnRequest request) {
        if (request instanceof FullNameMatchBatchJobColumnRequest) {
            return mapToFullNameMatchColumnPojo((FullNameMatchBatchJobColumnRequest) request);
        } else if (request instanceof PatternNameMatchBatchJobColumnRequest) {
            return mapToPatternNameMatchColumnPojo((PatternNameMatchBatchJobColumnRequest) request);
        }
        throw new IllegalArgumentException("Unsupported BatchJobColumnRequest type: " + request.getClass());
    }

    private BatchJobColumnPojo mapToFullNameMatchColumnPojo(FullNameMatchBatchJobColumnRequest request) {
        return new FullNameMatchBatchJobColumnPojo(
            BatchJobColumnValidationPolicy.valueOf(request.getValidationPolicy().name()),
            request.getPrefix(),
            request.getName());
    }

    private BatchJobColumnPojo mapToPatternNameMatchColumnPojo(PatternNameMatchBatchJobColumnRequest request) {
        return new PatternNameMatchBatchJobColumnPojo(
            BatchJobColumnValidationPolicy.valueOf(request.getValidationPolicy().name()),
            request.getPrefix(),
            request.getNamePattern());
    }

    private static Set<com.extole.reporting.entity.batch.BatchJobScope>
        mapRestScopesToEntityScopes(Set<BatchJobScope> restScopes) {
        return restScopes.stream()
            .map(restScope -> com.extole.reporting.entity.batch.BatchJobScope.valueOf(restScope.name()))
            .collect(Collectors.toSet());
    }

}
