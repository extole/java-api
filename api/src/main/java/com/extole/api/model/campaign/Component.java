package com.extole.api.model.campaign;

import javax.annotation.Nullable;

public interface Component {

    String getId();

    String getComponentVersion();

    String getName();

    @Nullable
    String getDescription();

    String[] getTags();

    Variable[] getVariables();

    ComponentAsset[] getAssets();

    String getCreatedDate();

    String getUpdatedDate();

}
