package com.extole.consumer.rest.signal.step;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class QualityRuleResult {

    private static final String RULE_NAME = "rule_name";
    private static final String SCORE = "score";

    private final String ruleName;
    private final QualityResults.QualityScore qualityScore;

    public QualityRuleResult(
        @JsonProperty(RULE_NAME) String ruleName,
        @JsonProperty(SCORE) QualityResults.QualityScore qualityScore) {
        this.ruleName = ruleName;
        this.qualityScore = qualityScore;
    }

    @JsonProperty(RULE_NAME)
    public String getRuleType() {
        return ruleName;
    }

    @JsonProperty(SCORE)
    public QualityResults.QualityScore getQualityScore() {
        return qualityScore;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
