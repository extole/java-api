package com.extole.api.campaign.runtime;

import java.util.Map;

import com.extole.api.GlobalContext;
import com.extole.evaluateable.handlebars.ShortVariableSyntaxContext;

public interface VariableRuntimeContext<T extends GlobalContext> extends GlobalContext, ShortVariableSyntaxContext {

    T getLocalContext();

    String getVariableName();

    Map<String, Object> getVariables();

}
