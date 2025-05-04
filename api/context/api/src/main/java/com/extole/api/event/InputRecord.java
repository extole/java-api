package com.extole.api.event;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface InputRecord {

    String getId();

    String getClientId();

    String getEventTime();

    String getRequestTime();

    @Nullable
    String getDeviceProfileId();

    @Nullable
    String getIdentityProfileId();

    String getPersonId();

    String getContainer();

    String getName();

    String getLocale();

    String getApiType();

    @Nullable
    String getUserId();

    Map<String, String> getData();

    @Nullable
    String getUserAgent();

    Map<String, String> getAppData();

    String getDeviceType();

    String getDeviceOs();

    String getAppType();

    @Nullable
    String getIpAddress();

    Map<String, List<String>> getHttpHeaders();
}
