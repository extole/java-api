package com.extole.common.variable.evaluator;

import javax.annotation.Nullable;

public interface VariableEvaluator {

    @Nullable
    Object evaluateVariable(String variableName, String[] keys);

}
