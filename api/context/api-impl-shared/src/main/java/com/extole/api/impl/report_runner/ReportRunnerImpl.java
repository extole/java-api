package com.extole.api.impl.report_runner;

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.extole.api.report.Report.ReportFormat;
import com.extole.api.report_runner.ReportRunner;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;
import com.extole.reporting.entity.report.runner.PauseInfo;

public abstract class ReportRunnerImpl implements ReportRunner {
    private final String name;
    private final ReportFormat[] formats;
    private final String type;
    private final String reportTypeName;
    private final String[] tags;
    private final String sftpServerId;
    private final String createdDate;
    private final String updatedDate;
    private final boolean isPaused;
    private final PauseInfo pauseInfo;

    protected ReportRunnerImpl(com.extole.reporting.entity.report.runner.ReportRunner reportRunner) {
        this.name = reportRunner.getName();
        this.formats = reportRunner.getFormats().stream()
            .map(item -> ReportFormat.valueOf(item.name())).collect(Collectors.toSet()).toArray(new ReportFormat[] {});
        this.type = reportRunner.getType().name();
        this.reportTypeName = reportRunner.getReportTypeName();
        this.tags = reportRunner.getTags().toArray(new String[0]);
        this.sftpServerId = reportRunner.getSftpServerId().map(Id::getValue).orElse(null);
        this.createdDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(reportRunner.getCreatedDate());
        this.updatedDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(reportRunner.getUpdatedDate());
        this.pauseInfo = reportRunner.getPauseInfo().map(PauseInfoImpl::new).orElse(null);
        this.isPaused = reportRunner.getPauseInfo().isPresent();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReportFormat[] getFormats() {
        return formats;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getReportTypeName() {
        return reportTypeName;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Nullable
    @Override
    public String getSftpServerId() {
        return sftpServerId;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getUpdatedDate() {
        return updatedDate;
    }

    @Deprecated // TODO remove ENG-22714
    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Nullable
    public PauseInfo getPauseInfo() {
        return pauseInfo;
    }

    private static final class PauseInfoImpl implements PauseInfo {
        private final com.extole.reporting.entity.report.runner.PauseInfo pauseInfo;

        private PauseInfoImpl(com.extole.reporting.entity.report.runner.PauseInfo pauseInfo) {
            this.pauseInfo = pauseInfo;
        }

        @Override
        public String getUserId() {
            return pauseInfo.getUserId();
        }

        @Override
        public String getDescription() {
            return pauseInfo.getDescription().orElse(StringUtils.EMPTY);
        }

        @Override
        public String getUpdatedDate() {
            return ExtoleDateTimeFormatters.ISO_INSTANT.format(pauseInfo.getUpdatedDate());
        }

        @Override
        public String getPausedDate() {
            return ExtoleDateTimeFormatters.ISO_INSTANT.format(pauseInfo.getPausedDate());
        }
    }
}
