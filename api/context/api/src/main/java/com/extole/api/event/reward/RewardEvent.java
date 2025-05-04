package com.extole.api.event.reward;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.Sandbox;

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

    String getPartnerRewardKeyType();

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

    /**
     * @deprecated use {@link #getSandbox()} instead
     */
    @Deprecated // TODO remove in ENG-22703
    String getContainer();

    Sandbox getSandbox();

    Map<String, Object> getData();

    String[] getTags();
}
