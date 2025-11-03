package com.extole.evaluation.model.single;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithEnumEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, DummyEnum> evaluatable;

    @JsonCreator
    public DummyModelWithEnumEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, DummyEnum> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, DummyEnum> getEvaluatable() {
        return evaluatable;
    }

    public enum DummyEnum {
        TEST_VALUE
    }
}
