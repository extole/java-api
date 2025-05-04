package com.extole.api.impl.event;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.api.event.InputRecord;
import com.extole.common.lang.ToString;

public final class InputRecordImpl implements InputRecord, Serializable {

    private final String id;
    private final String clientId;
    private final String eventTime;
    private final String requestTime;
    private final String deviceProfileId;
    private final String identityProfileId;
    private final String personId;
    private final String container;
    private final String name;
    private final String locale;
    private final String apiType;
    private final String userId;
    private final String userAgent;
    private final String deviceType;
    private final String deviceOs;
    private final Map<String, String> data;
    private final Map<String, String> appData;
    private final Map<String, String> metadata;
    private final Map<String, List<String>> httpHeaders;
    private final String appType;
    private final String ipAddress;

    private InputRecordImpl(String id, String clientId, String eventTime, String requestTime,
        @Nullable String deviceProfileId, @Nullable String identityProfileId, String personId, String container,
        String name, @Nullable String locale, String apiType, @Nullable String userId, @Nullable String userAgent,
        String deviceType, String deviceOs, @Nullable Map<String, String> data,
        @Nullable Map<String, String> appData, Map<String, String> metadata, Map<String, List<String>> httpHeaders,
        String appType, String ipAddress) {
        this.id = id;
        this.clientId = clientId;
        this.eventTime = eventTime;
        this.requestTime = requestTime;
        this.deviceProfileId = deviceProfileId;
        this.identityProfileId = identityProfileId;
        this.personId = personId;
        this.container = container;
        this.name = name;
        this.locale = locale;
        this.apiType = apiType;
        this.userId = userId;
        this.userAgent = userAgent;
        this.deviceType = deviceType;
        this.deviceOs = deviceOs;
        this.data = data != null ? ImmutableMap.copyOf(data) : Maps.newHashMap();
        this.appData = appData != null ? ImmutableMap.copyOf(appData) : Maps.newHashMap();
        this.metadata = metadata;
        this.httpHeaders = httpHeaders;
        this.appType = appType;
        this.ipAddress = ipAddress;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Override
    public String getRequestTime() {
        return requestTime;
    }

    @Nullable
    @Override
    public String getDeviceProfileId() {
        return deviceProfileId;
    }

    @Nullable
    @Override
    public String getIdentityProfileId() {
        return identityProfileId;
    }

    @Override
    public String getPersonId() {
        return personId;
    }

    @Override
    public String getContainer() {
        return container;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public String getApiType() {
        return apiType;
    }

    @Nullable
    @Override
    public String getUserId() {
        return userId;
    }

    @Nullable
    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String getDeviceOs() {
        return deviceOs;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public Map<String, String> getAppData() {
        return appData;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public Map<String, List<String>> getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String clientId;
        private String eventTime;
        private String requestTime;
        private String deviceProfileId;
        private String identityProfileId;
        private String personId;
        private String container;
        private String name;
        private String locale;
        private String apiType;
        private String userId;
        private String userAgent;
        private String deviceType;
        private String deviceOs;
        private Map<String, String> data;
        private Map<String, String> appData;
        private Map<String, String> metadata;
        private Map<String, List<String>> httpHeaders;
        private String appType;
        private String ipAddress;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withEventTime(String eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder withRequestTime(String requestTime) {
            this.requestTime = requestTime;
            return this;
        }

        public Builder withDeviceProfileId(String deviceProfileId) {
            this.deviceProfileId = deviceProfileId;
            return this;
        }

        public Builder withIdentityProfileId(String identityProfileId) {
            this.identityProfileId = identityProfileId;
            return this;
        }

        public Builder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder withApiType(String apiType) {
            this.apiType = apiType;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder withDeviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder withDeviceOs(String deviceOs) {
            this.deviceOs = deviceOs;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Builder withAppData(Map<String, String> appData) {
            this.appData = appData;
            return this;
        }

        public Builder withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withHttpHeaders(Map<String, List<String>> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder withAppType(String appType) {
            this.appType = appType;
            return this;
        }

        public Builder withIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public InputRecordImpl build() {
            return new InputRecordImpl(id, clientId, eventTime, requestTime, deviceProfileId, identityProfileId,
                personId, container, name, locale, apiType, userId, userAgent, deviceType, deviceOs, data, appData,
                metadata, httpHeaders, appType, ipAddress);
        }
    }
}
