package com.extole.client.zone.rest.impl.zone.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.extole.consumer.service.zone.ZoneRenderResponse;

public class ZoneInvalidRedirectException extends Exception {

    private final ZoneRenderResponse zoneResponse;
    private final List<String> invalidRedirectUrls;

    public ZoneInvalidRedirectException(ZoneRenderResponse zoneResponse, String message,
        List<String> invalidRedirectUrls) {
        super(message);
        this.zoneResponse = zoneResponse;
        this.invalidRedirectUrls = Collections.unmodifiableList(new ArrayList<>(invalidRedirectUrls));
    }

    public ZoneInvalidRedirectException(ZoneRenderResponse zoneResponse, String message,
        List<String> invalidRedirectUrls, Throwable cause) {
        super(message, cause);
        this.zoneResponse = zoneResponse;
        this.invalidRedirectUrls = Collections.unmodifiableList(new ArrayList<>(invalidRedirectUrls));
    }

    public List<String> getInvalidRedirectUrls() {
        return invalidRedirectUrls;
    }

    public ZoneRenderResponse getZoneResponse() {
        return zoneResponse;
    }

}
