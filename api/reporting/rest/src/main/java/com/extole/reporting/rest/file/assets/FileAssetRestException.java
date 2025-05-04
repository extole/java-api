package com.extole.reporting.rest.file.assets;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FileAssetRestException extends ExtoleRestException {
    public static final ErrorCode<FileAssetRestException> NOT_FOUND = new ErrorCode<>(
        "file_asset_not_found", 400, "File Asset not found", "file_asset_id");

    public FileAssetRestException(String uniqueId, ErrorCode<FileAssetRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
