package com.extole.reporting.rest.impl.audience.operation.action;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceRequestUploader;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationDataSourceBuildException;
import com.extole.reporting.service.audience.operation.AudienceOperationParameterUpdateNotAllowedException;
import com.extole.reporting.service.audience.operation.action.data.source.ActionAudienceOperationDataSourceBuilder;
import com.extole.reporting.service.audience.operation.action.data.source.ActionAudienceOperationDataSourceMissingEventNameException;

@Component
public class ActionAudienceOperationDataSourceRequestUploader
    implements AudienceOperationDataSourceRequestUploader<ActionAudienceOperationDataSourceRequest> {

    @Override
    public void upload(AudienceOperationBuilder builder, ActionAudienceOperationDataSourceRequest request)
        throws ActionAudienceOperationDataSourceValidationRestException {
        try {
            ActionAudienceOperationDataSourceBuilder sourceBuilder =
                builder.withDataSource(AudienceOperationDataSourceType.ACTION);
            request.getEventColumns().ifPresent(eventColumns -> sourceBuilder.withEventColumns(eventColumns));
            request.getEventData().ifPresent(eventData -> sourceBuilder.withEventData(eventData));
            sourceBuilder.withEventName(request.getEventName())
                .done();
        } catch (ActionAudienceOperationDataSourceMissingEventNameException e) {
            throw RestExceptionBuilder.newBuilder(ActionAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(ActionAudienceOperationDataSourceValidationRestException.MISSING_EVENT_NAME)
                .withCause(e)
                .build();
        } catch (AudienceOperationDataSourceBuildException | AudienceOperationParameterUpdateNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.ACTION;
    }

}
