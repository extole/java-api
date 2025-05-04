package com.extole.api;

import javax.annotation.Nullable;

import com.extole.evaluateable.handlebars.ShortVariableSyntaxContext;

public interface RuntimeVariableContext extends ShortVariableSyntaxContext {

    @Nullable
    Object getVariable(String name);

    @Nullable
    Object getVariable(String name, String key);

}
