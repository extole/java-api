package com.extole.api.model;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ConfiguredReportType extends ReportType {
    String getCreatedDate();

    String getUpdatedDate();

    PreviewColumn[] getPreviewColumns();

    Parameter[] getParameters();

    interface PreviewColumn {
        String getName();

        @Nullable
        String getSampleValue();

        @Nullable
        String getNote();
    }

    interface Parameter {
        String getName();

        @Nullable
        String getValue();
    }
}
