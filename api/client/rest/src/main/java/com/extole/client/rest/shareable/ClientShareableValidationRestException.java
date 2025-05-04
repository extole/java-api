package com.extole.client.rest.shareable;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientShareableValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareableValidationRestException> KEY_TAKEN = new ErrorCode<>(
        "shareable_key_taken", 403, "The key associated with this shareable has already been specified", "code");

    public static final ErrorCode<ClientShareableValidationRestException> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_length_out_of_range", 403, "Label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<ClientShareableValidationRestException> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 403,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<ClientShareableValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 403, "Shareable data attribute name is invalid", "name");

    public static final ErrorCode<ClientShareableValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_name_length_out_of_range", 403,
            "Shareable data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<ClientShareableValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 403, "Shareable data value is invalid", "name");

    public static final ErrorCode<ClientShareableValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_value_length_out_of_range", 403,
            "Shareable data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<ClientShareableValidationRestException> CONTENT_IMAGE_URL_INVALID =
        new ErrorCode<>("content_image_url_invalid", 403, "Invalid content image_url", "image_url");

    public static final ErrorCode<ClientShareableValidationRestException> CONTENT_URL_INVALID =
        new ErrorCode<>("invalid_content_url", 403, "Invalid content url", "url");

    public static final ErrorCode<ClientShareableValidationRestException> CONTENT_URL_BLOCKED =
        new ErrorCode<>("blocked_content_url", 403, "Content url is blocked", "url");

    public static final ErrorCode<ClientShareableValidationRestException> CONTENT_DESCRIPTION_LENGTH_EXCEEDED =
        new ErrorCode<>("content_description_length_exceeded", 403, "Content description length exceeded",
            "description");

    public static final ErrorCode<ClientShareableValidationRestException> MISSING_NEW_PERSON_ID_EXCEPTION =
        new ErrorCode<>("missing_new_person_id_exception", 400, "New shareable owner id is missing");

    public static final ErrorCode<ClientShareableValidationRestException> SAME_PERSON_AS_NEW_OWNER =
        new ErrorCode<>("same_person_as_new_owner", 400, "Cannot set same person as new owner");

    public ClientShareableValidationRestException(String uniqueId,
        ErrorCode<ClientShareableValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
