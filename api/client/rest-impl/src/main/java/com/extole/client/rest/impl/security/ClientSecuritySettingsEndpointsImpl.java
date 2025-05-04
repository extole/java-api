package com.extole.client.rest.impl.security;

import java.time.Duration;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.security.ClientSecuritySettingsEndpoints;
import com.extole.client.rest.security.ClientSecuritySettingsRequest;
import com.extole.client.rest.security.ClientSecuritySettingsResponse;
import com.extole.client.rest.security.ClientSecuritySettingsRestException;
import com.extole.client.rest.security.TokenTransmissionChannel;
import com.extole.client.rest.settings.PasswordStrength;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.client.security.ClientSecuritySettings;
import com.extole.model.service.client.security.ClientSecuritySettingsBuilder;
import com.extole.model.service.client.security.ClientSecuritySettingsService;
import com.extole.model.service.client.security.InvalidClientTokenLifetimeException;
import com.extole.model.service.client.security.InvalidConsumerTokenLifetimeException;
import com.extole.model.service.client.security.InvalidDailyPasswordChangeLimitException;
import com.extole.model.service.client.security.InvalidFailedLoginsLimitException;
import com.extole.model.service.client.security.InvalidMinimumPasswordLengthException;
import com.extole.model.service.client.security.InvalidPasswordExpirationDaysException;
import com.extole.model.service.client.security.InvalidPasswordReuseLimitException;
import com.extole.model.service.client.security.InvalidPasswordStrengthException;

