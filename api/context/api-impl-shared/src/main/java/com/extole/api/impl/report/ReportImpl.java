package com.extole.api.impl.report;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.extole.api.report.Report;
import com.extole.api.report.ReportResult;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public class ReportImpl implements Report {
    private final String id;
    private final String name;
    private final String displayName;
    private final String executorType;
    private final ReportFormat[] reportFormats;
    private final String[] tags;
    private final String createdDate;
    private final Optional<String> sftpServerId;
    private final ReportResult result;

    public ReportImpl(com.extole.reporting.entity.report.Report report) {
        this.id = report.getId().getValue();
        this.name = report.getName();
        this.displayName = report.getDisplayName();
        this.executorType = report.getExecutorType().name();
        this.reportFormats = report.getFormats().stream()
            .map(item -> ReportFormat.valueOf(item.name())).collect(Collectors.toSet()).toArray(new ReportFormat[] {});
        this.tags = report.getTags().toArray(new String[] {});
        this.createdDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(report.getCreatedDate());
        this.sftpServerId = report.getSftpServerId().map(Id::getValue);
        this.result = new ReportResultImpl(report.getLastResult());
    }

    @Override
    public ReportFormat[] getReportFormats() {
        return reportFormats;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public ReportResult getReportResult() {
        return result;
    }

    @Override
    public String getExecutorType() {
        return executorType;
    }

    @Nullable
    @Override
    public String getSftpServerId() {
        return sftpServerId.orElse(null);
    }

}
