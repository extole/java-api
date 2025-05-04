package com.extole.client.rest.tango;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;

public class ClientTangoSettingsAccountResponse {
    private static final String CUSTOMER_ID = "customer_id";
    private static final String ACCOUNT_ID = "account_id";
    private static final String ENABLED = "enabled";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String STATUS = "status";
    private static final String CONTACT_EMAIL = "contact_email";
    private static final String CURRENT_BALANCE = "current_balance";
    private static final String HOURLY_AMOUNT_LIMIT_ENABLED = "hourly_amount_limit_enabled";
    private static final String DAILY_AMOUNT_LIMIT_ENABLED = "daily_amount_limit_enabled";
    private static final String HOURLY_AMOUNT_LIMIT = "hourly_amount_limit";
    private static final String DAILY_AMOUNT_LIMIT = "daily_amount_limit";
    private static final String FUNDS_AMOUNT_WARN_LIMIT = "funds_amount_warn_limit";

    private final String customerId;
    private final String accountId;
    private final boolean enabled;
    private final boolean hourlyAmountLimitEnabled;
    private final boolean dailyAmountLimitEnabled;
    private final FaceValueType faceValueType;
    private final TangoAccountStatus status;
    private final String contactEmail;
    private final BigDecimal currentBalance;
    private final BigDecimal hourlyAmountLimit;
    private final BigDecimal dailyAmountLimit;
    private final BigDecimal fundsAmountWarnLimit;

    public ClientTangoSettingsAccountResponse(@JsonProperty(CUSTOMER_ID) String customerId,
        @JsonProperty(ACCOUNT_ID) String accountId, @JsonProperty(ENABLED) boolean enabled,
        @JsonProperty(HOURLY_AMOUNT_LIMIT_ENABLED) boolean hourlyAmountLimitEnabled,
        @JsonProperty(DAILY_AMOUNT_LIMIT_ENABLED) boolean dailyAmountLimitEnabled,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType, @JsonProperty(STATUS) TangoAccountStatus status,
        @JsonProperty(CONTACT_EMAIL) String contactEmail, @JsonProperty(CURRENT_BALANCE) BigDecimal currentBalance,
        @JsonProperty(HOURLY_AMOUNT_LIMIT) BigDecimal hourlyAmountLimit,
        @JsonProperty(DAILY_AMOUNT_LIMIT) BigDecimal dailyAmountLimit,
        @JsonProperty(FUNDS_AMOUNT_WARN_LIMIT) BigDecimal fundsAmountWarnLimit) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.enabled = enabled;
        this.hourlyAmountLimitEnabled = hourlyAmountLimitEnabled;
        this.dailyAmountLimitEnabled = dailyAmountLimitEnabled;
        this.faceValueType = faceValueType;
        this.status = status;
        this.contactEmail = contactEmail;
        this.currentBalance = currentBalance;
        this.hourlyAmountLimit = hourlyAmountLimit;
        this.dailyAmountLimit = dailyAmountLimit;
        this.fundsAmountWarnLimit = fundsAmountWarnLimit;
    }

    @JsonProperty(CUSTOMER_ID)
    public String getCustomerId() {
        return customerId;
    }

    @JsonProperty(ACCOUNT_ID)
    public String getAccountId() {
        return accountId;
    }

    @JsonProperty(ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(HOURLY_AMOUNT_LIMIT_ENABLED)
    public boolean isHourlyAmountLimitEnabled() {
        return hourlyAmountLimitEnabled;
    }

    @JsonProperty(DAILY_AMOUNT_LIMIT_ENABLED)
    public boolean isDailyAmountLimitEnabled() {
        return dailyAmountLimitEnabled;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(STATUS)
    public TangoAccountStatus getStatus() {
        return status;
    }

    @JsonProperty(CONTACT_EMAIL)
    public String getContactEmail() {
        return contactEmail;
    }

    @JsonProperty(CURRENT_BALANCE)
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    @JsonProperty(HOURLY_AMOUNT_LIMIT)
    public BigDecimal getHourlyAmountLimit() {
        return hourlyAmountLimit;
    }

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
