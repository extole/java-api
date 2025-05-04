package com.extole.api.impl.model;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.extole.api.model.RefreshingReportRunner;
import com.extole.reporting.pojo.report.runner.RefreshingReportRunnerPojo;

public final class RefreshingReportRunnerImpl implements RefreshingReportRunner {

    private final RefreshingReportRunnerPojo reportRunner;

    public RefreshingReportRunnerImpl(RefreshingReportRunnerPojo reportRunner) {
        this.reportRunner = reportRunner;
    }

    @Override
    public String getType() {
        return reportRunner.getType().name();
    }

    @Override
    public String getId() {
        return reportRunner.getId().getValue();
    }

    @Override
    public String getName() {
        return reportRunner.getName();
    }

    @Override
    public String getReportTypeName() {
        return reportRunner.getReportTypeName();
    }

    @Override
    public String[] getFormats() {
        return reportRunner.getFormats()
            .stream().map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String getCreatedDate() {
        return reportRunner.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return reportRunner.getUpdatedDate().toString();
    }

    @Override
    public Map<String, String> getParameters() {
        return reportRunner.getParameters().stream()
            .collect(Collectors.toUnmodifiableMap(p -> p.getDetails().getName(), p -> p.getValue()));
    }

    @Override
    public String[] getScopes() {
        return reportRunner.getScopes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String[] getTags() {
        return reportRunner.getTags()
            .toArray(String[]::new);
    }

    @Override
    public String getUserId() {
        return reportRunner.getUserId().getValue();
    }

    @Nullable
    @Override
    public String getSftpServerId() {
        return reportRunner.getSftpServerId().map(value -> value.getValue())
            .orElse(null);
    }

    @Nullable
    @Override
    public PauseInfo pauseInfo() {
        return reportRunner.getPauseInfo().map(PauseInfoImpl::new)
            .orElse(null);
    }

    @Nullable
    @Override
    public MergingConfiguration mergingConfiguration() {
        return reportRunner.getMergingConfiguration().map(MergingConfigurationImpl::new)
            .orElse(null);
    }

    @Override
    public boolean isLegacySftpReportNameFormat() {
        return false;
    }

    @Override
    public Long getExpirationDurationMilliseconds() {
        return Long.valueOf(reportRunner.getExpirationDuration().toMillis());
    }

    private static final class MergingConfigurationImpl implements MergingConfiguration {
        private final com.extole.reporting.entity.report.runner.MergingConfiguration mergingConfiguration;

        private MergingConfigurationImpl(
            com.extole.reporting.entity.report.runner.MergingConfiguration mergingConfiguration) {
            this.mergingConfiguration = mergingConfiguration;
        }

        @Override
        public String[] getSortBy() {
            return mergingConfiguration.getSortBy().toArray(String[]::new);
        }

        @Override
        public String[] getUniqueBy() {
            return mergingConfiguration.getUniqueBy().toArray(String[]::new);
        }

        @Override
        public String[] getFormats() {
            return mergingConfiguration.getFormats()
                .stream().map(value -> value.name())
                .toArray(String[]::new);
        }
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
            return pauseInfo.getUpdatedDate().toString();
        }
    }
}
