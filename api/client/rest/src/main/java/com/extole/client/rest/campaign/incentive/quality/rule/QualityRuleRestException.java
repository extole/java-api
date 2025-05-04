package com.extole.client.rest.campaign.incentive.quality.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class QualityRuleRestException extends ExtoleRestException {

    public static final ErrorCode<QualityRuleRestException> QUALITY_RULE_NOT_FOUND =
        new ErrorCode<>("quality_rule_not_found", 403, "Quality rule not found", "quality_rule_id");

    public QualityRuleRestException(String uniqueId, ErrorCode<QualityRuleRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
