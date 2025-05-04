package com.extole.consumer.rest.me.shareable;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CreateMeShareableRestException extends ExtoleRestException {
    public static final ErrorCode<CreateMeShareableRestException> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_length_out_of_range", 403, "Label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<CreateMeShareableRestException> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 403,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<CreateMeShareableRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 403, "Shareable data attribute name is invalid");

    public static final ErrorCode<CreateMeShareableRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_name_length_out_of_range", 403,
            "Shareable data attribute name length is out of range. Max 200 chars");

    public static final ErrorCode<CreateMeShareableRestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 403, "Shareable data attribute value is invalid");

    public static final ErrorCode<CreateMeShareableRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_value_length_out_of_range", 403,
            "Shareable data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<CreateMeShareableRestException> CONTENT_IMAGE_URL_INVALID =
        new ErrorCode<>("content_image_url_invalid", 403, "Invalid content image_url", "image_url");

    public static final ErrorCode<CreateMeShareableRestException> CONTENT_URL_INVALID =
        new ErrorCode<>("invalid_content_url", 403, "Invalid content url", "url");

    public static final ErrorCode<CreateMeShareableRestException> CONTENT_URL_BLOCKED =
        new ErrorCode<>("blocked_content_url", 403, "Content url is blocked", "url");

    public CreateMeShareableRestException(String uniqueId, ErrorCode<CreateMeShareableRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
