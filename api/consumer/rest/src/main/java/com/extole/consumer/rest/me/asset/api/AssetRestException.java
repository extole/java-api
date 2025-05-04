package com.extole.consumer.rest.me.asset.api;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AssetRestException extends ExtoleRestException {

    public static final ErrorCode<AssetRestException> ASSET_NOT_FOUND =
        new ErrorCode<>("asset_not_found", 400, "Asset not found", "criteria", "value");

    public static final ErrorCode<AssetRestException> ASSET_CONTENT_NOT_DOWNLOADABLE =
        new ErrorCode<>("asset_content_not_downloadable", 400, "Asset content could not be downloaded", "asset_id");

    public AssetRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
