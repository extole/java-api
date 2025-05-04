package com.extole.reporting.rest.file.assets;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FileAssetExpiredRestException extends ExtoleRestException {
    public static final ErrorCode<FileAssetExpiredRestException> EXPIRED = new ErrorCode<>(
        "file_asset_expired", 400, "File Asset is expired", "file_asset_id");

    public FileAssetExpiredRestException(String uniqueId, ErrorCode<FileAssetExpiredRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
