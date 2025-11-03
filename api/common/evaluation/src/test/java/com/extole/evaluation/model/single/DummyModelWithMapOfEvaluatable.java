package com.extole.evaluation.model.single;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithMapOfEvaluatable {

    private static final String JSON_EVALUATABLES = "evaluatables";

    private final Map<String, Evaluatable<DummyContext, Object>> evaluatables;

    @JsonCreator
    public DummyModelWithMapOfEvaluatable(
        @JsonProperty(JSON_EVALUATABLES) Map<String, Evaluatable<DummyContext, Object>> evaluatables) {
        this.evaluatables = evaluatables;
    }

    @JsonProperty(JSON_EVALUATABLES)
    public Map<String, Evaluatable<DummyContext, Object>> getEvaluatables() {
        return evaluatables;
    }
}
