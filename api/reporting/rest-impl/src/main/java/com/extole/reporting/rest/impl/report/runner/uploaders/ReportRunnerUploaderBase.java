package com.extole.reporting.rest.impl.report.runner.uploaders;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.SftpDestination;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.runner.ReportRunner;
import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.runner.ReportRunnerCreateRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerUpdateRequest;
import com.extole.reporting.service.report.runner.MergingConfigurationBuilder;
import com.extole.reporting.service.report.runner.PauseInfoBuilder;
import com.extole.reporting.service.report.runner.ReportRunnerBuilder;
import com.extole.reporting.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.service.report.runner.ReportRunnerService;

@Component
public class ReportRunnerUploaderBase {

    private final ReportRunnerService reportRunnerService;

    @Autowired
    public ReportRunnerUploaderBase(ReportRunnerService reportRunnerService) {
        this.reportRunnerService = reportRunnerService;
    }

    ReportRunnerBuilder<?, ?> upload(Authorization authorization, Id<ReportRunner> reportRunnerId,
        ReportRunnerUpdateRequest reportRunnerRequest)
        throws AuthorizationException, ReportRunnerNotFoundException {
        ReportRunnerBuilder<?, ?> builder = reportRunnerService.update(authorization, reportRunnerId);

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    ReportRunnerBuilder<?, ?> upload(Authorization authorization, ReportRunnerCreateRequest reportRunnerRequest)
        throws AuthorizationException {
        ReportRunnerBuilder<?, ?> builder =
            reportRunnerService.create(authorization, ReportRunnerType.valueOf(reportRunnerRequest.getType().name()));

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    ReportRunnerBuilder<?, ?> duplicate(Authorization authorization, Id<ReportRunner> reportRunnerId,
        boolean allowDuplicate, ReportRunnerUpdateRequest reportRunnerRequest)
        throws ReportRunnerNotFoundException {
        ReportRunnerBuilder<?, ?> builder =
            reportRunnerService.duplicate(authorization, reportRunnerId, allowDuplicate);

        return applyRequestedChanges(reportRunnerRequest, builder);
    }

    private ReportRunnerBuilder<?, ?> applyRequestedChanges(ReportRunnerCreateRequest reportRunnerRequest,
        ReportRunnerBuilder<?, ?> builder) {

        builder.withName(reportRunnerRequest.getName());
        builder.withReportTypeName(reportRunnerRequest.getReportType());
        reportRunnerRequest.getParameters().ifPresent(builder::withParameters);
        reportRunnerRequest.getFormats().ifPresent(
            formats -> builder.withFormats(
                formats.stream()
                    .map(reportFormat -> Report.Format.valueOf(reportFormat.name()))
                    .collect(Collectors.toCollection(LinkedHashSet::new))));
        reportRunnerRequest.getTags().ifPresent(builder::withTags);
        reportRunnerRequest.getScopes().ifPresent(
            scopes -> builder.withScopes(scopes.stream()
                .map(ReportTypeScope::name)
                .map(ReportType.Scope::valueOf)
                .collect(Collectors.toSet())));
        reportRunnerRequest.getSftpServerId().ifPresent(sftpServerId -> builder
            .withSftpServerId(
                sftpServerId.filter(id -> !Strings.isNullOrEmpty(id)).map(id -> Id.<SftpDestination>valueOf(id))
                    .orElse(null)));
        reportRunnerRequest.getPauseInfo().ifPresent(pauseInfo -> {
            if (pauseInfo.isPresent()) {
                PauseInfoBuilder pauseInfoBuilder = builder.withPauseInfo();
                pauseInfo.get().getPaused().ifPresent(pause -> pauseInfoBuilder.withPaused(pause.booleanValue()));
                pauseInfo.get().getDescription()
                    .ifPresent(description -> pauseInfoBuilder.withDescription(description.orElse(null)));
            } else {
                builder.withPauseInfo().withPaused(false);
            }
        });
        reportRunnerRequest.getMergingConfiguration()
            .ifPresent(mergingConfiguration -> {
                if (mergingConfiguration.isPresent()) {
                    MergingConfigurationBuilder configurationBuilder = builder.addMergingConfiguration();
                    mergingConfiguration.get().getFormats()
                        .ifPresent(formats -> configurationBuilder.withFormats(formats.stream()
                            .map(reportFormat -> Report.Format.valueOf(reportFormat.name()))
                            .collect(Collectors.toCollection(LinkedHashSet::new))));
                    mergingConfiguration.get().getSortBy().ifPresent(configurationBuilder::withSortBy);
                    mergingConfiguration.get().getUniqueBy().ifPresent(configurationBuilder::withUniqueBy);
                } else {
                    builder.removeMergingConfiguration();
                }
            });
        reportRunnerRequest.getDeliverEmptyReportsToSftp().ifPresent(deliverEmptyReportsToSftp -> {
            if (deliverEmptyReportsToSftp.isPresent()) {
                deliverEmptyReportsToSftp.ifPresent(builder::withDeliverEmptyReportsToSftp);
            } else {
                builder.withDeliverEmptyReportsToSftp(Boolean.TRUE);
            }
        });
        reportRunnerRequest.getReportNamePattern().ifPresent(builder::withReportNamePattern);
        reportRunnerRequest.getSftpReportNamePattern().ifPresent(builder::withSftpReportNamePattern);
        return builder;
    }

    private ReportRunnerBuilder<?, ?> applyRequestedChanges(ReportRunnerUpdateRequest reportRunnerRequest,
        ReportRunnerBuilder<?, ?> builder) {
        reportRunnerRequest.getName().ifPresent(builder::withName);
        reportRunnerRequest.getReportType().ifPresent(builder::withReportTypeName);
        reportRunnerRequest.getParameters().ifPresent(builder::withParameters);
        reportRunnerRequest.getFormats().ifPresent(
            formats -> builder.withFormats(
                formats.stream()
                    .map(reportFormat -> Report.Format.valueOf(reportFormat.name()))
                    .collect(Collectors.toCollection(LinkedHashSet::new))));
        reportRunnerRequest.getTags().ifPresent(builder::withTags);
        reportRunnerRequest.getScopes().ifPresent(
            scopes -> builder.withScopes(scopes.stream()
                .map(ReportTypeScope::name)
                .map(ReportType.Scope::valueOf)
                .collect(Collectors.toSet())));
        reportRunnerRequest.getSftpServerId().ifPresent(sftpServerId -> builder
            .withSftpServerId(
                sftpServerId.filter(id -> !Strings.isNullOrEmpty(id)).map(id -> Id.<SftpDestination>valueOf(id))
                    .orElse(null)));
        reportRunnerRequest.getPauseInfo().ifPresent(pauseInfo -> {
            if (pauseInfo.isPresent()) {
                PauseInfoBuilder pauseInfoBuilder = builder.withPauseInfo();
                pauseInfo.get().getPaused().ifPresent(pause -> pauseInfoBuilder.withPaused(pause.booleanValue()));
                pauseInfo.get().getDescription()
                    .ifPresent(description -> pauseInfoBuilder.withDescription(description.orElse(null)));
            } else {
                builder.withPauseInfo().withPaused(false);
            }
        });
        reportRunnerRequest.getMergingConfiguration()
            .ifPresent(mergingConfiguration -> {
                if (mergingConfiguration.isPresent()) {
                    MergingConfigurationBuilder configurationBuilder = builder.addMergingConfiguration();
                    mergingConfiguration.get().getFormats()
                        .ifPresent(formats -> configurationBuilder.withFormats(formats.stream()
                            .map(reportFormat -> Report.Format.valueOf(reportFormat.name()))
                            .collect(Collectors.toCollection(LinkedHashSet::new))));
                    mergingConfiguration.get().getSortBy().ifPresent(configurationBuilder::withSortBy);
                    mergingConfiguration.get().getUniqueBy().ifPresent(configurationBuilder::withUniqueBy);
                } else {
                    builder.removeMergingConfiguration();
                }
            });
        reportRunnerRequest.getDeliverEmptyReportsToSftp().ifPresent(deliverEmptyReportsToSftp -> {
            if (deliverEmptyReportsToSftp.isPresent()) {
                deliverEmptyReportsToSftp.ifPresent(builder::withDeliverEmptyReportsToSftp);
            } else {
                builder.withDeliverEmptyReportsToSftp(Boolean.TRUE);
            }
        });
        reportRunnerRequest.getReportNamePattern().ifPresent(builder::withReportNamePattern);
        reportRunnerRequest.getSftpReportNamePattern().ifPresent(builder::withSftpReportNamePattern);
        return builder;
    }
}
