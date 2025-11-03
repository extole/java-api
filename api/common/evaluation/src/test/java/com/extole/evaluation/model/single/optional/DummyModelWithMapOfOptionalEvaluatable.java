package com.extole.evaluation.model.single.optional;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.model.DummyContext;

public class DummyModelWithMapOfOptionalEvaluatable {

    private static final String JSON_EVALUATABLES = "evaluatables";

    private final Map<String, Evaluatable<DummyContext, Optional<Object>>> evaluatables;

    @JsonCreator
    public DummyModelWithMapOfOptionalEvaluatable(
        @JsonProperty(JSON_EVALUATABLES) Map<String, Evaluatable<DummyContext, Optional<Object>>> evaluatables) {
        this.evaluatables = evaluatables;
    }

    @JsonProperty(JSON_EVALUATABLES)
    public Map<String, Evaluatable<DummyContext, Optional<Object>>> getEvaluatables() {
        return evaluatables;
    }
}
