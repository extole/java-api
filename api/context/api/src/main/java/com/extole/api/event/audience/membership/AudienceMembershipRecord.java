package com.extole.api.event.audience.membership;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface AudienceMembershipRecord {
    String getType();

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

    Map<String, String> getData();

    Map<String, String> getAppData();

    String getAudienceId();

    String getAudienceName();
}
