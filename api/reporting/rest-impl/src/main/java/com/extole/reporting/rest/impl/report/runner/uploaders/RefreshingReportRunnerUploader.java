package com.extole.reporting.rest.impl.report.runner.uploaders;

import java.time.Duration;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.report.runner.RefreshingReportRunner;
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.report.runner.RefreshingReportRunnerBuilder;
import com.extole.model.service.report.runner.RefreshingReportRunnerInvalidExpirationException;
import com.extole.model.service.report.runner.ReportRunnerDuplicateException;
import com.extole.model.service.report.runner.ReportRunnerFormatNotSupportedException;
import com.extole.model.service.report.runner.ReportRunnerInvalidParametersException;
import com.extole.model.service.report.runner.ReportRunnerInvalidScopesException;
import com.extole.model.service.report.runner.ReportRunnerInvalidSortByException;
import com.extole.model.service.report.runner.ReportRunnerMergeEmptyFormatException;
import com.extole.model.service.report.runner.ReportRunnerMissingNameException;
import com.extole.model.service.report.runner.ReportRunnerMissingParametersException;
import com.extole.model.service.report.runner.ReportRunnerNameInvalidException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerReportTypeMissingException;
import com.extole.model.service.report.runner.ReportRunnerReportTypeNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerUpdateManagedByGitException;
import com.extole.reporting.rest.report.runner.RefreshingReportRunnerCreateRequest;
import com.extole.reporting.rest.report.runner.RefreshingReportRunnerUpdateRequest;
import com.extole.reporting.rest.report.runner.RefreshingReportRunnerValidationRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerValidationRestException;

@Component
public class RefreshingReportRunnerUploader
    implements ReportRunnerUploader<RefreshingReportRunnerCreateRequest, RefreshingReportRunnerUpdateRequest> {

    private final ReportRunnerUploaderBase reportRunnerUploaderBase;

    @Autowired
    public RefreshingReportRunnerUploader(ReportRunnerUploaderBase reportRunnerUploaderBase) {
        this.reportRunnerUploaderBase = reportRunnerUploaderBase;
    }

    @Override
    public RefreshingReportRunner upload(Authorization authorization,
        RefreshingReportRunnerCreateRequest reportRunnerRequest)
        throws AuthorizationException, SftpDestinationNotFoundException, ReportRunnerFormatNotSupportedException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeMissingException,
        ReportRunnerInvalidScopesException, ReportRunnerInvalidParametersException, ReportRunnerMissingNameException,
        ReportRunnerReportTypeNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {

        RefreshingReportRunnerBuilder<?> builder =
            (RefreshingReportRunnerBuilder<?>) reportRunnerUploaderBase.upload(authorization, reportRunnerRequest);

        return applyRequestedChanges(builder, reportRunnerRequest);
    }

    @Override
    public ReportRunner upload(Authorization authorization, Id<ReportRunner> reportRunnerId,
        RefreshingReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {

        RefreshingReportRunnerBuilder<?> builder =
            (RefreshingReportRunnerBuilder<?>) reportRunnerUploaderBase.upload(authorization, reportRunnerId,
                reportRunnerRequest);

        return applyRequestedChanges(builder, reportRunnerRequest);
    }

    @Override
    public ReportRunner duplicate(Authorization authorization, Id<ReportRunner> reportRunnerId, boolean allowDuplicate,
        RefreshingReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {

        RefreshingReportRunnerBuilder<?> builder =
            (RefreshingReportRunnerBuilder<?>) reportRunnerUploaderBase.duplicate(authorization, reportRunnerId,
                allowDuplicate, reportRunnerRequest);

        return applyRequestedChanges(builder, reportRunnerRequest);
    }

    private RefreshingReportRunner applyRequestedChanges(RefreshingReportRunnerBuilder<?> builder,
        RefreshingReportRunnerCreateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {

        try {
            reportRunnerRequest.getExpirationMs().map(Duration::ofMillis).ifPresent(builder::withExpirationDuration);
            return builder.save();
        } catch (RefreshingReportRunnerInvalidExpirationException e) {
            throw RestExceptionBuilder.newBuilder(RefreshingReportRunnerValidationRestException.class)
                .withErrorCode(RefreshingReportRunnerValidationRestException.REPORT_RUNNER_INVALID_EXPIRATION_MS)
                .withCause(e)
                .build();
        } catch (DuplicateKeyException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_NAME_EXISTS)
                .addParameter("name", reportRunnerRequest.getName()).withCause(e)
                .build();
        } catch (ReportRunnerUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_LOCKED)
                .withCause(e)
                .build();
        } catch (ReportRunnerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_DUPLICATE)
                .addParameter("report_runner_ids",
                    e.getReportRunnerIds().stream().map(Id::getValue).collect(Collectors.toList()))
                .withCause(e)
                .build();
        } catch (ReportRunnerNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_NAME_ILLEGAL_CHARACTER)
                .addParameter("name", reportRunnerRequest.getName())
                .withCause(e)
                .build();
        } catch (ReportRunnerInvalidSortByException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_SORT_BY)
                .addParameter("sort_by", e.getSortBy())
                .withCause(e)
                .build();
        }
    }

    private RefreshingReportRunner applyRequestedChanges(RefreshingReportRunnerBuilder<?> builder,
        RefreshingReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {
        try {
            reportRunnerRequest.getExpirationMs().map(Duration::ofMillis).ifPresent(builder::withExpirationDuration);
            return builder.save();
        } catch (RefreshingReportRunnerInvalidExpirationException e) {
            throw RestExceptionBuilder.newBuilder(RefreshingReportRunnerValidationRestException.class)
                .withErrorCode(RefreshingReportRunnerValidationRestException.REPORT_RUNNER_INVALID_EXPIRATION_MS)
                .withCause(e)
                .build();
        } catch (DuplicateKeyException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_NAME_EXISTS)
                .addParameter("name", reportRunnerRequest.getName()).withCause(e)
                .build();
        } catch (ReportRunnerUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_LOCKED)
                .withCause(e)
                .build();
        } catch (ReportRunnerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_DUPLICATE)
                .addParameter("report_runner_ids",
                    e.getReportRunnerIds().stream().map(Id::getValue).collect(Collectors.toList()))
                .withCause(e)
                .build();
        } catch (ReportRunnerNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_NAME_ILLEGAL_CHARACTER)
                .addParameter("name", reportRunnerRequest.getName())
                .withCause(e)
                .build();
        } catch (ReportRunnerInvalidSortByException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_SORT_BY)
                .addParameter("sort_by", e.getSortBy())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportRunnerType getType() {
        return ReportRunnerType.REFRESHING;
    }
}
