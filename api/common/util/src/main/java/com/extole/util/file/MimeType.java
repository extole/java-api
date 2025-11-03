package com.extole.util.file;

import com.google.common.base.Objects;

import com.extole.common.lang.ToString;

public class MimeType {
    public static final MimeType JSON = new MimeType("application/json", "json");
    public static final MimeType JSONL = new MimeType("application/jsonl", "jsonl");
    public static final MimeType PNG = new MimeType("image/png", "png");
    public static final MimeType JPEG = new MimeType("image/jpeg", "jpeg");
    public static final MimeType JPG = new MimeType("image/jpeg", "jpg");
    public static final MimeType GIF = new MimeType("image/gif", "gif");
    public static final MimeType CSV = new MimeType("text/csv", "csv");
    public static final MimeType PSV = new MimeType("text/psv", "psv");
    public static final MimeType PDF = new MimeType("application/pdf", "pdf");
    public static final MimeType XLSX =
        new MimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
    public static final MimeType BINARY = new MimeType("application/octet-stream", "bin");

    private final String mimeType;
    private final String extension;

    public MimeType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension.equalsIgnoreCase("bin") ? "BINARY" : extension.toUpperCase();
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MimeType other = (MimeType) object;
        return Objects.equal(mimeType, other.mimeType) && Objects.equal(extension, other.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mimeType, extension);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
