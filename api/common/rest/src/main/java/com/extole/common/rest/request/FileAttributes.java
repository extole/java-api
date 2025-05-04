package com.extole.common.rest.request;

public final class FileAttributes {

    private final String fileName;
    private final long size;

    public FileAttributes(String fileName, long size) {
        this.fileName = fileName;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }
}
