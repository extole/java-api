package com.extole.common.variable.evaluator;

import com.extole.api.campaign.VariableContext;

public interface VariableRuntimeContextFactory {

    VariableContext createContextSupplier(VariableEvaluator variableEvaluator);

}
