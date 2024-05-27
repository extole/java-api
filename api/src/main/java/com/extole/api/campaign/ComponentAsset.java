package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ComponentAsset {

    String getName();

    String getFilename();

    String getUrl();

    @Nullable
    String getDescription();

}
