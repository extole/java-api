package com.extole.reporting.rest.impl.audience.member;

import java.util.Arrays;
import java.util.Optional;

public enum AudienceMemberDownloadFormat {

    JSON("application/json", "json"),
    CSV("text/csv", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

    private final String mimeType;
    private final String extension;

    AudienceMemberDownloadFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static AudienceMemberDownloadFormat valueOfMimeType(String mimeType) {
        Optional<AudienceMemberDownloadFormat> fileFormat =
            Arrays.stream(AudienceMemberDownloadFormat.values())
                .filter(format -> format.mimeType.equalsIgnoreCase(mimeType)).findAny();
        return fileFormat.orElseThrow(
            () -> new IllegalArgumentException("Could not determine format from mime-type " + mimeType));
    }

}
