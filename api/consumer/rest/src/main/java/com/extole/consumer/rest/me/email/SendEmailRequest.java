package com.extole.consumer.rest.me.email;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendEmailRequest {
    private static final String JSON_ZONE_NAME = "zone_name";
    private static final String JSON_DATA = "data";

    private final String zoneName;
    private final Map<String, String> data;

    public SendEmailRequest(
        @JsonProperty(JSON_ZONE_NAME) String zoneName,
        @JsonProperty(JSON_DATA) Map<String, String> data) {
        this.zoneName = zoneName;
        this.data = data != null ? data : new HashMap<>();
    }

    @JsonProperty(JSON_ZONE_NAME)
    public String getZoneName() {
        return zoneName;
    }

    @Nullable
    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return data;
    }

}
