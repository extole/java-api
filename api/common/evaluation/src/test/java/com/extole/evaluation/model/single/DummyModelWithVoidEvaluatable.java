package com.extole.evaluation.model.single;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithVoidEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";

    private final Evaluatable<DummyContext, Void> evaluatable;

    @JsonCreator
    public DummyModelWithVoidEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Void> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Void> getEvaluatable() {
        return evaluatable;
    }

}
