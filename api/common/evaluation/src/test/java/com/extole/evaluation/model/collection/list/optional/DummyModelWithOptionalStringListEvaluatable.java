package com.extole.evaluation.model.collection.list.optional;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithOptionalStringListEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Optional<List<String>>> evaluatable;

    @JsonCreator
    public DummyModelWithOptionalStringListEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Optional<List<String>>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<List<String>>> getEvaluatable() {
        return evaluatable;
    }

}
