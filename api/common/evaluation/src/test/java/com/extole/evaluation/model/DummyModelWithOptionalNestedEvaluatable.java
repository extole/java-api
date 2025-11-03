package com.extole.evaluation.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
public class DummyModelWithOptionalNestedEvaluatable {

    private static final String EVALUATABLE_EVALUATABLE = "evaluatable_evaluatable";
    private static final String BUILD_IN_EVALUATABLE = "build_in_evaluatable";
    private static final String RUNTIME_IN_EVALUATABLE = "runtime_in_evaluatable";
    private static final String EVALUATABLE_IN_BUILD = "evaluatable_in_build";
    private static final String BUILD_IN_BUILD = "build_in_build";
    private static final String RUNTIME_IN_BUILD = "runtime_in_build";
    private static final String EVALUATABLE_IN_RUNTIME = "evaluatable_in_runtime";
    private static final String BUILD_IN_RUNTIME = "build_in_runtime";
    private static final String RUNTIME_IN_RUNTIME = "runtime_in_runtime";
    private static final String OPTIONAL_EVALUATABLE = "optional_evaluatable";
    private final Evaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>> evaluatableEvaluatable;
    private final Evaluatable<DummyContext,
        Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInEvaluatable;
    private final Evaluatable<DummyContext,
        Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInEvaluatable;
    private final BuildtimeEvaluatable<DummyContext,
        Optional<Evaluatable<DummyContext, List<String>>>> evaluatableInBuild;
    private final BuildtimeEvaluatable<DummyContext,
        Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInBuild;
    private final BuildtimeEvaluatable<DummyContext,
        Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInBuild;
    private final RuntimeEvaluatable<DummyContext,
        Optional<Evaluatable<DummyContext, List<String>>>> evaluatableInRuntime;
    private final RuntimeEvaluatable<DummyContext,
        Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInRuntime;
    private final RuntimeEvaluatable<DummyContext,
        Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInRuntime;
    private final Optional<
        Evaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>>> optionalEvaluatable;

    @JsonCreator
    public DummyModelWithOptionalNestedEvaluatable(
        @JsonProperty(EVALUATABLE_EVALUATABLE) Evaluatable<DummyContext,
            Optional<Evaluatable<DummyContext, List<String>>>> evaluatableEvaluatable,
        @JsonProperty(BUILD_IN_EVALUATABLE) Evaluatable<DummyContext,
            Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInEvaluatable,
        @JsonProperty(RUNTIME_IN_EVALUATABLE) Evaluatable<DummyContext,
            Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInEvaluatable,
        @JsonProperty(EVALUATABLE_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            Optional<Evaluatable<DummyContext, List<String>>>> evaluatableInBuild,
        @JsonProperty(BUILD_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInBuild,
        @JsonProperty(RUNTIME_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInBuild,
        @JsonProperty(EVALUATABLE_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            Optional<Evaluatable<DummyContext, List<String>>>> evaluatableInRuntime,
        @JsonProperty(BUILD_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            Optional<BuildtimeEvaluatable<DummyContext, List<String>>>> buildInRuntime,
        @JsonProperty(RUNTIME_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            Optional<RuntimeEvaluatable<DummyContext, List<String>>>> runtimeInRuntime,
        @JsonProperty(OPTIONAL_EVALUATABLE) Optional<
            Evaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>>> optionalEvaluatable) {
        this.evaluatableEvaluatable = evaluatableEvaluatable;
        this.buildInEvaluatable = buildInEvaluatable;
        this.runtimeInEvaluatable = runtimeInEvaluatable;
        this.evaluatableInBuild = evaluatableInBuild;
        this.buildInBuild = buildInBuild;
        this.runtimeInBuild = runtimeInBuild;
        this.evaluatableInRuntime = evaluatableInRuntime;
        this.buildInRuntime = buildInRuntime;
        this.runtimeInRuntime = runtimeInRuntime;
        this.optionalEvaluatable = optionalEvaluatable;
    }

    @JsonProperty(EVALUATABLE_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>> getEvaluatableEvaluatable() {
        return evaluatableEvaluatable;
    }

    @JsonProperty(BUILD_IN_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<BuildtimeEvaluatable<DummyContext, List<String>>>>
        getBuildInEvaluatable() {
        return buildInEvaluatable;
    }

    @JsonProperty(RUNTIME_IN_EVALUATABLE)
    public Evaluatable<DummyContext, Optional<RuntimeEvaluatable<DummyContext, List<String>>>>
        getRuntimeInEvaluatable() {
        return runtimeInEvaluatable;
    }

    @JsonProperty(EVALUATABLE_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>>
        getEvaluatableInBuild() {
        return evaluatableInBuild;
    }

    @JsonProperty(BUILD_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, Optional<BuildtimeEvaluatable<DummyContext, List<String>>>>
        getBuildInBuild() {
        return buildInBuild;
    }

    @JsonProperty(RUNTIME_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, Optional<RuntimeEvaluatable<DummyContext, List<String>>>>
        getRuntimeInBuild() {
        return runtimeInBuild;
    }

    @JsonProperty(EVALUATABLE_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>>
        getEvaluatableInRuntime() {
        return evaluatableInRuntime;
    }

    @JsonProperty(BUILD_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, Optional<BuildtimeEvaluatable<DummyContext, List<String>>>>
        getBuildInRuntime() {
        return buildInRuntime;
    }

    @JsonProperty(RUNTIME_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, Optional<RuntimeEvaluatable<DummyContext, List<String>>>>
        getRuntimeInRuntime() {
        return runtimeInRuntime;
    }

    @JsonProperty(OPTIONAL_EVALUATABLE)
    public Optional<Evaluatable<DummyContext, Optional<Evaluatable<DummyContext, List<String>>>>>
        getOptionalEvaluatable() {
        return optionalEvaluatable;
    }
}
