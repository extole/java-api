package com.extole.evaluation.model.collection.set;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithStringSetEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Set<String>> evaluatable;

    @JsonCreator
    public DummyModelWithStringSetEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Set<String>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Set<String>> getEvaluatable() {
        return evaluatable;
    }

}
