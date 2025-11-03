package com.extole.common.variable.evaluator.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import com.extole.api.impl.campaign.RuntimeFlatSetting;
import com.extole.api.impl.campaign.VariableKey;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.variable.evaluator.RuntimeVariableEvaluationService;
import com.extole.common.variable.evaluator.RuntimeVariablesProvider.VariableContextSupplier;
import com.extole.common.variable.evaluator.VariableEvaluator;
import com.extole.evaluation.EvaluationException;

public class LoopPreventingVariableEvaluatorImpl implements VariableEvaluator {

    private static final String DEFAULT_VARIANT = "default";
    private final VariableContextSupplier contextSupplier;
    private final RuntimeVariableEvaluationService runtimeVariableEvaluationService;
    private final String currentVariant;
    private final Map<VariableKey, RuntimeFlatSetting> variables;
    private final Map<RuntimeFlatSetting, Object> evaluatedVariables = new HashMap<>();

    private final LinkedList<VariableKey> stack = Lists.newLinkedList();

    public LoopPreventingVariableEvaluatorImpl(
        VariableContextSupplier contextSupplier,
        RuntimeVariableEvaluationService runtimeVariableEvaluationService,
        String currentVariant,
        Map<VariableKey, RuntimeFlatSetting> variables) {
        this.contextSupplier = contextSupplier;
        this.runtimeVariableEvaluationService = runtimeVariableEvaluationService;
        this.currentVariant = currentVariant;
        this.variables = variables;
    }

    @Nullable
    @Override
    public Object evaluateVariable(String variableName, String[] keys) {
        List<String> variantsToConsider = new ArrayList<>(Arrays.asList(keys));
        if (keys.length == 0) {
            variantsToConsider.add(currentVariant);
        }
        variantsToConsider.add(DEFAULT_VARIANT);

        for (String variant : variantsToConsider) {
            VariableKey variableKey = VariableKey.of(variableName, variant);
            RuntimeFlatSetting flatVariable = variables.get(variableKey);
            if (flatVariable != null) {
                if (evaluatedVariables.containsKey(flatVariable)) {
                    return evaluatedVariables.get(flatVariable);
                }
                try {
                    if (stack.contains(variableKey)) {
                        throw new LoopPreventingVariableEvaluatorRuntimeException(String
                            .format("Failed to evaluate key = %s due circular reference : %s", variableKey, stack));
                    }
                    stack.add(variableKey);
                    Object evaluationResult = runtimeVariableEvaluationService.evaluate(flatVariable.getValue(),
                        new LazyLoadingSupplier<>(() -> contextSupplier.supply(this)), flatVariable)
                        .orElse(null);

                    stack.removeLast();
                    evaluatedVariables.put(flatVariable, evaluationResult);
                    return evaluationResult;
                } catch (EvaluationException e) {
                    throw new LoopPreventingVariableEvaluatorRuntimeException(e);
                }
            }
        }

        return null;
    }
}
