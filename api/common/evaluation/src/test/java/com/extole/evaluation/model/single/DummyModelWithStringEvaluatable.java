package com.extole.evaluation.model.single;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithStringEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, String> evaluatable;

    @JsonCreator
    public DummyModelWithStringEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, String> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, String> getEvaluatable() {
        return evaluatable;
    }

}
