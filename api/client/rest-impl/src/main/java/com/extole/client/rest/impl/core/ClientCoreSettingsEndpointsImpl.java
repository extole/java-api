package com.extole.client.rest.impl.core;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.core.ClientCoreSettingsEndpoints;
import com.extole.client.rest.core.ClientCoreSettingsRequest;
import com.extole.client.rest.core.ClientCoreSettingsResponse;
import com.extole.client.rest.core.ClientCoreSettingsRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.client.core.ClientCoreSettings;
import com.extole.model.entity.client.core.CookieConsentPolicy;
import com.extole.model.entity.client.core.CookieDomainPolicy;
import com.extole.model.entity.client.core.CookiePolicy;
import com.extole.model.service.authorization.ExtoleAuthProviderTypeCredentialsException;
import com.extole.model.service.client.ClientBuilder;
import com.extole.model.service.client.ClientInvalidNameException;
import com.extole.model.service.client.ClientInvalidShortNameException;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.client.IncompatibleRewardRuleIdentityKeyException;
import com.extole.model.service.client.core.ClientCoreSettingCreativeRespondsHtmlEnabledIllegalStateException;
import com.extole.model.service.client.core.ClientCoreSettingsBuilder;
import com.extole.model.service.client.core.CoreVersionsException;
import com.extole.model.service.client.core.CoreVersionsService;
import com.extole.model.service.client.core.InvalidExtendedCoreJavascriptException;
import com.extole.model.service.client.support.SupportFieldValidationException;
import com.extole.model.service.program.ProgramInvalidProgramDomainException;
import com.extole.model.service.user.UserFirstNameInvalidLengthException;
import com.extole.model.service.user.UserInvalidEmailException;
import com.extole.model.service.user.UserLastNameInvalidLengthException;

@Provider
public class ClientCoreSettingsEndpointsImpl implements ClientCoreSettingsEndpoints {
    private final ClientService clientService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CoreVersionsService coreVersionsService;

