package com.extole.evaluation.model.single.optional;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluation.model.DummyContext;

@SuppressWarnings("checkstyle:lineLength")
public class DummyModelWithOptionalNestedEvaluatable {

    private static final String RUNTIME_IN_BUILD = "runtime_in_build";
    private final BuildtimeEvaluatable<DummyContext,
        Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInBuild;

    @JsonCreator
    public DummyModelWithOptionalNestedEvaluatable(
        @JsonProperty(RUNTIME_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInBuild) {
        this.runtimeInBuild = runtimeInBuild;
    }

    @JsonProperty(RUNTIME_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, Optional<RuntimeEvaluatable<DummyContext, List<String>>>>
        getRuntimeInBuild() {
        return runtimeInBuild;
    }

}
