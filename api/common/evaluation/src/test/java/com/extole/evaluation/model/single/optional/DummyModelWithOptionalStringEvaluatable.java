package com.extole.evaluation.model.single.optional;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithOptionalStringEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Optional<String>> evaluatable;

    @JsonCreator
    public DummyModelWithOptionalStringEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Optional<String>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<String>> getEvaluatable() {
        return evaluatable;
    }

}
