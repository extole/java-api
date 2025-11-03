package com.extole.evaluation.model.collection.set;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithEnumSetEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, Set<DummyEnum>> evaluatable;

    @JsonCreator
    public DummyModelWithEnumSetEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, Set<DummyEnum>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, Set<DummyEnum>> getEvaluatable() {
        return evaluatable;
    }

    public enum DummyEnum {
        TEST_VALUE, TEST_VALUE2
    }

}
