package com.extole.client.rest.security.key;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientKeyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_MISSING = new ErrorCode<>(
        "client_key_missing", 400, "Key value is not specified");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_MISSING_ALGORITHM =
        new ErrorCode<>(
            "client_key_missing_algorithm", 400, "Key algorithm is not specified");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_TOO_LONG =
        new ErrorCode<>(
            "client_key_too_long", 400, "Key does not satisfy max length requirement for the associated algorithm",
            "max_allowed_length");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_TOO_SHORT =
        new ErrorCode<>(
            "client_key_too_short", 400, "Key does not satisfy min length requirement for the associated algorithm",
            "min_required_length", "algorithm");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_INVALID =
        new ErrorCode<>("client_key_invalid", 400, "Invalid key");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_DUPLICATE_PARTNER_KEY_ID =
        new ErrorCode<>("client_key_duplicate_partner_key_id", 400, "Key with such partner key id already defined",
            "partner_key_id");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_INVALID_PARTNER_KEY_ID =
        new ErrorCode<>("client_key_invalid_partner_key_id", 400, "Partner key id should be up to 240 characters",
            "partner_key_id");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_MISSING_TYPE = new ErrorCode<>(
        "client_key_missing_type", 400, "Key type is not specified");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_UNSUPPORTED_TYPE =
        new ErrorCode<>("client_key_unsupported_type", 400, "Key type is not supported for this algorithm", "type",
            "algorithm");

    public static final ErrorCode<ClientKeyValidationRestException> CLIENT_KEY_INVALID_TAG =
        new ErrorCode<>("client_key_invalid_tag", 400, "Invalid client key tag", "tag", "tag_max_length");

    public ClientKeyValidationRestException(String uniqueId, ErrorCode<ClientKeyValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
