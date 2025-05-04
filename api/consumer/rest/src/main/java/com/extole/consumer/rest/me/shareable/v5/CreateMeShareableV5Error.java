package com.extole.consumer.rest.me.shareable.v5;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.model.RestError;

public final class CreateMeShareableV5Error extends RestError {
    public static final ErrorCode<CreateMeShareableV5Error> SHAREABLE_CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 403, "Shareable code contains reserved word");

    public static final ErrorCode<CreateMeShareableV5Error> LABEL_IS_MISSING =
        new ErrorCode<>("label_is_missing", 403, "Label is missing");

    public static final ErrorCode<CreateMeShareableV5Error> CODE_TAKEN =
        new ErrorCode<>("code_taken", 403, "The code associated with this shareable has already been specified");

    public static final ErrorCode<CreateMeShareableV5Error> CODE_TAKEN_BY_PROMOTION =
        new ErrorCode<>("code_taken_by_promotion", 400,
            "The code associated with this shareable is already used by a promotion link", "code");

    public static final ErrorCode<CreateMeShareableV5Error> CONTENT_DESCRIPTION_LENGTH_EXCEEDED =
        new ErrorCode<>("content_description_length_exceeded", 400,
            "Content description length exceeded");

    public static final ErrorCode<CreateMeShareableV5Error> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_out_of_range", 403, "Code length must be between 4 and 50 characters");

    public static final ErrorCode<CreateMeShareableV5Error> CODE_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("code_illegal_character", 403, "Code can only contain alphanumeric, dash and underscore");

    public static final ErrorCode<CreateMeShareableV5Error> CODE_CONTAINS_PROFANE_WORD =
        new ErrorCode<>("code_profane_word", 403, "Code contains profane word");

    public static final ErrorCode<CreateMeShareableV5Error> KEY_TAKEN = new ErrorCode<>("shareable_key_taken", 403,
        "The key associated with this shareable has already been specified");

    public static final ErrorCode<CreateMeShareableV5Error> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_out_of_range", 403, "Label name length must be between 2 and 255 characters");

    public static final ErrorCode<CreateMeShareableV5Error> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 403,
            "Label name can only contain alphanumeric, dash and underscore characters");

    public static final ErrorCode<CreateMeShareableV5Error> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 403, "Shareable data attribute name is invalid");

    public static final ErrorCode<CreateMeShareableV5Error> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_name_length_out_of_range", 403,
            "Shareable data attribute name length is out of range. Max 200 chars");

    public static final ErrorCode<CreateMeShareableV5Error> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 403, "Shareable data value is invalid");

    public static final ErrorCode<CreateMeShareableV5Error> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_value_length_out_of_range", 403,
            "Shareable data attribute value length is out of range. Max 2000 chars");

    public static final ErrorCode<CreateMeShareableV5Error> CONTENT_IMAGE_URL_INVALID =
        new ErrorCode<>("content_image_url_invalid", 403, "Invalid content image_url");

    public static final ErrorCode<CreateMeShareableV5Error> CONTENT_URL_INVALID =
        new ErrorCode<>("invalid_content_url", 403, "Invalid content url");

    public static final ErrorCode<CreateMeShareableV5Error> SERVER_ERROR =
        new ErrorCode<>("server_error", 500, "Server error");

    @JsonCreator
    protected CreateMeShareableV5Error(
        @JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        super(uniqueId, httpStatusCode, code, message, parameters);
    }

    public CreateMeShareableV5Error(RestError restError) {
        super(restError);
    }
}
