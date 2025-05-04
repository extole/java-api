package com.extole.client.rest.label;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class LabelInjectorValidationRestException extends ExtoleRestException {
    public static final ErrorCode<LabelInjectorValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("label_injector_name_out_of_range", 403,
            "Label injector name is not of valid length", "name", "min_length", "max_length");

    public static final ErrorCode<LabelInjectorValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("label_name_contains_illegal_character", 403,
            "Label name can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<LabelInjectorValidationRestException> NAME_MISSING =
        new ErrorCode<>("label_name_missing", 403, "Label name is missing");

    public static final ErrorCode<LabelInjectorValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "label_description_out_of_range", 403, "Label description length must not exceed 250 characters");

    public static final ErrorCode<LabelInjectorValidationRestException> INVALID_FORWARD_TO_LABEL =
        new ErrorCode<>("invalid_forward_label", 403, "Forward label is invalid", "forward_from_label");

    public static final ErrorCode<LabelInjectorValidationRestException> CANNOT_FORWARD_ITSELF =
        new ErrorCode<>("cannot_forward_itself", 403, "Cannot forward to itself");

    public static final ErrorCode<LabelInjectorValidationRestException> FORWARD_DEPTH_EXCEEDED = new ErrorCode<>(
        "forward_depth_exceeded", 403, "Forward label should not forward to another label", "forward_from_label");

    public static final ErrorCode<LabelInjectorValidationRestException> FORWARD_FROM_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("forward_from_name_length_out_of_range", 403, "Forward from label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<LabelInjectorValidationRestException> FORWARD_FROM_LABEL_NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("forward_from_label_name_contains_illegal_character", 403,
            "Forward label name can only contain alphanumeric, dash and underscore characters", "forward_from_label");

    public static final ErrorCode<LabelInjectorValidationRestException> CHILD_CANNOT_BE_ITSELF =
        new ErrorCode<>("child_cannot_be_itself", 403, "Cannot add itself as child label");

    public static final ErrorCode<LabelInjectorValidationRestException> CHILD_CANNOT_HAVE_CHILDREN =
        new ErrorCode<>("child_cannot_have_children", 403, "Child label cannot have children", "child_label");

    public static final ErrorCode<LabelInjectorValidationRestException> CHILDREN_ARE_BOTH_NORMAL_AND_OPTIONAL =
        new ErrorCode<>("children_are_both_normal_and_optional", 403,
            "Label has children that are both normal and optional");

    public static final ErrorCode<LabelInjectorValidationRestException> CHILD_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("child_name_length_out_of_range", 403, "Child label name is not of valid length",
            "name", "min_length", "max_length");

    public static final ErrorCode<LabelInjectorValidationRestException> CHILD_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("child_name_contains_illegal_character", 403,
            "Child label name can only contain alphanumeric, dash and underscore characters", "child_label");

    public static final ErrorCode<LabelInjectorValidationRestException> PROGRAM_DOMAIN_NOT_FOUND =
        new ErrorCode<>("program_domain_not_found", 403,
            "Program domain does not exist", "program_domain_id");

    public LabelInjectorValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
