package com.extole.reporting.rest.report.execution;

import java.util.Arrays;
import java.util.Optional;

public enum ReportFormat {
    JSON("application/json", "json"),
    JSONL("application/jsonl", "jsonl"),
    CSV("text/csv", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    PSV("text/psv", "psv"),
    HEADLESS_CSV("text/csv", "headless.csv"),
    HEADLESS_PSV("text/psv", "headless.psv");

    private final String mimeType;
    private final String extension;

    ReportFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static ReportFormat valueOfMimeType(String mimeType) {
        Optional<ReportFormat> reportFormat =
            Arrays.stream(ReportFormat.values()).filter(format -> format.mimeType.equalsIgnoreCase(mimeType)).findAny();
        return reportFormat.orElseThrow(
            () -> new IllegalArgumentException("Could not determine format from mime-type " + mimeType));
    }
}
