package com.extole.evaluation.model.collection.set.optional;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithOptionalStringSetEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Optional<Set<String>>> evaluatable;

    @JsonCreator
    public DummyModelWithOptionalStringSetEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Optional<Set<String>>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<Set<String>>> getEvaluatable() {
        return evaluatable;
    }

}
