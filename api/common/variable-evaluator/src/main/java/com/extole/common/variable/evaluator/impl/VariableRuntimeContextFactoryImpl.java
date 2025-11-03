package com.extole.common.variable.evaluator.impl;

import org.springframework.stereotype.Component;

import com.extole.api.campaign.VariableContext;
import com.extole.common.variable.evaluator.VariableEvaluator;
import com.extole.common.variable.evaluator.VariableRuntimeContextFactory;

@Component
public class VariableRuntimeContextFactoryImpl implements VariableRuntimeContextFactory {

    @Override
    public VariableContext createContextSupplier(VariableEvaluator variableEvaluator) {
        return createVariableContext(variableEvaluator);
    }

    private VariableContext createVariableContext(VariableEvaluator variableEvaluator) {

        return new VariableContext() {

            @Override
            public Object get(String name) {
                return variableEvaluator.evaluateVariable(name, new String[] {});
            }

            @Override
            public Object get(String name, String key) {
                return variableEvaluator.evaluateVariable(name, new String[] {key});
            }

            @Override
            public Object get(String name, String... keys) {
                return variableEvaluator.evaluateVariable(name, keys);
            }

            @Override
            public Object getVariable(String name, String... keys) {
                return get(name, keys);
            }
        };
    }

}
