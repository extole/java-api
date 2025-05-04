package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;

@Schema
public interface ComponentBuildtimeContext extends GlobalContext, LoggerContext {

    /**
     * Alternative for
     * getVariableContext().get("component.name")
     * getVariableContext().get("component.displayName")
     */
    @Nullable
    Component getComponent();

    /**
     * Same as {@link #getVariableContext(String)} invoked with {@code "default"}
     */
    VariableContext getVariableContext();

    /**
     * Same as {@link #getVariableContext(String...)} invoked with 1 specified defaultKey
     */
    VariableContext getVariableContext(String defaultKey);

    /**
     * @param defaultKeys - specifies defaultKeys for the requested {@link VariableContext}
     *            defaultKeys are to be considered when no key is specified either by using
     *            {@link VariableContext#get(String)} or {@link VariableContext#get(String, String...)} with an empty
     *            keys array
     * @return a new {@link VariableContext} with internally set defaultKeys
     */
    VariableContext getVariableContext(String... defaultKeys);

    @Nullable
    ComponentAsset getAsset(String assetName);

}
