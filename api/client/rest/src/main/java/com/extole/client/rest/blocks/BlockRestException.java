package com.extole.client.rest.blocks;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BlockRestException extends ExtoleRestException {

    public static final ErrorCode<BlockRestException> DUPLICATE_BLOCK = new ErrorCode<>(
        "block_duplicate", 400, "Block duplicate", "filterType", "listType", "value");

    public static final ErrorCode<BlockRestException> BLOCK_NOT_FOUND = new ErrorCode<>(
        "block_not_found", 404, "Block not found", "blockId");

    public static final ErrorCode<BlockRestException> INVALID_VALUE = new ErrorCode<>(
        "block_value_invalid", 400, "Block value is not valid", "value");

    public static final ErrorCode<BlockRestException> MISSING_VALUE = new ErrorCode<>(
        "block_value_invalid", 400, "Block value is missing");

    public static final ErrorCode<BlockRestException> MISSING_FILTER_TYPE = new ErrorCode<>(
        "block_value_invalid", 400, "Block filter type is missing");

    public static final ErrorCode<BlockRestException> MISSING_LIST_TYPE = new ErrorCode<>(
        "block_value_invalid", 400, "Block list type is missing");

    public static final ErrorCode<BlockRestException> INVALID_EMAIL_DOMAIN_VALUE = new ErrorCode<>(
        "block_value_invalid", 400, "Normalized email block value domain is not valid", "domain");

    public static final ErrorCode<BlockRestException> INVALID_EMAIL_ADDRESS = new ErrorCode<>(
        "block_value_invalid", 400, "Normalized email block email address is not valid", "email");

    public BlockRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
