package com.extole.api.impl.evaluation.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.extole.api.evaluation.result.EvaluationResult;
import com.extole.api.evaluation.result.EvaluationResultBuilder;

public class EvaluationResultBuilderImpl implements EvaluationResultBuilder {

    private final List<EvaluationResult> evaluationResults;

    public EvaluationResultBuilderImpl() {
        this.evaluationResults = new ArrayList<>();
    }

    @Override
    public EvaluationResultBuilder pass(String message) {
        evaluationResults.add(EvaluationResultImpl.pass(message, ImmutableMap.of()));
        return this;
    }

    @Override
    public EvaluationResultBuilder fail(String resultCode, String message, Map<String, String> parameters) {
        evaluationResults.add(EvaluationResultImpl.fail(resultCode, message, ImmutableMap.copyOf(parameters)));
        return this;
    }

    @Override
    public EvaluationResultBuilder fail(String resultCode, String message) {
        evaluationResults.add(EvaluationResultImpl.fail(resultCode, message, ImmutableMap.of()));
        return this;
    }

    @Override
    public List<EvaluationResult> getEvaluationResults() {
        return evaluationResults;
    }
}
