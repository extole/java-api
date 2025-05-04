package com.extole.api.impl.model;

import javax.annotation.Nullable;

import com.extole.api.model.ConfiguredReportType;
import com.extole.common.lang.ToString;
import com.extole.reporting.entity.report.ReportTypeColumn;
import com.extole.reporting.pojo.report.type.ConfiguredReportTypePojo;

final class ConfiguredReportTypeImpl implements ConfiguredReportType {

    private final ConfiguredReportTypePojo configuredReportType;

    ConfiguredReportTypeImpl(ConfiguredReportTypePojo configuredReportType) {
        this.configuredReportType = configuredReportType;
    }

    @Override
    public String getId() {
        return configuredReportType.getId().getValue();
    }

    @Override
    public String getType() {
        return configuredReportType.getType().name();
    }

    @Override
    public String getName() {
        return configuredReportType.getName();
    }

    @Override
    public String getDisplayName() {
        return configuredReportType.getDisplayName();
    }

    @Override
    public String getDescription() {
        return configuredReportType.getDescription();
    }

    @Override
    public String[] getCategories() {
        return configuredReportType.getCategories().toArray(String[]::new);
    }

    @Override
    public String[] getScopes() {
        return configuredReportType.getScopes().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getVisibility() {
        return configuredReportType.getVisibility().name();
    }

    @Override
    public String[] getTags() {
        return configuredReportType.getTags().stream().map(value -> value.getName()).toArray(String[]::new);
    }

    @Override
    public String[] getFormats() {
        return configuredReportType.getFormats().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getDataStart() {
        return configuredReportType.getDataStart().toString();
    }

    @Override
    public String[] getAllowedScopes() {
        return configuredReportType.getAllowedScopes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String getCreatedDate() {
        return configuredReportType.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return configuredReportType.getUpdatedDate().toString();
    }

    @Override
    public PreviewColumn[] getPreviewColumns() {
        return configuredReportType.getPreviewColumns().stream()
            .map(PreviewColumnImpl::new)
            .toArray(PreviewColumn[]::new);
    }

    @Override
    public Parameter[] getParameters() {
        return configuredReportType.getParameters()
            .stream()
            .map(value -> new ParameterImpl(value.getName(), value.getDefaultValue().orElse(null)))
            .toArray(Parameter[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private static final class ParameterImpl implements Parameter {
        private final String name;
        private final String value;

        private ParameterImpl(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public String getValue() {
            return value;
        }
    }

    private static final class PreviewColumnImpl implements PreviewColumn {
        private final ReportTypeColumn reportTypeColumn;

        private PreviewColumnImpl(ReportTypeColumn reportTypeColumn) {
            this.reportTypeColumn = reportTypeColumn;
        }

        @Override
        public String getName() {
            return reportTypeColumn.getName();
        }

        @Nullable
        @Override
        public String getSampleValue() {
            return reportTypeColumn.getSampleValue().orElse(null);
        }

        @Nullable
        @Override
        public String getNote() {
            return reportTypeColumn.getNote().orElse(null);
        }
    }

}
