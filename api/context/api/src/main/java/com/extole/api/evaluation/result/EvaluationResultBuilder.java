package com.extole.api.evaluation.result;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface EvaluationResultBuilder {

    EvaluationResultBuilder pass(String message);

    EvaluationResultBuilder fail(String resultCode, String message, Map<String, String> parameters);

    EvaluationResultBuilder fail(String resultCode, String message);

    List<EvaluationResult> getEvaluationResults();
}
