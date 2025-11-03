package com.extole.evaluation.model.single;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithEvaluatableMapKey {

    private static final String JSON_EVALUATABLES = "evaluatables";

    private final Map<Evaluatable<DummyContext, String>, Object> evaluatables;

    @JsonCreator
    public DummyModelWithEvaluatableMapKey(
        @JsonProperty(JSON_EVALUATABLES) Map<Evaluatable<DummyContext, String>, Object> evaluatables) {
        this.evaluatables = evaluatables;
    }

    @JsonProperty(JSON_EVALUATABLES)
    public Map<Evaluatable<DummyContext, String>, Object> getEvaluatables() {
        return evaluatables;
    }

}
