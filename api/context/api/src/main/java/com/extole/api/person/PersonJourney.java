package com.extole.api.person;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonJourney {

    String getId();

    String getCampaignId();

    String getJourneyName();

    String getContainer();

    @Nullable
    // TODO make non-nullable - ENG-13267
    String getProgramLabel();

    Map<String, Object> getData();

    @Nullable
    JourneyKey getKey();

    String getCreatedDate();

}
