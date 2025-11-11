package com.extole.client.rest.person.shareables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonShareableValidationRestException extends ExtoleRestException {
    public static final ErrorCode<PersonShareableValidationRestException> LABEL_IS_MISSING =
        new ErrorCode<>("label_is_missing", 400, "Label is missing");

    public static final ErrorCode<PersonShareableValidationRestException> CODE_TAKEN =
        new ErrorCode<>("code_taken", 400,
            "The code associated with this shareable has already been specified", "code", "code_suggestions");

    public static final ErrorCode<PersonShareableValidationRestException> CODE_TAKEN_BY_PROMOTION =
        new ErrorCode<>("code_taken_by_promotion", 400,
            "The code associated with this shareable is already used by a promotion link", "code");

    public static final ErrorCode<PersonShareableValidationRestException> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_out_of_range", 400, "Code length must be between 4 and 50 characters", "code");

    public static final ErrorCode<PersonShareableValidationRestException> CODE_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("code_illegal_character", 400, "Code can only contain alphanumeric, dash and underscore",
            "code");

    public static final ErrorCode<PersonShareableValidationRestException> SHAREABLE_CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 400, "Shareable code contains reserved word", "code",
            "reserved_word");

    public static final ErrorCode<PersonShareableValidationRestException> KEY_TAKEN = new ErrorCode<>(
        "shareable_key_taken", 400, "The key associated with this shareable has already been specified", "code");

    public static final ErrorCode<PersonShareableValidationRestException> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_length_out_of_range", 400, "Label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<PersonShareableValidationRestException> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 400,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<PersonShareableValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 400, "Shareable data attribute name is invalid", "name");

    public static final ErrorCode<PersonShareableValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_name_length_out_of_range", 400,
            "Shareable data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<PersonShareableValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 400, "Shareable data value is invalid", "name");

    public static final ErrorCode<PersonShareableValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_value_length_out_of_range", 400,
            "Shareable data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<PersonShareableValidationRestException> CONTENT_IMAGE_URL_INVALID =
        new ErrorCode<>("content_image_url_invalid", 400, "Invalid content image_url", "image_url");

    public static final ErrorCode<PersonShareableValidationRestException> CONTENT_URL_INVALID =
        new ErrorCode<>("invalid_content_url", 400, "Invalid content url", "url");

    public static final ErrorCode<PersonShareableValidationRestException> CONTENT_URL_BLOCKED =
        new ErrorCode<>("blocked_content_url", 400, "Content url is blocked", "url");

    public static final ErrorCode<PersonShareableValidationRestException> CONTENT_URL_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("content_url_length_out_of_range", 400,
            "Content url is out of range", "url", "max_length");

    public static final ErrorCode<PersonShareableValidationRestException> CONTENT_DESCRIPTION_LENGTH_EXCEEDED =
        new ErrorCode<>("content_description_length_exceeded", 400, "Content description length exceeded",
            "description");

    public static final ErrorCode<PersonShareableValidationRestException> PERSON_NOT_REWARDABLE =
        new ErrorCode<>("person_not_rewardable", 400, "Person does not have email address", "person_id");

    public PersonShareableValidationRestException(String uniqueId,
        ErrorCode<PersonShareableValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
