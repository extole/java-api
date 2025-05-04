package com.extole.consumer.rest.shareable.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.consumer.rest.me.shareable.v4.MeShareableV4RestException;

@Deprecated // TODO remove ENG-10127
public class CreateShareableV4RestException extends ExtoleRestException {
    public static final ErrorCode<CreateShareableV4RestException> SHAREABLE_CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 403, "Shareable code contains reserved word", "code",
            "reserved_word");
    public static final ErrorCode<CreateShareableV4RestException> INVALID_URL_FORMAT =
        new ErrorCode<>("INVALID_URL_FORMAT", 400, "Invalid URL format for URL.", "access_token", "url");
    public static final ErrorCode<CreateShareableV4RestException> INVALID_TARGET_URL =
        new ErrorCode<>("INVALID_TARGET_URL", 400, "Invalid target URL.", "target_url");
    public static final ErrorCode<CreateShareableV4RestException> INVALID_SHARE_URL =
        new ErrorCode<>("INVALID_SHARE_URL", 400, "Invalid share URL.", "access_token");
    // TODO fix exception name when we increment API version.
    public static final ErrorCode<CreateShareableV4RestException> EXISTING_LINK =
        new ErrorCode<>("EXISTING_LINK", 400, "Link already exists.", "link", "code_suggestions");
    public static final ErrorCode<CreateShareableV4RestException> NON_EXISTING_LINK =
        new ErrorCode<>("NON_EXISTING_LINK", 400, "Link doesn't exist for update.", "access_token");
    public static final ErrorCode<CreateShareableV4RestException> INVALID_PARTNER_CONTENT_URL =
        new ErrorCode<>("INVALID_PARTNER_CONTENT_URL", 400, "Invalid partner content URL.", "partner_content_url");
    public static final ErrorCode<CreateShareableV4RestException> INVALID_IMAGE_URL =
        new ErrorCode<>("INVALID_IMAGE_URL", 400, "Invalid image URL.", "image_url");
    public static final ErrorCode<CreateShareableV4RestException> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_out_of_range", 400,
            "Code length must be between 4 and 50 characters", "code");
    public static final ErrorCode<CreateShareableV4RestException> CODE_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("code_illegal_character", 400,
            "Code can only contain alphanumeric, dash, underscore and period", "code");
    public static final ErrorCode<CreateShareableV4RestException> CODE_CONTAINS_PROFANE_WORD =
        new ErrorCode<>("code_profane_word", 400, "Code contains profane word", "code");
    public static final ErrorCode<CreateShareableV4RestException> CODE_TAKEN_BY_PROMOTION =
        new ErrorCode<>("code_taken_by_promotion", 400,
            "The code associated with this shareable is already used by a promotion link", "code");
    public static final ErrorCode<CreateShareableV4RestException> UNREWARDABLE =
        new ErrorCode<>("UNREWARDABLE", 400, "User must have an associated email to create a shareable.");
    public static final ErrorCode<CreateShareableV4RestException> KEY_TAKEN = new ErrorCode<>("key_taken", 400,
        "The key associated with this shareable has already been specified", "key");

    public static final ErrorCode<CreateShareableV4RestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 400, "Shareable data attribute name is invalid", "name");

    public static final ErrorCode<CreateShareableV4RestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_name_length_out_of_range", 400,
            "Shareable data attribute name length is out of range. Max 200 chars",
            "name");

    public static final ErrorCode<CreateShareableV4RestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 400, "Shareable data attribute value is invalid", "name");

    public static final ErrorCode<CreateShareableV4RestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_value_length_out_of_range", 400,
            "Shareable data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<CreateShareableV4RestException> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_out_of_range", 400, "Label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<CreateShareableV4RestException> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 400,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<CreateShareableV4RestException> TARGET_URL_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("target_url_length_out_of_range", 400,
            "Target URL length is out of range. Max 2000 chars", "target_url");

    public static final ErrorCode<CreateShareableV4RestException> EXTOLE_DESTINATION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("extole_destination_length_out_of_range", 400,
            "Extole destination length is out of range. Max 2000 chars", "target_url");

    public static final ErrorCode<CreateShareableV4RestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 400, "Person not found", "person_id");

    public static final ErrorCode<MeShareableV4RestException> INVALID_SHAREABLE_ID =
        new ErrorCode<>("invalid_shareable_id", 400, "Invalid shareable id", "shareable_id");

    public static final ErrorCode<CreateShareableV4RestException> CONTENT_DESCRIPTION_LENGTH_EXCEEDED =
        new ErrorCode<>("content_description_length_exceeded", 400, "Content description length exceeded",
            "description");

    public CreateShareableV4RestException(String uniqueId, ErrorCode<CreateShareableV4RestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
