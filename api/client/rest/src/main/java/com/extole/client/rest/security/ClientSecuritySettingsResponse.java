package com.extole.client.rest.security;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.settings.PasswordStrength;
import com.extole.common.lang.ToString;

public class ClientSecuritySettingsResponse {

    private static final String PASSWORD_LENGTH_MINIMUM = "password_length_minimum";
    private static final String PASSWORD_REUSE_LIMIT = "password_reuse_limit";
    private static final String PASSWORD_CHANGE_LIMIT = "password_change_limit";
    private static final String PASSWORD_LIFETIME = "password_lifetime";
    private static final String PASSWORD_STRENGTH = "password_strength";
    private static final String FAILED_LOGINS_LIMIT = "failed_logins_limit";
    private static final String CLIENT_TOKEN_LIFETIME = "client_token_lifetime";
    private static final String CONSUMER_TOKEN_LIFETIME = "consumer_token_lifetime";
    private static final String CLIENT_TOKEN_TRANSMISSION_CHANNELS = "client_token_transmission_channels";
    private static final String CONSUMER_TOKEN_TRANSMISSION_CHANNELS = "consumer_token_transmission_channels";

    private final Integer minimumPasswordLength;
    private final Integer passwordReuseLimit;
    private final Integer dailyPasswordChangeLimit;
    private final Integer passwordExpirationDays;
    private final PasswordStrength passwordStrength;
    private final Integer failedLoginsLimit;
    private final Long clientTokenLifetime;
    private final Long consumerTokenLifetime;
    private final Set<TokenTransmissionChannel> clientTokenTransmissionChannels;
    private final Set<TokenTransmissionChannel> consumerTokenTransmissionChannels;

    @JsonCreator
    public ClientSecuritySettingsResponse(
        @JsonProperty(PASSWORD_LENGTH_MINIMUM) Integer minimumPasswordLength,
        @JsonProperty(PASSWORD_REUSE_LIMIT) Integer passwordReuseLimit,
        @JsonProperty(PASSWORD_CHANGE_LIMIT) Integer dailyPasswordChangeLimit,
        @JsonProperty(PASSWORD_LIFETIME) Integer passwordExpirationDays,
        @JsonProperty(PASSWORD_STRENGTH) PasswordStrength passwordStrength,
        @JsonProperty(FAILED_LOGINS_LIMIT) Integer failedLoginsLimit,
        @JsonProperty(CLIENT_TOKEN_LIFETIME) Long clientTokenLifetime,
        @JsonProperty(CONSUMER_TOKEN_LIFETIME) Long consumerTokenLifetime,
        @JsonProperty(CLIENT_TOKEN_TRANSMISSION_CHANNELS) Set<TokenTransmissionChannel> clientTokenChannels,
        @JsonProperty(CONSUMER_TOKEN_TRANSMISSION_CHANNELS) Set<TokenTransmissionChannel> consumerTokenChannels) {
        this.minimumPasswordLength = minimumPasswordLength;
        this.passwordReuseLimit = passwordReuseLimit;
        this.dailyPasswordChangeLimit = dailyPasswordChangeLimit;
        this.passwordExpirationDays = passwordExpirationDays;
        this.passwordStrength = passwordStrength;
        this.failedLoginsLimit = failedLoginsLimit;
        this.clientTokenLifetime = clientTokenLifetime;
        this.consumerTokenLifetime = consumerTokenLifetime;
        this.clientTokenTransmissionChannels = clientTokenChannels;
        this.consumerTokenTransmissionChannels = consumerTokenChannels;
    }

    @JsonProperty(PASSWORD_LENGTH_MINIMUM)
    public Integer getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    @JsonProperty(PASSWORD_REUSE_LIMIT)
    public Integer getPasswordReuseLimit() {
        return passwordReuseLimit;
    }

    @JsonProperty(PASSWORD_CHANGE_LIMIT)
    public Integer getDailyPasswordChangeLimit() {
        return dailyPasswordChangeLimit;
    }

    @JsonProperty(PASSWORD_LIFETIME)
    public Integer getPasswordExpirationDays() {
        return passwordExpirationDays;
    }

    @JsonProperty(PASSWORD_STRENGTH)
    public PasswordStrength getPasswordStrength() {
        return passwordStrength;
    }

    @JsonProperty(FAILED_LOGINS_LIMIT)
    public Integer getFailedLoginsLimit() {
        return failedLoginsLimit;
    }

    @JsonProperty(CLIENT_TOKEN_LIFETIME)
    public Long getClientTokenLifetime() {
        return clientTokenLifetime;
    }

    @JsonProperty(CONSUMER_TOKEN_LIFETIME)
    public Long getConsumerTokenLifetime() {
        return consumerTokenLifetime;
    }

    @JsonProperty(CLIENT_TOKEN_TRANSMISSION_CHANNELS)
    public Set<TokenTransmissionChannel> getClientTokenTransmissionChannels() {
        return clientTokenTransmissionChannels;
    }

    @JsonProperty(CONSUMER_TOKEN_TRANSMISSION_CHANNELS)
    public Set<TokenTransmissionChannel> getConsumerTokenTransmissionChannels() {
        return consumerTokenTransmissionChannels;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
