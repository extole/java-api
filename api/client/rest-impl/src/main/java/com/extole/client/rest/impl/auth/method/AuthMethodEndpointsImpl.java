package com.extole.client.rest.impl.auth.method;

import static com.extole.model.entity.auth.provider.type.AuthProviderTypeProtocol.EXTOLE;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.client.rest.auth.method.AuthMethodDiscoverRequest;
import com.extole.client.rest.auth.method.AuthMethodEndpoints;
import com.extole.client.rest.auth.method.AuthMethodResponse;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeProtocol;
import com.extole.common.email.Email;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.model.entity.client.auth.provider.AuthProvider;
import com.extole.model.entity.user.User;
import com.extole.model.shared.client.auth.provider.AuthProviderCache;
import com.extole.model.shared.user.UserCache;
import com.extole.openid.connect.service.AuthRequest;
import com.extole.openid.connect.service.OpenIdConnectService;

@Provider
public class AuthMethodEndpointsImpl implements AuthMethodEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(AuthMethodEndpointsImpl.class);

    private static final String CSRF_TOKEN = "csrf_token";
    private static final String NONCE = "nonce";

    private final AuthProviderCache authProviderCache;
    private final ClientAuthorizationProvider authorizationProvider;
    private final UserCache userCache;
    private final OpenIdConnectService openIdConnectService;
    private final VerifiedEmailService verifiedEmailService;
    private final String extoleAuthProviderAuthUrl;

    @Autowired
    public AuthMethodEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        UserCache userCache,
        OpenIdConnectService openIdConnectService,
        VerifiedEmailService verifiedEmailService,
        AuthProviderCache authProviderCache,
        @Value("${extole.auth.method.auth.url:https://my.extole.com/}") String extoleAuthProviderAuthUrl) {
        this.authProviderCache = authProviderCache;
        this.authorizationProvider = authorizationProvider;
        this.userCache = userCache;
        this.openIdConnectService = openIdConnectService;
        this.verifiedEmailService = verifiedEmailService;
        this.extoleAuthProviderAuthUrl = extoleAuthProviderAuthUrl;
    }

    @Override
    public List<AuthMethodResponse> discoverAuthMethods(Optional<AuthMethodDiscoverRequest> discoverRequest) {
        if (discoverRequest.isPresent() && discoverRequest.get().getEmail() != null) {
            return buildPersonalizedAuthMethods(discoverRequest.get().getEmail());
        } else {
            return buildDefaultAuthMethods();
        }
    }

    @Override
    public List<AuthMethodResponse> discoverReAuthMethods(String accessToken) throws UserAuthorizationRestException {
        UserAuthorization authorization = authorizationProvider.getUserAuthorization(accessToken);
        User user = authorization.getIdentity();
        return ListUtils.union(buildOidcReAuthMethods(user), buildReAuthExtoleMethods(user));
    }

    private List<AuthMethodResponse> buildReAuthExtoleMethods(User user) {
        return userCache.getByNormalizedEmail(user.getNormalizedEmail()).stream()
            .flatMap(value -> authProviderCache.getAuthProviders(value.getClientId(), value.getId()).stream())
            .filter(authProvider -> authProvider.getAuthProviderType().getAuthProviderTypeProtocol() == EXTOLE)
            .collect(Collectors.toCollection(() -> new TreeSet<>(
                Comparator.comparing(authProvider -> authProvider.getAuthProviderType().getId().getValue()))))
            .stream()
            .map(authProvider -> toExtoleAuthMethodResponse(authProvider))
            .collect(Collectors.toList());
    }

    private List<AuthMethodResponse> buildOidcReAuthMethods(User user) {
        return openIdConnectService
            .getAllAuthRequests(user, true)
            .stream()
            .map(authRequest -> toOpenIdConnectAuthMethodResponse(authRequest))
            .collect(Collectors.toList());
    }

    private List<AuthMethodResponse> buildPersonalizedAuthMethods(String email) {
        Email verifiedEmail;
        try {
            verifiedEmail = verifiedEmailService.verifyEmail(email).getEmail();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            LOG.warn("Discover auth methods attempt with invalid email {} ", email);
            return buildDefaultAuthMethods();
        }

        List<User> users = userCache.getByNormalizedEmail(verifiedEmail.getNormalizedAddress());

        if (users.isEmpty()) {
            return buildDefaultAuthMethods();
        }

        List<AuthMethodResponse> oidcAuthMethods = openIdConnectService.getEnterpriseAuthRequests(verifiedEmail, false)
            .stream()
            .map(authRequest -> toOpenIdConnectAuthMethodResponse(authRequest))
            .collect(Collectors.toList());

        return ListUtils.union(buildDefaultAuthMethods(), oidcAuthMethods);
    }

    private List<AuthMethodResponse> buildDefaultAuthMethods() {
        return ListUtils.union(buildDefaultPasswordAuthMethod(), buildDefaultSocialAuthMethods());
    }

    private List<AuthMethodResponse> buildDefaultSocialAuthMethods() {
        return openIdConnectService.getSocialAuthRequests().stream()
            .map(authRequest -> toOpenIdConnectAuthMethodResponse(authRequest))
            .collect(Collectors.toList());
    }

    private List<AuthMethodResponse> buildDefaultPasswordAuthMethod() {
        return Collections.singletonList(new AuthMethodResponse("Default Extole Password Auth Method",
            AuthProviderTypeProtocol.EXTOLE, extoleAuthProviderAuthUrl, Collections.emptyMap(), null));
    }

    private AuthMethodResponse toOpenIdConnectAuthMethodResponse(AuthRequest authRequest) {
        return AuthMethodResponse.builder()
            .withName(authRequest.getName())
            .withAuthProviderTypeProtocol(AuthProviderTypeProtocol.OIDC)
            .withAuthUrl(authRequest.getAuthUri().toString())
            .withData(ImmutableMap.<String, String>builder()
                .put(CSRF_TOKEN, authRequest.getCsrfToken())
                .put(NONCE, authRequest.getNonce())
                .build())
            .withDescription(authRequest.getDescription().orElse(null))
            .build();
    }

    private AuthMethodResponse toExtoleAuthMethodResponse(AuthProvider authProvider) {
        return AuthMethodResponse.builder()
            .withName(authProvider.getName())
            .withAuthProviderTypeProtocol(AuthProviderTypeProtocol.EXTOLE)
            .withAuthUrl(extoleAuthProviderAuthUrl)
            .withData(Collections.emptyMap())
            .withDescription(authProvider.getDescription().orElse(null))
            .build();
    }

}
