package com.extole.client.rest.media;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class MediaAssetRestException extends ExtoleRestException {
    public static final ErrorCode<MediaAssetRestException> MEDIA_ASSET_NOT_FOUND =
        new ErrorCode<>("media_asset_not_found", 400, "Media asset not found", "asset_id");

    public MediaAssetRestException(String uniqueId, ErrorCode<MediaAssetRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
