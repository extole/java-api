package com.extole.api.impl.event.audience.membership;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.api.event.audience.membership.AudienceMembershipRecord;
import com.extole.common.lang.ToString;

public final class AudienceMembershipRecordImpl implements AudienceMembershipRecord, Serializable {

    private final String type;
    private final String id;
    private final String clientId;
    private final String eventTime;
    private final String requestTime;
    private final String deviceProfileId;
    private final String identityProfileId;
    private final String personId;
    private final String container;
    private final Map<String, String> data;
    private final Map<String, String> appData;
    private final String audienceId;
    private final String audienceName;
    private final Map<String, String> metadata;

    private AudienceMembershipRecordImpl(String type, String id, String clientId, String eventTime, String requestTime,
        @Nullable String deviceProfileId, @Nullable String identityProfileId, String personId, String container,
        @Nullable Map<String, String> data, @Nullable Map<String, String> appData, String audienceId,
        String audienceName, Map<String, String> metadata) {
        this.type = type;
        this.id = id;
        this.clientId = clientId;
        this.eventTime = eventTime;
        this.requestTime = requestTime;
        this.deviceProfileId = deviceProfileId;
        this.identityProfileId = identityProfileId;
        this.personId = personId;
        this.container = container;
        this.data = data != null ? ImmutableMap.copyOf(data) : Maps.newHashMap();
        this.appData = appData != null ? ImmutableMap.copyOf(appData) : Maps.newHashMap();
        this.audienceId = audienceId;
        this.audienceName = audienceName;
        this.metadata = metadata;
    }

    @Override
    public String getType() {
        return type;
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
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public Map<String, String> getAppData() {
        return appData;
    }

    @Override
    public String getAudienceId() {
        return audienceId;
    }

    @Override
    public String getAudienceName() {
        return audienceName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String type;
        private String id;
        private String clientId;
        private String eventTime;
        private String requestTime;
        private String deviceProfileId;
        private String identityProfileId;
        private String personId;
        private String container;
        private Map<String, String> data;
        private Map<String, String> appData;
        private String audienceId;
        private String audienceName;
        private Map<String, String> metadata;

        private Builder() {
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
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

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Builder withAppData(Map<String, String> appData) {
            this.appData = appData;
            return this;
        }

        public Builder withAudienceId(String audienceId) {
            this.audienceId = audienceId;
            return this;
        }

        public Builder withAudienceName(String audienceName) {
            this.audienceName = audienceName;
            return this;
        }

        public Builder withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public AudienceMembershipRecordImpl build() {
            return new AudienceMembershipRecordImpl(type, id, clientId, eventTime, requestTime, deviceProfileId,
                identityProfileId, personId, container, data, appData, audienceId, audienceName, metadata);
        }
    }
}
