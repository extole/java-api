package com.extole.evaluation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

@SuppressWarnings("checkstyle:lineLength")
public class DummyModelWithNestedEvaluatable {

    private static final String EVALUATABLE_EVALUATABLE = "evaluatable_evaluatable";
    private static final String BUILD_IN_EVALUATABLE = "build_in_evaluatable";
    private static final String RUNTIME_IN_EVALUATABLE = "runtime_in_evaluatable";
    private static final String EVALUATABLE_IN_BUILD = "evaluatable_in_build";
    private static final String BUILD_IN_BUILD = "build_in_build";
    private static final String RUNTIME_IN_BUILD = "runtime_in_build";
    private static final String EVALUATABLE_IN_RUNTIME = "evaluatable_in_runtime";
    private static final String BUILD_IN_RUNTIME = "build_in_runtime";
    private static final String RUNTIME_IN_RUNTIME = "runtime_in_runtime";
    private final Evaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> evaluatableEvaluatable;
    private final Evaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> buildInEvaluatable;
    private final Evaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> runtimeInEvaluatable;
    private final BuildtimeEvaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> evaluatableInBuild;
    private final BuildtimeEvaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> buildInBuild;
    private final BuildtimeEvaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> runtimeInBuild;
    private final RuntimeEvaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> evaluatableInRuntime;
    private final RuntimeEvaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> buildInRuntime;
    private final RuntimeEvaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> runtimeInRuntime;

    @JsonCreator
    public DummyModelWithNestedEvaluatable(
        @JsonProperty(EVALUATABLE_EVALUATABLE) Evaluatable<DummyContext,
            Evaluatable<DummyContext, List<String>>> evaluatableEvaluatable,
        @JsonProperty(BUILD_IN_EVALUATABLE) Evaluatable<DummyContext,
            BuildtimeEvaluatable<DummyContext, List<String>>> buildInEvaluatable,
        @JsonProperty(RUNTIME_IN_EVALUATABLE) Evaluatable<DummyContext,
            RuntimeEvaluatable<DummyContext, List<String>>> runtimeInEvaluatable,
        @JsonProperty(EVALUATABLE_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            Evaluatable<DummyContext, List<String>>> evaluatableInBuild,
        @JsonProperty(BUILD_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            BuildtimeEvaluatable<DummyContext, List<String>>> buildInBuild,
        @JsonProperty(RUNTIME_IN_BUILD) BuildtimeEvaluatable<DummyContext,
            RuntimeEvaluatable<DummyContext, List<String>>> runtimeInBuild,
        @JsonProperty(EVALUATABLE_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            Evaluatable<DummyContext, List<String>>> evaluatableInRuntime,
        @JsonProperty(BUILD_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            BuildtimeEvaluatable<DummyContext, List<String>>> buildInRuntime,
        @JsonProperty(RUNTIME_IN_RUNTIME) RuntimeEvaluatable<DummyContext,
            RuntimeEvaluatable<DummyContext, List<String>>> runtimeInRuntime) {
        this.evaluatableEvaluatable = evaluatableEvaluatable;
        this.buildInEvaluatable = buildInEvaluatable;
        this.runtimeInEvaluatable = runtimeInEvaluatable;
        this.evaluatableInBuild = evaluatableInBuild;
        this.buildInBuild = buildInBuild;
        this.runtimeInBuild = runtimeInBuild;
        this.evaluatableInRuntime = evaluatableInRuntime;
        this.buildInRuntime = buildInRuntime;
        this.runtimeInRuntime = runtimeInRuntime;
    }

    @JsonProperty(EVALUATABLE_EVALUATABLE)
    public Evaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> getEvaluatableEvaluatable() {
        return evaluatableEvaluatable;
    }

    @JsonProperty(BUILD_IN_EVALUATABLE)
    public Evaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> getBuildInEvaluatable() {
        return buildInEvaluatable;
    }

    @JsonProperty(RUNTIME_IN_EVALUATABLE)
    public Evaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> getRuntimeInEvaluatable() {
        return runtimeInEvaluatable;
    }

    @JsonProperty(EVALUATABLE_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> getEvaluatableInBuild() {
        return evaluatableInBuild;
    }

    @JsonProperty(BUILD_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> getBuildInBuild() {
        return buildInBuild;
    }

    @JsonProperty(RUNTIME_IN_BUILD)
    public BuildtimeEvaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> getRuntimeInBuild() {
        return runtimeInBuild;
    }

    @JsonProperty(EVALUATABLE_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, Evaluatable<DummyContext, List<String>>> getEvaluatableInRuntime() {
        return evaluatableInRuntime;
    }

    @JsonProperty(BUILD_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, BuildtimeEvaluatable<DummyContext, List<String>>> getBuildInRuntime() {
        return buildInRuntime;
    }

    @JsonProperty(RUNTIME_IN_RUNTIME)
    public RuntimeEvaluatable<DummyContext, RuntimeEvaluatable<DummyContext, List<String>>> getRuntimeInRuntime() {
        return runtimeInRuntime;
    }
}
