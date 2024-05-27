package com.extole.api.campaign.runtime;

import java.util.Map;

import com.extole.api.GlobalContext;

public interface VariableRuntimeContext<T extends GlobalContext> extends GlobalContext {

    T getLocalContext();

    String getVariableName();

    Map<String, Object> getVariables();

}
