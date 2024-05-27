package com.extole.api.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface VariableContext {

    /**
     * Same as {@link #get(String, String...)} invoked with specified {@code name} and empty {@code keys} array
     */
    Object get(String name);

    /**
     * Same as {@link #get(String, String...)} invoked with specified {@code name} and 1-size {@code keys} array
     */
    Object get(String name, String key);

    /**
     * @param name - specifies the variable name
     * @param keys - specifies the variable keys to be considered.
     *            Keys listed first have higher lookup priority.
     *            Empty array means that the internal {@link VariableContext} defaultKeys are going to be considered.
     * @return value associated with the {@code name} and first {@code key} defined among specified ones in same order.
     *         {@code null} is returned if variable is not defined, none of the specified key is defined or a key is
     *         defined with {@code null} itself
     * @see CampaignBuildtimeContext#getVariableContext(String...) for internal {@link VariableContext} defaultKeys
     */
    Object get(String name, String... keys);

}
