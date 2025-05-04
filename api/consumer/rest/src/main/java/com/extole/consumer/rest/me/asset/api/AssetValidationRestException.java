package com.extole.consumer.rest.me.asset.api;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AssetValidationRestException extends ExtoleRestException {

    public static final ErrorCode<AssetValidationRestException> ASSET_SIZE_INVALID = new ErrorCode<>(
        "asset_size_invalid", 400, "The supplied asset has an invalid size", "size", "max_allowed_size");

    public static final ErrorCode<AssetValidationRestException> ASSET_MIME_TYPE_INVALID = new ErrorCode<>(
        "asset_mime_type_invalid", 400, "The supplied asset has an invalid mime type", "mime_type");

    public static final ErrorCode<AssetValidationRestException> ASSET_LIMIT_EXCEEDED =
        new ErrorCode<>("asset_limit_exceeded", 400, "The number of assets exceeds the limit", "limit");

    public AssetValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
