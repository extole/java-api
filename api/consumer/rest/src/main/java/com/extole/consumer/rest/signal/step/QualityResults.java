package com.extole.consumer.rest.signal.step;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class QualityResults {

    public enum QualityScore {
        ERROR, LOW, HIGH
    }

    private static final String SCORE = "score";
    private static final String QUALITY_RULE_RESULTS = "quality_rule_results";

    private final QualityScore score;
    private final List<QualityRuleResult> qualityRuleResults;

    public QualityResults(@JsonProperty(SCORE) QualityScore score,
        @JsonProperty(QUALITY_RULE_RESULTS) List<QualityRuleResult> qualityRuleResults) {
        this.score = score;
        this.qualityRuleResults = ImmutableList.copyOf(qualityRuleResults);
    }

    @JsonProperty(SCORE)
    public QualityScore getScore() {
        return score;
    }

    @JsonProperty(QUALITY_RULE_RESULTS)
    public List<QualityRuleResult> getQualityRuleResults() {
        return qualityRuleResults;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
