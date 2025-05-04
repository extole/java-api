package com.extole.reporting.rest.impl.batch;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.batch.BatchJob;
import com.extole.reporting.entity.batch.BatchJobResult;
import com.extole.reporting.entity.batch.BatchJobResult.ErrorCode;
import com.extole.reporting.entity.batch.BatchJobStatus;
import com.extole.reporting.entity.batch.column.BatchJobColumn;
import com.extole.reporting.entity.batch.column.FullNameMatchBatchJobColumn;
import com.extole.reporting.entity.batch.column.PatternNameMatchBatchJobColumn;
import com.extole.reporting.rest.batch.BatchJobResponse;
import com.extole.reporting.rest.batch.BatchJobScope;
import com.extole.reporting.rest.batch.column.BatchJobColumnValidationPolicy;
import com.extole.reporting.rest.batch.column.response.BatchJobColumnResponse;
import com.extole.reporting.rest.batch.column.response.FullNameMatchBatchJobColumnResponse;
import com.extole.reporting.rest.batch.column.response.PatternNameMatchBatchJobColumnResponse;
import com.extole.reporting.rest.impl.batch.data.source.response.BatchJobDataSourceResponseMappersRepository;

@Component
public class BatchJobResponseMapper {

    private static final Set<BatchJobStatus> INTERNAL_PENDING_MAPPABLE_STATUSES =
        Set.of(BatchJobStatus.QUEUED, BatchJobStatus.PENDING_RETRY);

    private final BatchJobDataSourceResponseMappersRepository batchJobDataSourceResponseMappersRepository;

    @Autowired
    public BatchJobResponseMapper(
        BatchJobDataSourceResponseMappersRepository batchJobDataSourceResponseMappersRepository) {
        this.batchJobDataSourceResponseMappersRepository = batchJobDataSourceResponseMappersRepository;

    }

    public BatchJobResponse toResponse(Authorization authorization, BatchJob job, ZoneId timeZone) {
        BatchJobResult result = job.getResult();

        Optional<String> debugMessage = Optional.empty();
        if (authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            debugMessage = result.getDebugMessage();
        }
        return new BatchJobResponse(job.getId().getValue(),
            job.getEventName(),
            job.getDefaultEventName(),
            job.getCreatedDate().atZone(timeZone),
            result.getStartedDate().map(date -> date.atZone(timeZone)),
            result.getCompletedDate().map(date -> date.atZone(timeZone)),
            toResponseStatus(job.getStatus()),
            job.getName(),
            job.getTags(),
            job.getEventData(),
            job.getEventColumns(),
            job.getColumns().stream()
                .map(this::mapToColumnResponse)
                .collect(Collectors.toSet()),
            batchJobDataSourceResponseMappersRepository.getMapper(job.getDataSource().getType())
                .toResponse(job.getDataSource()),
            result.getSuccessRows(),
            result.getFailedRows(),
            result.getTopicName(),
            job.getScopes().stream().map(scope -> BatchJobScope.valueOf(scope.name())).collect(Collectors.toSet()),
            result.getErrorCode().map(ErrorCode::name),
            result.getErrorMessage(),
            debugMessage);
    }

    public com.extole.reporting.rest.batch.BatchJobStatus toResponseStatus(BatchJobStatus status) {
        if (INTERNAL_PENDING_MAPPABLE_STATUSES.contains(status)) {
            return com.extole.reporting.rest.batch.BatchJobStatus.PENDING;
        }
        return com.extole.reporting.rest.batch.BatchJobStatus.valueOf(status.name());
    }

    private BatchJobColumnResponse mapToColumnResponse(BatchJobColumn pojo) {
        if (pojo instanceof FullNameMatchBatchJobColumn) {
            return mapToFullNameMatchColumnResponse((FullNameMatchBatchJobColumn) pojo);
        } else if (pojo instanceof PatternNameMatchBatchJobColumn) {
            return mapToPatternNameMatchColumnResponse((PatternNameMatchBatchJobColumn) pojo);
        }
        throw new IllegalArgumentException("Unsupported BatchJobColumnPojo type: " + pojo.getClass());
    }

    private BatchJobColumnResponse mapToFullNameMatchColumnResponse(FullNameMatchBatchJobColumn pojo) {
        return new FullNameMatchBatchJobColumnResponse(
            BatchJobColumnValidationPolicy.valueOf(pojo.getValidationPolicy().name()),
            pojo.getPrefix(),
            pojo.getName());
    }

    private BatchJobColumnResponse mapToPatternNameMatchColumnResponse(PatternNameMatchBatchJobColumn pojo) {
        return new PatternNameMatchBatchJobColumnResponse(
            BatchJobColumnValidationPolicy.valueOf(pojo.getValidationPolicy().name()),
            pojo.getPrefix(),
            pojo.getNamePattern());
    }

}