    @Autowired
    public ClientCoreSettingsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientService clientService, CoreVersionsService coreVersionsService) {
        this.authorizationProvider = authorizationProvider;
        this.clientService = clientService;
        this.coreVersionsService = coreVersionsService;
    }

    @Override
    public ClientCoreSettingsResponse get(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toClientCoreSettingsResponse(
                clientService.getById(authorization, authorization.getClientId()).getCoreSettings());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientCoreSettingsResponse update(String accessToken, ClientCoreSettingsRequest request)
        throws UserAuthorizationRestException, ClientCoreSettingsRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientBuilder clientBuilder = clientService.updateClient(authorization);
            ClientCoreSettingsBuilder builder = clientBuilder.getClientCoreSettingsBuilder();
            if (request.getSource() != null) {
                builder.withExtendedCoreJavascript(request.getSource());
            }
            if (request.getOriginHostOverride() != null) {
                builder.withPublicOriginHostOverride(request.getOriginHostOverride());
            }
            if (!Strings.isNullOrEmpty(request.getVersion())) {
                String coreVersion = request.getVersion();
                if (!this.coreVersionsService.getAvailableCoreVersions().contains(coreVersion)) {
                    throw RestExceptionBuilder.newBuilder(ClientCoreSettingsRestException.class)
                        .withErrorCode(ClientCoreSettingsRestException.INVALID_CORE_VERSION)
                        .addParameter("core_version", coreVersion).build();
                }
                builder.withCoreVersion(coreVersion);
            }
            if (request.isLegacyTagsEnabled() != null) {
                builder.withLegacyTagsEnabled(request.isLegacyTagsEnabled().booleanValue());
            }
            if (request.isThirdPartyCookiesDisabled() != null) {
                builder.withThirdPartyCookiesDisabled(request.isThirdPartyCookiesDisabled().booleanValue());
            }
            if (request.isGlobalZoneParametersEnabled() != null) {
                builder.withGlobalZoneParametersEnabled(request.isGlobalZoneParametersEnabled().booleanValue());
            }
            if (request.isZonePostEnabled() != null) {
                builder.withZonePostEnabled(request.isZonePostEnabled().booleanValue());
            }
            if (request.isJsCreativeRespondsWithHtmlEnabled() != null) {
                builder.withJsCreativeRespondsWithHtmlEnabled(
                    request.isJsCreativeRespondsWithHtmlEnabled().booleanValue());
            }
            if (request.isAccessTokenIncludedInResponseEnabled() != null) {
                builder.withAccessTokenIncludedInResponseEnabled(
                    request.isAccessTokenIncludedInResponseEnabled().booleanValue());
            }
            if (request.isDeprecatedAccessTokenCookieAllowed() != null) {
                builder.withDeprecatedAccessTokenCookieAllowed(
                    request.isDeprecatedAccessTokenCookieAllowed().booleanValue());
            }
            if (request.getCookiePolicy() != null) {
                builder.withCookiePolicy(CookiePolicy.valueOf(request.getCookiePolicy().name()));
            }
            if (request.getCookieConsentPolicy() != null) {
                builder.withCookieConsentPolicy(CookieConsentPolicy.valueOf(request.getCookieConsentPolicy().name()));
            }
            if (request.getCookieDomainPolicy() != null) {
                builder
                    .withCookieDomainPolicy(CookieDomainPolicy.valueOf(request.getCookieDomainPolicy().name()));
            }
            Client client = clientBuilder.save();
            return toClientCoreSettingsResponse(client.getCoreSettings());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (InvalidExtendedCoreJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(ClientCoreSettingsRestException.class)
                .withErrorCode(ClientCoreSettingsRestException.INVALID_EXTENDED_CORE_JAVASCRIPT)
                .addParameter("output", e.getOutput())
                .withCause(e).build();
        } catch (ClientCoreSettingCreativeRespondsHtmlEnabledIllegalStateException e) {
            throw RestExceptionBuilder.newBuilder(ClientCoreSettingsRestException.class)
                .withErrorCode(ClientCoreSettingsRestException.ILLEGAL_STATE_FOR_CREATIVE_RESPONDS_HTML_ENABLED)
                .withCause(e).build();
        } catch (CoreVersionsException | ClientInvalidNameException | ClientInvalidShortNameException
            | UserFirstNameInvalidLengthException | UserLastNameInvalidLengthException | UserInvalidEmailException
            | ExtoleAuthProviderTypeCredentialsException | SupportFieldValidationException
            | ProgramInvalidProgramDomainException | IncompatibleRewardRuleIdentityKeyException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private static ClientCoreSettingsResponse toClientCoreSettingsResponse(ClientCoreSettings coreSettings) {
        return new ClientCoreSettingsResponse(coreSettings.getExtendedCoreJavascript().orElse(null),
            coreSettings.getCoreVersion(),
            coreSettings.isLegacyTagsEnabled(),
            coreSettings.isThirdPartyCookiesDisabled(),
            coreSettings.isGlobalZoneParametersEnabled(),
            coreSettings.isZonePostEnabled(),
            coreSettings.isJsCreativeRespondsWithHtmlEnabled(),
            coreSettings.isAccessTokenIncludedInResponseEnabled(),
            coreSettings.isDeprecatedAccessTokenCookieAllowed(),
            coreSettings.getPublicOriginHostOverride().map(HostAndPort::toString).orElse(null),
            ClientCoreSettingsRequest.CookiePolicy.valueOf(coreSettings.getCookiePolicy().name()),
            ClientCoreSettingsRequest.CookieConsentPolicy.valueOf(coreSettings.getCookieConsentPolicy().name()),
            ClientCoreSettingsRequest.CookieDomainPolicy.valueOf(coreSettings.getCookieDomainPolicy().name()));
    }
}
