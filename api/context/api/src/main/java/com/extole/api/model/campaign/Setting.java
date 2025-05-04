package com.extole.api.model.campaign;

import javax.annotation.Nullable;

public interface Setting {

    String getName();

    @Nullable
    String getDisplayName();

    String getType();

    String[] getTags();

}
