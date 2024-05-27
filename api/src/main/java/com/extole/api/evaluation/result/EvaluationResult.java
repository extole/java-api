package com.extole.api.evaluation.result;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface EvaluationResult {

    boolean isPassed();

    String getResultCode();

    String getMessage();

    Map<String, String> getParameters();
}
