package com.extole.evaluation.model.single.optional;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithOptionalEnumEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Optional<DummyEnum>> evaluatable;

    @JsonCreator
    public DummyModelWithOptionalEnumEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Optional<DummyEnum>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<DummyEnum>> getEvaluatable() {
        return evaluatable;
    }

    public enum DummyEnum {
        TEST_VALUE
    }
}
