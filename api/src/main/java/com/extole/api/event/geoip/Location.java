package com.extole.api.event.geoip;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Location {

    @Nullable
    Double getLatitude();

    @Nullable
    Double getLongitude();
}
