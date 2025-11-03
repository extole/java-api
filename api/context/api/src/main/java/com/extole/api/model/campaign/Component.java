package com.extole.api.model.campaign;

import java.util.List;

import javax.annotation.Nullable;

public interface Component {

    String getId();

    String getName();

    List<String> getType();

    @Nullable
    String getDescription();

    String[] getTags();

    Variable[] getVariables();

    ComponentAsset[] getAssets();

    String getCreatedDate();

    String getUpdatedDate();

}
