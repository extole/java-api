package com.extole.api.model.campaign;

import javax.annotation.Nullable;

public interface ComponentAsset {

    String getId();

    String getName();

    String getFilename();

    String[] getTags();

    @Nullable
    String getDescription();

}
