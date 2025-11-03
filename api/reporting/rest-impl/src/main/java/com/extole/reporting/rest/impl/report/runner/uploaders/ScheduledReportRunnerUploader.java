package com.extole.reporting.rest.impl.report.runner.uploaders;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.entity.report.runner.ScheduledReportRunner;
import com.extole.model.entity.report.runner.schedule.ScheduleFrequency;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
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
import com.extole.model.service.report.runner.ScheduledReportRunnerBuilder;
import com.extole.model.service.report.runner.ScheduledReportRunnerFrequencyNotSupportedForLegacySftpException;
import com.extole.model.service.report.runner.ScheduledReportRunnerMissingFrequencyException;
import com.extole.model.service.report.runner.ScheduledReportRunnerMissingScheduleStartDateException;
import com.extole.reporting.rest.report.runner.ReportRunnerValidationRestException;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerCreateRequest;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerUpdateRequest;

@Component
public class ScheduledReportRunnerUploader
    implements ReportRunnerUploader<ScheduledReportRunnerCreateRequest, ScheduledReportRunnerUpdateRequest> {

    private final ReportRunnerUploaderBase reportRunnerUploaderBase;

    @Autowired
    public ScheduledReportRunnerUploader(ReportRunnerUploaderBase reportRunnerUploaderBase) {
        this.reportRunnerUploaderBase = reportRunnerUploaderBase;
    }

    @Override
    public ScheduledReportRunner upload(Authorization authorization,
        ScheduledReportRunnerCreateRequest reportRunnerRequest)
        throws AuthorizationException, SftpDestinationNotFoundException, ReportRunnerFormatNotSupportedException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeMissingException,
        ReportRunnerInvalidScopesException, ReportRunnerInvalidParametersException, ReportRunnerMissingNameException,
        ReportRunnerReportTypeNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {

        ScheduledReportRunnerBuilder<?> builder =
            (ScheduledReportRunnerBuilder<?>) reportRunnerUploaderBase.upload(authorization, reportRunnerRequest);

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    @Override
    public ReportRunner upload(Authorization authorization, Id<ReportRunner> reportRunnerId,
        ScheduledReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {

        ScheduledReportRunnerBuilder<?> builder =
            (ScheduledReportRunnerBuilder<?>) reportRunnerUploaderBase.upload(authorization, reportRunnerId,
                reportRunnerRequest);

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    @Override
    public ReportRunner duplicate(Authorization authorization, Id<ReportRunner> reportRunnerId, boolean allowDuplicate,
        ScheduledReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {

        ScheduledReportRunnerBuilder<?> builder =
            (ScheduledReportRunnerBuilder<?>) reportRunnerUploaderBase.duplicate(authorization, reportRunnerId,
                allowDuplicate, reportRunnerRequest);

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    private ScheduledReportRunner applyRequestedChanges(ScheduledReportRunnerCreateRequest reportRunnerRequest,
        ScheduledReportRunnerBuilder<?> builder)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {
        reportRunnerRequest.getFrequency()
            .map(com.extole.reporting.rest.report.schedule.ScheduleFrequency::name)
            .map(ScheduleFrequency::valueOf)
            .ifPresent(builder::withFrequency);
        if (reportRunnerRequest.getScheduleStartDate() != null) {
            builder.withScheduleStartDate(reportRunnerRequest.getScheduleStartDate().toInstant());
        }
        reportRunnerRequest.isLegacySftpReportNameFormat().ifPresent(builder::withLegacySftpReportNameFormat);

        try {
            return builder.save();
        } catch (ScheduledReportRunnerMissingScheduleStartDateException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_START_DATE).withCause(e)
                .build();
        } catch (ScheduledReportRunnerMissingFrequencyException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_FREQUENCY).withCause(e)
                .build();
        } catch (ScheduledReportRunnerFrequencyNotSupportedForLegacySftpException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(
                    ReportRunnerValidationRestException.REPORT_RUNNER_FREQUENCY_NOT_SUPPORTED_FOR_LEGACY_SFTP)
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

    private ScheduledReportRunner applyRequestedChanges(ScheduledReportRunnerUpdateRequest reportRunnerRequest,
        ScheduledReportRunnerBuilder<?> builder)
        throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerReportTypeMissingException,
        ReportRunnerMissingParametersException, ReportRunnerReportTypeNotFoundException,
        ReportRunnerFormatNotSupportedException, ReportRunnerInvalidParametersException,
        ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
        ReportRunnerMergeEmptyFormatException {
        reportRunnerRequest.getFrequency()
            .map(com.extole.reporting.rest.report.schedule.ScheduleFrequency::name)
            .map(ScheduleFrequency::valueOf)
            .ifPresent(builder::withFrequency);
        reportRunnerRequest.getScheduleStartDate().map(ZonedDateTime::toInstant)
            .ifPresent(builder::withScheduleStartDate);
        reportRunnerRequest.isLegacySftpReportNameFormat().ifPresent(builder::withLegacySftpReportNameFormat);

        try {
            return builder.save();
        } catch (ScheduledReportRunnerMissingScheduleStartDateException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_START_DATE).withCause(e)
                .build();
        } catch (ScheduledReportRunnerMissingFrequencyException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_FREQUENCY).withCause(e)
                .build();
        } catch (ScheduledReportRunnerFrequencyNotSupportedForLegacySftpException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(
                    ReportRunnerValidationRestException.REPORT_RUNNER_FREQUENCY_NOT_SUPPORTED_FOR_LEGACY_SFTP)
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
        return ReportRunnerType.SCHEDULED;
    }
}
