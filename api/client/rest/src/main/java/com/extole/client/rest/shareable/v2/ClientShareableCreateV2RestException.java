package com.extole.client.rest.shareable.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public class ClientShareableCreateV2RestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareableCreateV2RestException> CODE_TAKEN = new ErrorCode<>("code_taken", 400,
        "The code associated with this shareable has already been specified", "shareable_id", "code_suggestions");

    public static final ErrorCode<ClientShareableCreateV2RestException> CODE_TAKEN_BY_PROMOTION =
        new ErrorCode<>("code_taken_by_promotion", 400,
            "The code associated with this shareable is already used by a promotion link", "code");

    public static final ErrorCode<ClientShareableCreateV2RestException> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_out_of_range", 400, "Code length must be between 4 and 50 characters", "code");

    public static final ErrorCode<ClientShareableCreateV2RestException> CODE_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("code_illegal_character", 400, "Code can only contain alphanumeric, dash and underscore",
            "code");

    public static final ErrorCode<ClientShareableCreateV2RestException> PERSON_NOT_REWARDABLE =
        new ErrorCode<>("person_not_rewardable", 400, "Person does not have email address", "person_id");

    public static final ErrorCode<ClientShareableCreateV2RestException> INVALID_PROGRAM_ID =
        new ErrorCode<>("invalid_program_id", 400, "Shareable program id is not valid", "program_id");

    public ClientShareableCreateV2RestException(String uniqueId, ErrorCode<ClientShareableCreateV2RestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
