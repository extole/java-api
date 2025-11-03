package com.extole.reporting.rest.impl.report.runner.uploaders;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.id.Id;
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerFormatNotSupportedException;
import com.extole.model.service.report.runner.ReportRunnerInvalidParametersException;
import com.extole.model.service.report.runner.ReportRunnerInvalidScopesException;
import com.extole.model.service.report.runner.ReportRunnerMergeEmptyFormatException;
import com.extole.model.service.report.runner.ReportRunnerMissingNameException;
import com.extole.model.service.report.runner.ReportRunnerMissingParametersException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerReportTypeMissingException;
import com.extole.model.service.report.runner.ReportRunnerReportTypeNotFoundException;
import com.extole.reporting.rest.report.runner.ReportRunnerCreateRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerUpdateRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerValidationRestException;

public interface ReportRunnerUploader<C extends ReportRunnerCreateRequest, U extends ReportRunnerUpdateRequest> {

    ReportRunner upload(Authorization authorization, C reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException,
        ReportRunnerValidationRestException, ReportRunnerMergeEmptyFormatException;

    ReportRunner upload(Authorization authorization, Id<ReportRunner> reportRunnerId, U reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException,
        ReportRunnerReportTypeMissingException, ReportRunnerMissingParametersException,
        ReportRunnerReportTypeNotFoundException, ReportRunnerFormatNotSupportedException,
        ReportRunnerInvalidParametersException, ReportRunnerInvalidScopesException, SftpDestinationNotFoundException,
        ReportRunnerValidationRestException, ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException;

    ReportRunner duplicate(Authorization authorization, Id<ReportRunner> reportRunnerId, boolean allowDuplicate,
        U reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException,
        ReportRunnerReportTypeMissingException, ReportRunnerMissingParametersException,
        ReportRunnerReportTypeNotFoundException, ReportRunnerFormatNotSupportedException,
        ReportRunnerInvalidParametersException, ReportRunnerInvalidScopesException, SftpDestinationNotFoundException,
        ReportRunnerValidationRestException, ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException;

    ReportRunnerType getType();
}
