package com.extole.client.rest.shareable.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public class ClientShareableValidationV2RestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareableValidationV2RestException> SHAREABLE_CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 403, "Shareable code contains reserved word", "code",
            "reserved_word");
    public static final ErrorCode<ClientShareableValidationV2RestException> TARGET_URL_INVALID =
        new ErrorCode<>("invalid_target_url", 403, "Invalid target_url", "target_url");

    public static final ErrorCode<ClientShareableValidationV2RestException> TARGET_URL_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("target_url_length_out_of_range", 403, "Target URL length is out of range. Max 2000 chars",
            "target_url");

    public static final ErrorCode<ClientShareableValidationV2RestException> IMAGE_URL_INVALID =
        new ErrorCode<>("invalid_image_url", 403, "Invalid image_url", "image_url");

    public static final ErrorCode<ClientShareableValidationV2RestException> CONTENT_URL_INVALID =
        new ErrorCode<>("invalid_content_url", 403, "Invalid url", "url");

    public static final ErrorCode<ClientShareableValidationV2RestException> KEY_TAKEN = new ErrorCode<>("key_taken",
        403, "The key associated with this shareable has already been specified", "shareable_id");

    public static final ErrorCode<ClientShareableValidationV2RestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 403, "Shareable data attribute name is invalid", "name");

    public static final ErrorCode<ClientShareableValidationV2RestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_name_length_out_of_range", 403,
            "Shareable data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<ClientShareableValidationV2RestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 403, "Shareable data value is invalid", "name");

    public static final ErrorCode<ClientShareableValidationV2RestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_value_length_out_of_range", 403,
            "Shareable data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<ClientShareableValidationV2RestException> LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_name_length_out_of_range", 403, "Label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<ClientShareableValidationV2RestException> LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 403,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<ClientShareableValidationV2RestException> CONTENT_DESCRIPTION_LENGTH_EXCEEDED =
        new ErrorCode<>("content_description_length_exceeded", 403, "Content description length exceeded",
            "description");

    public ClientShareableValidationV2RestException(String uniqueId,
        ErrorCode<ClientShareableValidationV2RestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
