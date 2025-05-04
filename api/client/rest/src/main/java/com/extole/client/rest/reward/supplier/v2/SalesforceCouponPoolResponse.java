package com.extole.client.rest.reward.supplier.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SalesforceCouponPoolResponse {
    private static final String SETTINGS_ID = "settings_id";
    private static final String SETTINGS_NAME = "settings_name";
    private static final String COUPON_POOL_ID = "coupon_pool_id";
    private static final String COUPON_COUNT = "coupon_count";
    private static final String TYPE = "type";
    private static final String ENABLED = "enabled";

    private final String settingsId;
    private final String settingsName;
    private final String couponPoolId;
    private final Integer couponCount;
    private final SalesforceCouponPoolType type;
    private final Boolean enabled;

    public SalesforceCouponPoolResponse(
        @JsonProperty(SETTINGS_ID) String settingsId,
        @JsonProperty(SETTINGS_NAME) String settingsName,
        @JsonProperty(COUPON_POOL_ID) String couponPoolId,
        @JsonProperty(COUPON_COUNT) Integer couponCount,
        @JsonProperty(TYPE) SalesforceCouponPoolType type,
        @JsonProperty(ENABLED) Boolean enabled) {
        this.settingsId = settingsId;
        this.settingsName = settingsName;
        this.couponPoolId = couponPoolId;
        this.couponCount = couponCount;
        this.type = type;
        this.enabled = enabled;
    }

    @JsonProperty(SETTINGS_NAME)
    public String getSettingsName() {
        return settingsName;
    }

    @JsonProperty(SETTINGS_ID)
    public String getSettingsId() {
        return settingsId;
    }

    @JsonProperty(COUPON_POOL_ID)
    public String getCouponPoolId() {
        return couponPoolId;
    }

    @JsonProperty(COUPON_COUNT)
    public Integer getCouponCount() {
        return couponCount;
    }

    @JsonProperty(ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @JsonProperty(TYPE)
    SalesforceCouponPoolType getType() {
        return type;
    }
}
