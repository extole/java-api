package com.extole.reporting.rest.impl.audience.operation;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.PersonListAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;

public interface AudienceOperationDataSourceRequestUploader<REQUEST extends AudienceOperationDataSourceRequest> {

    void upload(AudienceOperationBuilder builder, REQUEST createRequest)
        throws ReportAudienceOperationDataSourceValidationRestException,
        PersonListAudienceOperationDataSourceValidationRestException,
        FileAssetAudienceOperationDataSourceValidationRestException,
        ActionAudienceOperationDataSourceValidationRestException;

    AudienceOperationDataSourceType getType();

}
