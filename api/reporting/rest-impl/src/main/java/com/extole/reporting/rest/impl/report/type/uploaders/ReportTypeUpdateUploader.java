package com.extole.reporting.rest.impl.report.type.uploaders;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ReportTypeUpdateRequest;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;

public interface ReportTypeUpdateUploader<T extends ReportTypeUpdateRequest> {

    ReportType upload(Authorization authorization, String name, T reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException;

    Type getType();
}
