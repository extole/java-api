package com.extole.api.impl.evaluation.result;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.evaluation.result.EvaluationResult;

public final class EvaluationResultImpl implements EvaluationResult {

    private final boolean passed;
    private final String resultCode;
    private final String message;
    private final Map<String, String> parameters;

    private EvaluationResultImpl(boolean passed, String resultCode, String message, Map<String, String> parameters) {
        this.passed = passed;
        this.resultCode = resultCode;
        this.message = message;
        this.parameters = parameters != null ? ImmutableMap.copyOf(parameters) : null;
    }

    public static EvaluationResult pass(String message, Map<String, String> parameters) {
        return new EvaluationResultImpl(true, "success", message, parameters);
    }

    public static EvaluationResult fail(String resultCode, String message, Map<String, String> parameters) {
        return new EvaluationResultImpl(false, resultCode, message, parameters);
    }

    @Override
    public boolean isPassed() {
        return passed;
    }

    @Nullable
    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }
}
