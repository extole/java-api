package com.extole.api.event;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface InputEventLocale {

    @Nullable
    String getUserSpecified();

    @Nullable
    String getLastBrowser();
}
