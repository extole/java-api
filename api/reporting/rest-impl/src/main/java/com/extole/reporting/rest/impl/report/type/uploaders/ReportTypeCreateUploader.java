package com.extole.reporting.rest.impl.report.type.uploaders;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ReportTypeCreateRequest;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;

public interface ReportTypeCreateUploader<T extends ReportTypeCreateRequest> {

    ReportType upload(Authorization authorization, T reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException;

    Type getType();
}
