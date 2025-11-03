package com.extole.evaluation.model.single;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;
import com.extole.id.Id;

public class DummyModelWithIdEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";

    private final Evaluatable<DummyContext, Id<?>> evaluatable;

    @JsonCreator
    public DummyModelWithIdEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Id<?>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Id<?>> getEvaluatable() {
        return evaluatable;
    }

}
