package com.extole.common.rest.request;

import java.io.InputStream;

public final class FileInputStreamRequest implements AutoCloseable {

    private final InputStream inputStream;
    private final FileAttributes attributes;

    public FileInputStreamRequest(InputStream inputStream, FileAttributes attributes) {
        this.inputStream = inputStream;
        this.attributes = attributes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public FileAttributes getAttributes() {
        return attributes;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