@Provider
public class ClientSecuritySettingsEndpointsImpl implements ClientSecuritySettingsEndpoints {
    private final ClientSecuritySettingsService settingService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ClientSecuritySettingsEndpointsImpl(ClientSecuritySettingsService settingService,
        ClientAuthorizationProvider authorizationProvider) {
        this.settingService = settingService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public ClientSecuritySettingsResponse get(String accessToken) throws UserAuthorizationRestException {
        try {
            Authorization clientAuthorization = authorizationProvider.getClientAuthorization(accessToken);
            ClientSecuritySettings settings =
                settingService.getSettings(clientAuthorization, clientAuthorization.getClientId());
            return toClientSecuritySettingsResponse(settings);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ClientSecuritySettingsResponse update(String accessToken, ClientSecuritySettingsRequest request)
        throws UserAuthorizationRestException, ClientSecuritySettingsRestException {
        try {
            Authorization clientAuthorization = authorizationProvider.getClientAuthorization(accessToken);
            ClientSecuritySettingsBuilder settingsBuilder =
                settingService.updateSettings(clientAuthorization, clientAuthorization.getClientId());
            if (request.getMinimumPasswordLength() != null) {
                settingsBuilder.withMinimumPasswordLength(request.getMinimumPasswordLength());
            }
            if (request.getPasswordReuseLimit() != null) {
                settingsBuilder.withPasswordReuseLimit(request.getPasswordReuseLimit());
            }
            if (request.getDailyPasswordChangeLimit() != null) {
                settingsBuilder.withDailyPasswordChangeLimit(request.getDailyPasswordChangeLimit());
            }
            if (request.getPasswordExpirationDays() != null) {
                settingsBuilder.withPasswordExpirationDays(request.getPasswordExpirationDays());
            }
            if (request.getPasswordStrength() != null) {
                settingsBuilder.withPasswordStrength(com.extole.model.entity.client.security.PasswordStrength
                    .valueOf(request.getPasswordStrength().toString()));
            }
            if (request.getFailedLoginsLimit() != null) {
                settingsBuilder.withFailedLoginsLimit(request.getFailedLoginsLimit());
            }

            if (request.getClientTokenLifetime() != null) {
                settingsBuilder.withClientTokenLifetime(Duration.ofMillis(request.getClientTokenLifetime()));
            }

            if (request.getConsumerTokenLifetime() != null) {
                settingsBuilder.withConsumerTokenLifetime(Duration.ofMillis(request.getConsumerTokenLifetime()));
            }

            if (CollectionUtils.isNotEmpty(request.getClientTokenTransmissionChannels())) {
                settingsBuilder.withClientTokenTransmissionChannels(request.getClientTokenTransmissionChannels()
                    .stream()
                    .map(tokenTransmissionChannel -> com.extole.model.entity.client.security.TokenTransmissionChannel
                        .valueOf(tokenTransmissionChannel.name()))
                    .collect(Collectors.toSet()));
            }

            if (CollectionUtils.isNotEmpty(request.getConsumerTokenTransmissionChannels())) {
                settingsBuilder.withConsumerTokenTransmissionChannels(request.getConsumerTokenTransmissionChannels()
                    .stream()
                    .map(tokenTransmissionChannel -> com.extole.model.entity.client.security.TokenTransmissionChannel
                        .valueOf(tokenTransmissionChannel.name()))
                    .collect(Collectors.toSet()));
            }

            settingsBuilder.save();
            ClientSecuritySettings settings =
                settingService.getSettings(clientAuthorization, clientAuthorization.getClientId());
            return toClientSecuritySettingsResponse(settings);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (InvalidMinimumPasswordLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_PASSWORD_LENGTH_MINIMUM)
                .addParameter("password_length_minimum", request.getMinimumPasswordLength()).withCause(e).build();
        } catch (InvalidPasswordReuseLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_PASSWORD_REUSE_LIMIT)
                .addParameter("password_reuse_limit", request.getPasswordReuseLimit()).withCause(e).build();
        } catch (InvalidDailyPasswordChangeLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_PASSWORD_CHANGE_LIMIT)
                .addParameter("password_change_limit", request.getDailyPasswordChangeLimit()).withCause(e).build();
        } catch (InvalidPasswordExpirationDaysException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_PASSWORD_LIFETIME)
                .addParameter("password_lifetime", request.getPasswordExpirationDays()).withCause(e).build();
        } catch (InvalidPasswordStrengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_PASSWORD_STRENGTH)
                .addParameter("password_strength", request.getPasswordStrength()).withCause(e).build();
        } catch (InvalidClientTokenLifetimeException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_CLIENT_TOKEN_LIFETIME)
                .addParameter("client_token_lifetime", e.getValue().toMillis())
                .addParameter("min_client_token_lifetime", e.getMinExpectedValue().toMillis())
                .addParameter("max_client_token_lifetime", e.getMaxExpectedValue().toMillis())
                .withCause(e)
                .build();
        } catch (InvalidConsumerTokenLifetimeException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_CONSUMER_TOKEN_LIFETIME)
                .addParameter("consumer_token_lifetime", e.getValue().toMillis())
                .addParameter("min_consumer_token_lifetime", e.getMinExpectedValue().toMillis())
                .addParameter("max_consumer_token_lifetime", e.getMaxExpectedValue().toMillis())
                .withCause(e)
                .build();
        } catch (InvalidFailedLoginsLimitException e) {
            throw RestExceptionBuilder.newBuilder(ClientSecuritySettingsRestException.class)
                .withErrorCode(ClientSecuritySettingsRestException.INVALID_FAILED_LOGINS_LIMIT)
                .addParameter("failed_logins_limit", request.getFailedLoginsLimit()).withCause(e).build();
        }
    }

    private ClientSecuritySettingsResponse toClientSecuritySettingsResponse(ClientSecuritySettings settings) {
        return new ClientSecuritySettingsResponse(settings.getMinimumPasswordLength(),
            settings.getPasswordReuseLimit(),
            settings.getDailyPasswordChangeLimit(),
            settings.getPasswordExpirationDays(),
            PasswordStrength.valueOf(settings.getPasswordStrength().toString()),
            settings.getFailedLoginsLimit(),
            Long.valueOf(settings.getClientTokenLifetime().toMillis()),
            Long.valueOf(settings.getConsumerTokenLifetime().toMillis()),
            settings.getClientTokenTransmissionChannels().stream()
                .map(tokenTransmissionChannel -> TokenTransmissionChannel.valueOf(tokenTransmissionChannel.name()))
                .collect(Collectors.toSet()),
            settings.getConsumerTokenTransmissionChannels().stream()
                .map(tokenTransmissionChannel -> TokenTransmissionChannel.valueOf(tokenTransmissionChannel.name()))
                .collect(Collectors.toSet()));
    }
}
