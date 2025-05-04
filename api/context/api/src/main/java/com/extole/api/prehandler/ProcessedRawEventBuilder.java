package com.extole.api.prehandler;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ProcessedRawEventBuilder {

    ProcessedRawEventBuilder withClientDomain(String clientDomain);

    ProcessedRawEventBuilder withEventName(String eventName);

    ProcessedRawEventBuilder withEventTime(String eventTime);

    ProcessedRawEventBuilder withSandbox(String sandboxId);

    ProcessedRawEventBuilder withAppType(String appType);

    ProcessedRawEventBuilder withDefaultAppType(String defaultAppType);

    ProcessedRawEventBuilder addSourceGeoIp(String ipAddress);

    ProcessedRawEventBuilder removeSourceGeoIp(String ipAddress);

    ProcessedRawEventBuilder withDeviceId(String deviceId);

    ProcessedRawEventBuilder withPageId(String pageId);

    ProcessedRawEventBuilder addJwt(String jwt);

    ProcessedRawEventBuilder addData(Map<String, Object> data);

    ProcessedRawEventBuilder addData(String name, Object value);

    ProcessedRawEventBuilder addVerifiedData(String name, Object value);

    ProcessedRawEventBuilder removeData(String name);

    ProcessedRawEventBuilder addAppData(Map<String, String> data);

    ProcessedRawEventBuilder addAppData(String name, String value);

    ProcessedRawEventBuilder removeAppData(String name);

    ProcessedRawEventBuilder withDeviceType(String deviceType);

    ProcessedRawEventBuilder withDeviceOs(String deviceOs);
}
