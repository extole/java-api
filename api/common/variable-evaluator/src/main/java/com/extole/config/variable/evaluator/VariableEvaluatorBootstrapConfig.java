package com.extole.config.variable.evaluator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.common.variable.evaluator.impl.VariableEvaluatorPackageMarker;
import com.extole.config.running.RunningBootstrapConfig;

@Configuration
@ComponentScan(basePackageClasses = {RunningBootstrapConfig.class, VariableEvaluatorPackageMarker.class})
public class VariableEvaluatorBootstrapConfig {

}
