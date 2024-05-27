package com.extole.api.event.reward;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface RewardEvent {
    String getType();

    String getId();

    String getEventTime();

    String getRewardId();

    String getRewardName();

    String getCauseEventId();

    @Nullable
    String getRootEventId();

    String getRewardSupplierName();

    String getRewardSupplierId();

    @Nullable
    String getPartnerRewardSupplierId();

    String getRewardSupplierType();

    String getPersonId();

    String getDeviceProfileId();

    @Nullable
    String getIdentityProfileId();

    @Nullable
    String getPartnerUserId();

    BigDecimal getFaceValue();

    String getFaceValueType();

    @Nullable
    String getMessage();

    String getClientId();

    String getCampaignId();

    String getProgramLabel();

    String getClientDomainId();

    String getContainer();

    Map<String, Object> getData();

    String[] getTags();
}
