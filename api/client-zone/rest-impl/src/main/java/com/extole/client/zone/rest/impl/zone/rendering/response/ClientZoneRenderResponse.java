package com.extole.client.zone.rest.impl.zone.rendering.response;

import javax.ws.rs.core.Response;

import com.extole.consumer.service.zone.ZoneRenderResponse;

public class ClientZoneRenderResponse {

    private final Response webResponse;
    private final ZoneRenderResponse zoneRenderResponse;

    public ClientZoneRenderResponse(Response webResponse, ZoneRenderResponse zoneRenderResponse) {
        this.webResponse = webResponse;
        this.zoneRenderResponse = zoneRenderResponse;
    }

    public Response getWebResponse() {
        return webResponse;
    }

    public ZoneRenderResponse getZoneRenderResponse() {
        return zoneRenderResponse;
    }

}
