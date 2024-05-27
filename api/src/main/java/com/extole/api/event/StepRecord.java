package com.extole.api.event;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface StepRecord {

    String DATA_FIELD_LOCALE = "locale";
    String DATA_FIELD_COUNTRY = "country";
    String DATA_FIELD_LANGUAGE = "language";
    String DATA_FIELD_AMOUNT = "amount";
    String DATA_FIELD_SOURCE = "source";
    String DATA_FIELD_SOURCE_TYPE = "source_type";
    String DATA_FIELD_CHANNEL = "channel";
    String DATA_FIELD_REFERRAL_REASON = "referral_reason";
    String DATA_FIELD_REFERRAL_REASON_CODE = "referral_reason_code";
    String DATA_FIELD_PARTNER_USER_ID = "partner_user_id";
    String DATA_FIELD_RELATED_PERSON_ID = "related_person_id";
    String DATA_FIELD_PARTNER_EVENT_ID_NAME = "partner_event_id_name";
    String DATA_FIELD_PARTNER_EVENT_ID_VALUE = "partner_event_id_value";
    String DATA_FIELD_API_TYPE = "api_type";

    String getId();

    String getClientId();

    String getEventTime();

    String getRequestTime();

    String getProgramLabel();

    @Nullable
    String getCampaignId();

    @Nullable
    String getDeviceProfileId();

    @Nullable
    String getIdentityProfileId();

    String getPersonId();

    String getContainer();

    String getPrimaryStepName();

    String getName();

    boolean isFirstSiteVisit();

    @Nullable
    Boolean getFirstProgramVisit();

    @Nullable
    Boolean getFirstCampaignVisit();

    @Nullable
    String getQuality();

    @Deprecated // TODO remove ENG-14640
    @Nullable
    String getRelatedPersonId();

    String getRootEventId();

    String getVisitType();

    String getAttribution();

    Map<String, String> getData();

    @Nullable
    String getJourneyName();

    String getDeviceType();

    String getDeviceOs();

    @Nullable
    String getVariant();

    String getAppType();

    Map<String, String> getAppData();
}
