package com.extole.evaluation.model.collection.set;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithBooleanSetEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Set<Boolean>> evaluatable;

    @JsonCreator
    public DummyModelWithBooleanSetEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Set<Boolean>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Set<Boolean>> getEvaluatable() {
        return evaluatable;
    }

}
