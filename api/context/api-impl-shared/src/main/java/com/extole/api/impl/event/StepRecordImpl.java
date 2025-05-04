package com.extole.api.impl.event;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import com.extole.api.event.StepRecord;
import com.extole.common.lang.ToString;

public final class StepRecordImpl implements StepRecord, Serializable {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PERSON_ID = "personId";
    public static final String FIELD_RELATED_PERSON_ID = "relatedPersonId";
    public static final String FIELD_VISIT_TYPE = "visitType";
    public static final String FIELD_ATTRIBUTION = "attribution";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_METADATA = "metadata";

    private final String eventId;
    private final String clientId;
    private final String eventTime;
    private final String requestTime;
    private final String programLabel;
    private final String campaignId;
    private final String deviceProfileId;
    private final String identityProfileId;
    private final String personId;
    private final String container;
    private final String primaryStepName;
    private final String stepName;
    private final boolean firstSiteVisit;
    private final Boolean firstProgramVisit;
    private final Boolean firstCampaignVisit;
    private final String quality;
    private final String rootEventId;
    private final String relatedPersonId;
    private final String visitType;
    private final String attribution;
    private final Map<String, String> data;
    private final String journeyName;
    private final String deviceType;
    private final String deviceOs;
    private final String variant;
    private final String appType;
    private final Map<String, String> appData;
    private final Map<String, String> metadata;

    private StepRecordImpl(String eventId, String clientId, String eventTime, String requestTime, String programLabel,
        String campaignId, String deviceProfileId, String identityProfileId, String container, String primaryStepName,
        String stepName, boolean firstSiteVisit, Boolean firstProgramVisit, Boolean firstCampaignVisit, String quality,
        String rootEventId, String relatedPersonId, String visitType, String attribution, String personId,
        Map<String, String> data, String journeyName, String deviceType, String deviceOs, String appType,
        Map<String, String> appData, String variant, Map<String, String> metadata) {
        this.identityProfileId = identityProfileId;
        this.programLabel = programLabel;
        this.container = container;
        this.clientId = clientId;
        this.quality = quality;
        this.campaignId = campaignId;
        this.visitType = visitType;
        this.attribution = attribution;
        this.data = data;
        this.primaryStepName = primaryStepName;
        this.eventTime = eventTime;
        this.firstSiteVisit = firstSiteVisit;
        this.eventId = eventId;
        this.firstProgramVisit = firstProgramVisit;
        this.requestTime = requestTime;
        this.stepName = stepName;
        this.deviceProfileId = deviceProfileId;
        this.firstCampaignVisit = firstCampaignVisit;
        this.rootEventId = rootEventId;
        this.relatedPersonId = relatedPersonId;
        this.personId = personId;
        this.journeyName = journeyName;
        this.deviceType = deviceType;
        this.deviceOs = deviceOs;
        this.appType = appType;
        this.appData = appData;
        this.variant = variant;
        this.metadata = metadata;
    }

    @Override
    public String getId() {
        return eventId;
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

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getDeviceProfileId() {
        return deviceProfileId;
    }

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
    public String getPrimaryStepName() {
        return primaryStepName;
    }

    @Override
    public String getName() {
        return stepName;
    }

    @Override
    public boolean isFirstSiteVisit() {
        return firstSiteVisit;
    }

    @Override
    public Boolean getFirstProgramVisit() {
        return firstProgramVisit;
    }

    @Override
    public Boolean getFirstCampaignVisit() {
        return firstCampaignVisit;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    @Override
    public String getRelatedPersonId() {
        return relatedPersonId;
    }

    @Override
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public String getVisitType() {
        return visitType;
    }

    @Override
    public String getAttribution() {
        return attribution;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String getJourneyName() {
        return journeyName;
    }

    @Nullable
    @Override
    public String getDeviceType() {
        return deviceType;
    }

    @Nullable
    @Override
    public String getDeviceOs() {
        return deviceOs;
    }

    @Nullable
    @Override
    public String getVariant() {
        return variant;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public Map<String, String> getAppData() {
        return appData;
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
        private String eventId;
        private String clientId;
        private String eventTime;
        private String requestTime;
        private String programLabel;
        private String campaignId;
        private String deviceProfileId;
        private String identityProfileId;
        private String container;
        private String primaryStepName;
        private String stepName;
        private boolean firstSiteVisit;
        private Boolean firstProgramVisit;
        private Boolean firstCampaignVisit;
        private String quality;
        private String rootEventId;
        private String relatedPersonId;
        private String visitType;
        private String attribution;
        private String personId;
        private Map<String, String> data;
        private String journeyName;
        private String deviceType;
        private String deviceOs;
        private String appType;
        private String variant;
        private Map<String, String> appData;
        private Map<String, String> metadata;

        private Builder() {
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
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

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
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

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withPrimaryStepName(String primaryStepName) {
            this.primaryStepName = primaryStepName;
            return this;
        }

        public Builder withStepName(String stepName) {
            this.stepName = stepName;
            return this;
        }

        public Builder withFirstSiteVisit(boolean firstSiteVisit) {
            this.firstSiteVisit = firstSiteVisit;
            return this;
        }

        public Builder withFirstProgramVisit(Boolean firstProgramVisit) {
            this.firstProgramVisit = firstProgramVisit;
            return this;
        }

        public Builder withFirstCampaignVisit(Boolean firstCampaignVisit) {
            this.firstCampaignVisit = firstCampaignVisit;
            return this;
        }

        public Builder withQuality(String quality) {
            this.quality = quality;
            return this;
        }

        public Builder withRootEventId(String rootEventId) {
            this.rootEventId = rootEventId;
            return this;
        }

        public Builder withRelatedPersonId(String relatedPersonId) {
            this.relatedPersonId = relatedPersonId;
            return this;
        }

        public Builder withVisitType(String visitType) {
            this.visitType = visitType;
            return this;
        }

        public Builder withAttribution(String attribution) {
            this.attribution = attribution;
            return this;
        }

        public Builder withPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Maps.newHashMap(data);
            return this;
        }

        public Builder withJourneyName(String journeyName) {
            this.journeyName = journeyName;
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

        public Builder withAppType(String appType) {
            this.appType = appType;
            return this;
        }

        public Builder withVariant(String variant) {
            this.variant = variant;
            return this;
        }

        public Builder withAppData(Map<String, String> appData) {
            this.appData = appData;
            return this;
        }

        public Builder withMetadata(Map<String, String> metadata) {
            this.metadata = Maps.newHashMap(metadata);
            return this;
        }

        public StepRecordImpl build() {
            return new StepRecordImpl(eventId, clientId, eventTime, requestTime, programLabel, campaignId,
                deviceProfileId, identityProfileId, container, primaryStepName, stepName, firstSiteVisit,
                firstProgramVisit, firstCampaignVisit, quality, rootEventId, relatedPersonId, visitType, attribution,
                personId, data, journeyName, deviceType, deviceOs, appType, appData, variant, metadata);
        }
    }
}
