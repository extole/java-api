package com.extole.client.rest.campaign.incentive.quality.rule;

import java.util.Map;

import com.extole.client.rest.campaign.incentive.reward.rule.ExpressionType;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class QualityRuleValidationRestException extends ExtoleRestException {

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_KEY_INVALID_LENGTH =
        new ErrorCode<>("quality_rule_property_key_invalid_length", 400,
            "Quality rule property keys must be between 2 and 24 characters", "key");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_KEY_INVALID_CHARACTER =
        new ErrorCode<>("quality_rule_property_key_invalid_character", 400,
            "Quality rule property key can only contain alphanumeric, space, dash, colon and underscore", "key");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_LENGTH =
        new ErrorCode<>("quality_rule_property_value_invalid_length", 400,
            "Quality rule property values must be between 2 and 255 characters", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_CHARACTER =
        new ErrorCode<>("quality_rule_property_value_invalid_character", 400,
            "Quality rule property value can only contain alphanumeric, space, dash, colon and underscore", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_NUMBER =
        new ErrorCode<>("quality_rule_property_value_invalid_number", 400,
            "Quality rule property value must be a number", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_BOOLEAN =
        new ErrorCode<>("quality_rule_property_value_invalid_boolean", 400,
            "Quality rule property value must be true or false", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_TIME_UNIT =
        new ErrorCode<>("quality_rule_property_value_invalid_time_unit", 400,
            "Quality rule property value must be SECOND, MINUTE, HOUR, or DAY", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_COUNTRY =
        new ErrorCode<>("quality_rule_property_value_invalid_country", 400,
            "Quality rule property value must only contain ISO 3166 country codes", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_DOMAIN =
        new ErrorCode<>("quality_rule_property_value_invalid_country", 400,
            "Quality rule property value must only contain valid domains", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_INVALID_VALUE_COUNT =
        new ErrorCode<>("quality_rule_property_invalid_value_count", 400,
            "Quality rule property can only have a single value", "key");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_INVALID_REGEXP =
        new ErrorCode<>("quality_rule_property_invalid_regexp", 400,
            "Quality rule property value must be valid regular expression");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_UNSUPPORTED =
        new ErrorCode<>("quality_rule_property_key_unsupported", 400,
            "Quality rule property not supported for the quality rule type", "key");

    public static final ErrorCode<
        QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_VALUE_INVALID_TEMPORAL_UNIT = new ErrorCode<>(
            "quality_rule_property_value_invalid_temporal_unit", 400,
            "Quality rule property value must be SECONDS, MINUTES, HOURS or DAYS", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_PROPERTY_INVALID_SUBNET =
        new ErrorCode<>("quality_rule_property_value_invalid_subnet", 400,
            "Quality rule property value must contain only valid subnetworks", "value");

    public static final ErrorCode<QualityRuleValidationRestException> QUALITY_RULE_INVALID_EXPRESSION_TYPE =
        new ErrorCode<>("quality_rule_property_value_invalid_expression_type", 400,
            "Quality rule property value must be a valid expression type: " + ExpressionType.values(),
            "expression_type");

    public QualityRuleValidationRestException(String uniqueId, ErrorCode<QualityRuleValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
