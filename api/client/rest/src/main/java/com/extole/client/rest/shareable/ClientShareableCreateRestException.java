package com.extole.client.rest.shareable;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientShareableCreateRestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareableCreateRestException> SHAREABLE_CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 403, "Shareable code contains reserved word", "code",
            "reserved_word");

    public static final ErrorCode<ClientShareableCreateRestException> LABEL_IS_MISSING =
        new ErrorCode<>("label_is_missing", 403, "Label is missing");

    public static final ErrorCode<ClientShareableCreateRestException> CODE_TAKEN = new ErrorCode<>("code_taken", 403,
        "The code associated with this shareable has already been specified", "code", "code_suggestions");

    public static final ErrorCode<ClientShareableCreateRestException> CODE_TAKEN_BY_PROMOTION =
        new ErrorCode<>("code_taken_by_promotion", 400,
            "The code associated with this shareable is already used by a promotion link", "code");

    public static final ErrorCode<ClientShareableCreateRestException> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_out_of_range", 403, "Code length must be between 4 and 50 characters", "code");

    public static final ErrorCode<ClientShareableCreateRestException> CODE_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("code_illegal_character", 403, "Code can only contain alphanumeric, dash and underscore",
            "code");

    public static final ErrorCode<ClientShareableCreateRestException> PERSON_NOT_REWARDABLE =
        new ErrorCode<>("person_not_rewardable", 403, "Person does not have email address", "person_id");

    public ClientShareableCreateRestException(String uniqueId, ErrorCode<ClientShareableCreateRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
