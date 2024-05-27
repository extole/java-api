package com.extole.api.person;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonStep {

    enum Scope {
        PUBLIC, PRIVATE, CLIENT
    }

    String getPersonId();

    @Nullable
    String getCampaignId();

    @Nullable
    String getProgramLabel();

    String getStepName();

    String getEventId();

    String getRootEventId();

    String getCauseEventId();

    String getEventDate();

    String getCreatedDate();

    @Nullable
    String getValue();

    boolean isAliasName();

    String getQuality();

    @Nullable
    PartnerEventId getPartnerEventId();

    Map<String, Object> getData();

    @Deprecated // TODO remove, use getData() - ENG-15534
    Map<String, Object> getPublicData();

    @Deprecated // TODO remove, use getData() - ENG-15534
    Map<String, Object> getPrivateData();

    String getScope();

    String getContainer();

    @Nullable
    String getJourneyName();

}
