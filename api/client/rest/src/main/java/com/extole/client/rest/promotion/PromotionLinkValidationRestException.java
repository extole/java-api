package com.extole.client.rest.promotion;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PromotionLinkValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PromotionLinkValidationRestException> CODE_INVALID =
        new ErrorCode<>("code_invalid", 400,
            "Promotion link code can only contain alphanumeric characters, dashes and underscores", "code");

    public static final ErrorCode<PromotionLinkValidationRestException> CODE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("code_length_out_of_range", 400, "Promotion link code length must be between 4 and 50", "code");

    public static final ErrorCode<PromotionLinkValidationRestException> CODE_TAKEN =
        new ErrorCode<>("code_taken", 400, "Promotion link code has already been specified", "code");

    public static final ErrorCode<PromotionLinkValidationRestException> CODE_CONTAINS_RESERVED_WORD =
        new ErrorCode<>("code_contains_reserved_word", 400, "Promotion link code contains reserved word", "code",
            "reserved_word");

    public static final ErrorCode<PromotionLinkValidationRestException> PROGRAM_NOT_FOUND =
        new ErrorCode<>("program_not_found", 400, "Promotion link program not found", "program_url", "client_id");

    public static final ErrorCode<PromotionLinkValidationRestException> PROGRAM_URL_INVALID =
        new ErrorCode<>("program_url_invalid", 400, "Promotion link program url is invalid", "program_url");

    public static final ErrorCode<PromotionLinkValidationRestException> CONTENT_URL_INVALID =
        new ErrorCode<>("content_url_invalid", 400, "Promotion link content url is invalid", "url");

    public static final ErrorCode<PromotionLinkValidationRestException> CONTENT_IMAGE_URL_INVALID =
        new ErrorCode<>("content_image_url_invalid", 400, "Promotion link content image url is invalid", "url");

    public static final ErrorCode<PromotionLinkValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
        new ErrorCode<>("data_attribute_name_invalid", 400, "Promotion link data attribute name is invalid", "name");

    public static final ErrorCode<PromotionLinkValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_name_length_out_of_range", 400,
            "Promotion link data attribute name length is out of range. Max 200 chars", "name");

    public static final ErrorCode<PromotionLinkValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
        new ErrorCode<>("data_attribute_value_invalid", 400, "Promotion link data attribute value is invalid", "name");

    public static final ErrorCode<PromotionLinkValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_attribute_value_length_out_of_range", 400,
            "Promotion link data attribute value length is out of range. Max 2000 chars", "name");

    public static final ErrorCode<PromotionLinkValidationRestException> PROMOTION_LABEL_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("promotion_label_name_out_of_range", 400,
            "Promotion label name is not of valid length", "name", "min_length", "max_length");

    public static final ErrorCode<PromotionLinkValidationRestException> LABEL_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_contains_illegal_character", 400,
            "Label name can only contain alphanumeric, dash and underscore characters", "label");

    public static final ErrorCode<PromotionLinkValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("description_length_out_of_range", 400, "Description is too long", "max_length");

    public PromotionLinkValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
