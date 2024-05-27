package com.extole.api.event.geoip;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface State {

    String getIsoCode();

    String getName();
}
