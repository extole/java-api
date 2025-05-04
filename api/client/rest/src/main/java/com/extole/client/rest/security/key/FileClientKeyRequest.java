package com.extole.client.rest.security.key;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.request.FileInputStreamRequest;

public class FileClientKeyRequest<REQUEST extends FileBasedClientKeyCreateRequest> {

    private final REQUEST clientKeyCreateRequest;
    private final FileInputStreamRequest fileInputStreamRequest;

    public FileClientKeyRequest(
        @Parameter(description = "Client key create request") REQUEST clientKeyCreateRequest,
        @Parameter(description = "Binary key content") FileInputStreamRequest fileInputStreamRequest) {
        this.clientKeyCreateRequest = clientKeyCreateRequest;
        this.fileInputStreamRequest = fileInputStreamRequest;
    }

    public REQUEST getClientKeyCreateRequest() {
        return clientKeyCreateRequest;
    }

    public FileInputStreamRequest getFileInputStreamRequest() {
        return fileInputStreamRequest;
    }
}
