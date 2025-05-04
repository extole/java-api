package com.extole.api.event;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.PublicClientDomain;
import com.extole.api.event.geoip.GeoIp;

@Schema
public interface ProcessedRawEvent {

    PublicClientDomain getClientDomain();

    String getEventName();

    String getEventTime();

    Sandbox getSandbox();

    GeoIp[] getSourceGeoIps();

    String getDeviceId();

    String getPageId();

    String getAppType();

    Map<String, String> getAppData();

    Map<String, Object> getData();

    Map<String, Object> getVerifiedData();

    EventData[] getAllData();

    String getDeviceType();

    String getDeviceOs();
}
