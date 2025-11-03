package com.extole.evaluation.model.single;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithBooleanEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Boolean> evaluatable;

    @JsonCreator
    public DummyModelWithBooleanEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Boolean> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Boolean> getEvaluatable() {
        return evaluatable;
    }

}
