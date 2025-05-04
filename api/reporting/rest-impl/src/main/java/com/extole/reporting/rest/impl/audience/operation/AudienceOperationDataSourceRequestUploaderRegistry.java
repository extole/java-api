package com.extole.reporting.rest.impl.audience.operation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;

@Component
public class AudienceOperationDataSourceRequestUploaderRegistry {

    private final Map<AudienceOperationDataSourceType, AudienceOperationDataSourceRequestUploader<?>> uploadersByType;

    @Autowired
    public AudienceOperationDataSourceRequestUploaderRegistry(
        List<AudienceOperationDataSourceRequestUploader<?>> uploaders) {
        this.uploadersByType = uploaders.stream().collect(Collectors.toMap(item -> item.getType(),
            Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <REQUEST extends AudienceOperationDataSourceRequest>
        AudienceOperationDataSourceRequestUploader<REQUEST> getUploader(AudienceOperationDataSourceType type) {
        AudienceOperationDataSourceRequestUploader<REQUEST> uploader =
            (AudienceOperationDataSourceRequestUploader<REQUEST>) uploadersByType.get(type);
        if (uploader == null) {
            throw new IllegalStateException("Unsupported data source type: " + type);
        }

        return uploader;
    }

}
