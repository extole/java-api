package com.extole.client.rest.person.asset;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonAssetValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PersonAssetValidationRestException> ASSET_SIZE_INVALID =
        new ErrorCode<>("asset_size_invalid", 400, "The supplied asset has an invalid size", "person_id", "size",
            "max_allowed_size");

    public static final ErrorCode<PersonAssetValidationRestException> ASSET_MIME_TYPE_INVALID = new ErrorCode<>(
        "asset_mime_type_invalid", 400, "The supplied asset has an invalid mime type", "person_id", "mime_type");

    public static final ErrorCode<PersonAssetValidationRestException> ASSET_LIMIT_EXCEEDED =
        new ErrorCode<>("asset_limit_exceeded", 400, "The number of assets exceeds the limit", "person_id", "limit");

    public PersonAssetValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
