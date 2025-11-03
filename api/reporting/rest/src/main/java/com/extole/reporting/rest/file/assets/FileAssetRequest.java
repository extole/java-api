package com.extole.reporting.rest.file.assets;

import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.request.FileInputStreamRequest;

public class FileAssetRequest {

    private final FileAssetMetadata fileAssetMetadata;
    private final FileInputStreamRequest fileInputStreamRequest;

    public FileAssetRequest(
        @Parameter(description = "Optional FileAsset metadata") FileAssetMetadata fileAssetMetadata,
        @Parameter(description = "File content") FileInputStreamRequest fileInputStreamRequest) {
        this.fileAssetMetadata = fileAssetMetadata;
        this.fileInputStreamRequest = fileInputStreamRequest;
    }

    public Optional<FileAssetMetadata> getFileAssetMetadata() {
        return Optional.ofNullable(fileAssetMetadata);
    }

    public FileInputStreamRequest getFileInputStreamRequest() {
        return fileInputStreamRequest;
    }
}
