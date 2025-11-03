package com.extole.evaluation.model.collection.list;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithBooleanListEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, List<Boolean>> evaluatable;

    @JsonCreator
    public DummyModelWithBooleanListEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, List<Boolean>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, List<Boolean>> getEvaluatable() {
        return evaluatable;
    }

}
