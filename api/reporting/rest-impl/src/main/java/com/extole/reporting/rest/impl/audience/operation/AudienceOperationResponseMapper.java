package com.extole.reporting.rest.impl.audience.operation;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.operation.AudienceOperation;
import com.extole.reporting.rest.audience.operation.AudienceOperationDetailedResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;

@Component
public class AudienceOperationResponseMapper {

    private final AudienceOperationDataSourceResponseMapperRegistry audienceOperationDataSourceResponseMapperRegistry;

    @Autowired
    public AudienceOperationResponseMapper(
        AudienceOperationDataSourceResponseMapperRegistry audienceOperationDataSourceResponseMapperRegistry) {
        this.audienceOperationDataSourceResponseMapperRegistry = audienceOperationDataSourceResponseMapperRegistry;
    }

    public AudienceOperationResponse toResponse(AudienceOperation audienceOperation, ZoneId timeZone) {
        return new AudienceOperationResponse(Id.valueOf(audienceOperation.getId().getValue()),
            AudienceOperationType.valueOf(audienceOperation.getType().name()),
            audienceOperation.getTags(),
            audienceOperationDataSourceResponseMapperRegistry.getMapper(audienceOperation.getDataSource().getType())
                .toResponse(audienceOperation.getDataSource()));
    }

    public AudienceOperationDetailedResponse toDetailedResponse(AudienceOperation audienceOperation, ZoneId timeZone) {
        return new AudienceOperationDetailedResponse(Id.valueOf(audienceOperation.getId().getValue()),
            AudienceOperationType.valueOf(audienceOperation.getType().name()),
            audienceOperation.getTags(),
            Id.valueOf(audienceOperation.getUserId().getValue()),
            audienceOperationDataSourceResponseMapperRegistry.getMapper(audienceOperation.getDataSource().getType())
                .toResponse(audienceOperation.getDataSource()),
            audienceOperation.getCreatedDate().atZone(timeZone),
            audienceOperation.getUpdatedDate().atZone(timeZone));
    }

}
