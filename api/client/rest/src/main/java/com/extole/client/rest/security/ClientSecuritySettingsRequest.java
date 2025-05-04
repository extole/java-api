package com.extole.client.rest.security;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.settings.PasswordStrength;
import com.extole.common.lang.ToString;

public class ClientSecuritySettingsRequest {

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

    public static Builder builder() {
        return new Builder();
    }

    public ClientSecuritySettingsRequest(
        @Nullable @JsonProperty(PASSWORD_LENGTH_MINIMUM) Integer minimumPasswordLength,
        @Nullable @JsonProperty(PASSWORD_REUSE_LIMIT) Integer passwordReuseLimit,
        @Nullable @JsonProperty(PASSWORD_CHANGE_LIMIT) Integer dailyPasswordChangeLimit,
        @Nullable @JsonProperty(PASSWORD_LIFETIME) Integer passwordExpirationDays,
        @Nullable @JsonProperty(PASSWORD_STRENGTH) PasswordStrength passwordStrength,
        @Nullable @JsonProperty(FAILED_LOGINS_LIMIT) Integer failedLoginsLimit,
        @Nullable @JsonProperty(CLIENT_TOKEN_LIFETIME) Long clientTokenLifetime,
        @Nullable @JsonProperty(CONSUMER_TOKEN_LIFETIME) Long consumerTokenLifetime,
        @Nullable @JsonProperty(CLIENT_TOKEN_TRANSMISSION_CHANNELS) Set<TokenTransmissionChannel> clientTokenChannels,
        @Nullable @JsonProperty(CONSUMER_TOKEN_TRANSMISSION_CHANNELS) Set<
            TokenTransmissionChannel> consumerTokenChannels) {
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

    @Nullable
    @JsonProperty(PASSWORD_LENGTH_MINIMUM)
    public Integer getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    @Nullable
    @JsonProperty(PASSWORD_REUSE_LIMIT)
    public Integer getPasswordReuseLimit() {
        return passwordReuseLimit;
    }

    @Nullable
    @JsonProperty(PASSWORD_CHANGE_LIMIT)
    public Integer getDailyPasswordChangeLimit() {
        return dailyPasswordChangeLimit;
    }

    @Nullable
    @JsonProperty(PASSWORD_LIFETIME)
    public Integer getPasswordExpirationDays() {
        return passwordExpirationDays;
    }

    @Nullable
    @JsonProperty(PASSWORD_STRENGTH)
    public PasswordStrength getPasswordStrength() {
        return passwordStrength;
    }

    @Nullable
    @JsonProperty(FAILED_LOGINS_LIMIT)
    public Integer getFailedLoginsLimit() {
        return failedLoginsLimit;
    }

    @Nullable
    @JsonProperty(CLIENT_TOKEN_LIFETIME)
    public Long getClientTokenLifetime() {
        return clientTokenLifetime;
    }

    @Nullable
    @JsonProperty(CONSUMER_TOKEN_LIFETIME)
    public Long getConsumerTokenLifetime() {
        return consumerTokenLifetime;
    }

    @Nullable
    @JsonProperty(CLIENT_TOKEN_TRANSMISSION_CHANNELS)
    public Set<TokenTransmissionChannel> getClientTokenTransmissionChannels() {
        return clientTokenTransmissionChannels;
    }

    @Nullable
    @JsonProperty(CONSUMER_TOKEN_TRANSMISSION_CHANNELS)
    public Set<TokenTransmissionChannel> getConsumerTokenTransmissionChannels() {
        return consumerTokenTransmissionChannels;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private Integer minimumPasswordLength;
        private Integer passwordReuseLimit;
        private Integer dailyPasswordChangeLimit;
        private Integer passwordExpirationDays;
        private PasswordStrength passwordStrength;
        private Integer failedLoginsLimit;
        private Long clientTokenLifetime;
        private Long consumerTokenLifetime;
        private Set<TokenTransmissionChannel> clientTokenTransmissionChannels;
        private Set<TokenTransmissionChannel> consumerTokenTransmissionChannels;

        private Builder() {

        }

        public Builder withMinimumPasswordLength(Integer minimumPasswordLength) {
            this.minimumPasswordLength = minimumPasswordLength;
            return this;
        }

        public Builder withPasswordReuseLimit(Integer passwordReuseLimit) {
            this.passwordReuseLimit = passwordReuseLimit;
            return this;
        }

        public Builder withDailyPasswordChangeLimit(Integer dailyPasswordChangeLimit) {
            this.dailyPasswordChangeLimit = dailyPasswordChangeLimit;
            return this;
        }

        public Builder withPasswordExpirationDays(Integer passwordExpirationDays) {
            this.passwordExpirationDays = passwordExpirationDays;
            return this;
        }

        public Builder withPasswordStrength(PasswordStrength passwordStrength) {
            this.passwordStrength = passwordStrength;
            return this;
        }

        public Builder withFailedLoginsLimit(Integer failedLoginsLimit) {
            this.failedLoginsLimit = failedLoginsLimit;
            return this;
        }

        public Builder withClientTokenLifetime(Long clientTokenLifetime) {
            this.clientTokenLifetime = clientTokenLifetime;
            return this;
        }

        public Builder withConsumerTokenLifetime(Long consumerTokenLifetime) {
            this.consumerTokenLifetime = consumerTokenLifetime;
            return this;
        }

        public Builder withClientTokenTransmissionChannels(Set<TokenTransmissionChannel> channels) {
            this.clientTokenTransmissionChannels = channels;
            return this;
        }

        public Builder withConsumerTokenTransmissionChannels(Set<TokenTransmissionChannel> channels) {
            this.consumerTokenTransmissionChannels = channels;
            return this;
        }

        public ClientSecuritySettingsRequest build() {
            return new ClientSecuritySettingsRequest(minimumPasswordLength,
                passwordReuseLimit,
                dailyPasswordChangeLimit,
                passwordExpirationDays,
                passwordStrength,
                failedLoginsLimit,
                clientTokenLifetime,
                consumerTokenLifetime,
                clientTokenTransmissionChannels,
                consumerTokenTransmissionChannels);
        }

    }

}
