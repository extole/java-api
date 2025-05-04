package com.extole.consumer.rest.impl.web.zone;

import java.util.Optional;

import javax.ws.rs.core.Response;

import com.extole.consumer.service.zone.ZoneRenderResponse;

public class WebZoneRenderResponse {
    private final Response webResponse;
    private final Optional<ZoneRenderResponse> zoneRenderResponse;

    public WebZoneRenderResponse(Response webResponse) {
        this(webResponse, null);
    }

    public WebZoneRenderResponse(Response webResponse, ZoneRenderResponse zoneRenderResponse) {
        this.webResponse = webResponse;
        this.zoneRenderResponse = Optional.ofNullable(zoneRenderResponse);
    }

    public Response getWebResponse() {
        return webResponse;
    }

    public Optional<ZoneRenderResponse> getZoneRenderResponse() {
        return zoneRenderResponse;
    }
}
