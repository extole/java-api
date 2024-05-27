package com.extole.api.person;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonReward {
    String[] getRewardSlots();

    String getPersonRole();

    String getFaceValue();

    String getFaceValueType();

    String getId();

    // TODO Remove nullable in ENG-9668
    @Nullable
    String getRewardId();

    String getDateEarned();

    // TODO Remove nullable in ENG-9668
    @Nullable
    String getState();

    @Nullable
    String getPartnerRewardId();

    // TODO Remove nullable in ENG-9668
    @Nullable
    String getSandbox();

    // TODO Remove nullable in ENG-9668
    @Nullable
    String getProgramLabel();

    // TODO Remove nullable in ENG-9668
    @Nullable
    String getCampaignId();

    String getRewardedDate();

    String getRewardSupplierId();

    Map<String, String> getData();

    Optional<String> expiryDate();

    @Nullable
    String getRedeemedDate();
}
