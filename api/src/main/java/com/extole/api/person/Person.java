package com.extole.api.person;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Person {

    String getId();

    @Nullable
    String getEmail();

    @Nullable
    String getNormalizedEmail();

    @Nullable
    String getDisplacedPersonId();

    @Nullable
    String getFirstName();

    @Nullable
    String getLastName();

    @Nullable
    String getLocale();

    boolean isBlocked();

    @Deprecated // TODO remove, use getData() - ENG-15534
    Map<String, Object> getPrivateData();

    @Deprecated // TODO remove, use getData() - ENG-15534
    Map<String, Object> getPublicData();

    Map<String, Object> getData();

    @Deprecated // TODO remove in favor of getLocale(), ENG-10118
    String getPreferredLocale();

    PersonReward[] getRewards();

    PersonReferral[] getRecentAssociatedFriends();

    PersonReferral[] getRecentAssociatedAdvocates();

    RequestContext[] getRecentRequestContexts();

    PersonStep[] getSteps();

    Shareable[] getShareables();

    @Nullable
    String getPartnerUserId();

    @Nullable
    String getProfilePictureUrl();

    PersonJourney[] getJourneys();

    PersonAudienceMembership[] getAudienceMemberships();

}
