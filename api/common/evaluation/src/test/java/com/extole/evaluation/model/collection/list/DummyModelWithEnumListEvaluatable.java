package com.extole.evaluation.model.collection.list;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithEnumListEvaluatable {

    private static final String JSON_EVALUATABLE = "evaluatable";
    private final Evaluatable<DummyContext, List<DummyEnum>> evaluatable;

    @JsonCreator
    public DummyModelWithEnumListEvaluatable(
        @JsonProperty(JSON_EVALUATABLE) Evaluatable<DummyContext, List<DummyEnum>> evaluatable) {
        this.evaluatable = evaluatable;
    }

    @JsonProperty(JSON_EVALUATABLE)
    public Evaluatable<DummyContext, List<DummyEnum>> getEvaluatable() {
        return evaluatable;
    }

    public enum DummyEnum {
        TEST_VALUE, TEST_VALUE2
    }

}
