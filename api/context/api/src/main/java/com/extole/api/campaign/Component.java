package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Component {

    String getId();

    String getName();

    String[] getTypes();

    String getDisplayName();

    @Nullable
    String getDescription();

    Object getVariableValue(String name);

    Object getVariableValue(String name, String... keys);

    ElementsQueryBuilder createElementsQuery();

    @Nullable
    Component getParent();

    Component[] getChildren(String socketName);

    Component[] getChildren();

}
