package com.extole.file.parser.content;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum FileContentParseFormat {
    JSON("application/json", "json"),
    CSV("text/csv", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

    private static final Map<String, FileContentParseFormat> FORMATS_BY_MIME_TYPES;
    private static final Map<String, FileContentParseFormat> FORMATS_BY_EXTENSIONS;

    static {
        EnumSet<FileContentParseFormat> formatsElements = EnumSet.allOf(FileContentParseFormat.class);
        FORMATS_BY_MIME_TYPES = formatsElements.stream().collect(Collectors.toUnmodifiableMap(
            value -> value.getMimeType(), Function.identity()));
        FORMATS_BY_EXTENSIONS = formatsElements.stream().collect(Collectors.toUnmodifiableMap(
            value -> value.getExtension(), Function.identity()));
    }

    private final String mimeType;
    private final String extension;

    FileContentParseFormat(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<FileContentParseFormat> fromMimeType(String mimeType) {
        return Optional.ofNullable(FORMATS_BY_MIME_TYPES.get(mimeType));
    }

    public static Optional<FileContentParseFormat> fromExtension(String extension) {
        return Optional.ofNullable(FORMATS_BY_EXTENSIONS.get(extension));
    }
}
