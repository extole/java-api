package com.extole.evaluation.model.single;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithBooleanEvaluatableMapKey {

    private static final String JSON_EVALUATABLES = "evaluatables";

    private final Map<Evaluatable<DummyContext, Boolean>, Object> evaluatables;

    @JsonCreator
    public DummyModelWithBooleanEvaluatableMapKey(
        @JsonProperty(JSON_EVALUATABLES) Map<Evaluatable<DummyContext, Boolean>, Object> evaluatables) {
        this.evaluatables = evaluatables;
    }

    @JsonProperty(JSON_EVALUATABLES)
    public Map<Evaluatable<DummyContext, Boolean>, Object> getEvaluatables() {
        return evaluatables;
    }

}
