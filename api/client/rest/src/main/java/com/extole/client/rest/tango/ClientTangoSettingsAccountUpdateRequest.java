package com.extole.client.rest.tango;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientTangoSettingsAccountUpdateRequest {

    private static final String JSON_PROPERTY_ENABLED = "enabled";
    private static final String HOURLY_AMOUNT_LIMIT_ENABLED = "hourly_amount_limit_enabled";
    private static final String DAILY_AMOUNT_LIMIT_ENABLED = "daily_amount_limit_enabled";
    private static final String HOURLY_AMOUNT_LIMIT = "hourly_amount_limit";
    private static final String DAILY_AMOUNT_LIMIT = "daily_amount_limit";
    private static final String FUNDS_AMOUNT_WARN_LIMIT = "funds_amount_warn_limit";

    private final Boolean enabled;
    private final Boolean hourlyAmountLimitEnabled;
    private final Boolean dailyAmountLimitEnabled;
    private final BigDecimal hourlyAmountLimit;
    private final BigDecimal dailyAmountLimit;
    private final BigDecimal fundsAmountWarnLimit;

    @JsonCreator
    public ClientTangoSettingsAccountUpdateRequest(@Nullable @JsonProperty(JSON_PROPERTY_ENABLED) Boolean enabled,
        @Nullable @JsonProperty(HOURLY_AMOUNT_LIMIT_ENABLED) Boolean hourlyAmountLimitEnabled,
        @Nullable @JsonProperty(DAILY_AMOUNT_LIMIT_ENABLED) Boolean dailyAmountLimitEnabled,
        @Nullable @JsonProperty(HOURLY_AMOUNT_LIMIT) BigDecimal hourlyAmountLimit,
        @Nullable @JsonProperty(DAILY_AMOUNT_LIMIT) BigDecimal dailyAmountLimit,
        @Nullable @JsonProperty(FUNDS_AMOUNT_WARN_LIMIT) BigDecimal fundsAmountWarnLimit) {
        this.enabled = enabled;
        this.hourlyAmountLimitEnabled = hourlyAmountLimitEnabled;
        this.dailyAmountLimitEnabled = dailyAmountLimitEnabled;
        this.hourlyAmountLimit = hourlyAmountLimit;
        this.dailyAmountLimit = dailyAmountLimit;
        this.fundsAmountWarnLimit = fundsAmountWarnLimit;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @Nullable
    @JsonProperty(HOURLY_AMOUNT_LIMIT_ENABLED)
    public Boolean isHourlyAmountLimitEnabled() {
        return hourlyAmountLimitEnabled;
    }

    @Nullable
    @JsonProperty(DAILY_AMOUNT_LIMIT_ENABLED)
    public Boolean isDailyAmountLimitEnabled() {
        return dailyAmountLimitEnabled;
    }

    @Nullable
    @JsonProperty(HOURLY_AMOUNT_LIMIT)
    public BigDecimal getHourlyAmountLimit() {
        return hourlyAmountLimit;
    }

    @Nullable
    @JsonProperty(DAILY_AMOUNT_LIMIT)
    public BigDecimal getDailyAmountLimit() {
        return dailyAmountLimit;
    }

    @JsonProperty(FUNDS_AMOUNT_WARN_LIMIT)
    public BigDecimal getFundsAmountWarnLimit() {
        return fundsAmountWarnLimit;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
