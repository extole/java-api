package com.extole.consumer.rest.impl.authorization.v4;

import static com.extole.consumer.event.service.processor.EventData.Source.REQUEST_BODY;
import static com.extole.consumer.rest.common.Scope.UPDATE_PROFILE;
import static com.extole.consumer.rest.impl.request.ConsumerContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.AuthorizationScopesDeniedException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.InvalidExpiresAtException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.email.Email;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.ExtoleCookieType;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.consumer.event.service.ConsumerEventSender;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.JwtDataExtractor;
import com.extole.consumer.rest.authorization.AuthorizationDurationRestException;
import com.extole.consumer.rest.authorization.v4.AuthorizationV4Endpoints;
import com.extole.consumer.rest.authorization.v4.TokenV4Request;
import com.extole.consumer.rest.authorization.v4.TokenV4Response;
import com.extole.consumer.rest.authorization.v4.UpgradeTokenV4Request;
import com.extole.consumer.rest.authorization.v4.UpgradeTokenV4RestException;
import com.extole.consumer.rest.common.AuthorizationIdentifyRestException;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.ReissueTokenRestException;
import com.extole.consumer.rest.common.Scope;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.email.authentication.AuthenticationEmailService;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonService;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class AuthorizationV4EndpointsImpl implements AuthorizationV4Endpoints {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationV4EndpointsImpl.class);

    private static final Duration TOKEN_MAXIMUM_TTL = Duration.ofSeconds(5 * 365 * 24 * 60 * 60);
    private static final Duration LONG_LIVED_TTL = Duration.ofDays(90);
    private static final Set<String> CONSUMER_SCOPES =
        Arrays.stream(Scope.values()).map(Enum::name).collect(Collectors.toSet());

    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final PersonAuthorizationService authorizationService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final AuthenticationEmailService authenticationEmailService;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final VerifiedEmailService verifiedEmailService;
    private final JwtDataExtractor jwtDataExtractor;

    @Inject
    public AuthorizationV4EndpointsImpl(
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse,
        BackendAuthorizationProvider backendAuthorizationProvider,
        PersonAuthorizationService authorizationService,
        ConsumerRequestContextService consumerRequestContextService,
        AuthenticationEmailService authenticationEmailService,
        PersonService personService,
        ConsumerEventSenderService consumerEventSenderService,
        JwtDataExtractor jwtDataExtractor,
        VerifiedEmailService verifiedEmailService) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.authorizationService = authorizationService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.authenticationEmailService = authenticationEmailService;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.jwtDataExtractor = jwtDataExtractor;
        this.verifiedEmailService = verifiedEmailService;
    }

    @Override
    public TokenV4Response createToken(Optional<TokenV4Request> tokenV4Request, String accessToken)
        throws ReissueTokenRestException, AuthorizationIdentifyRestException {
        TokenV4Request request = tokenV4Request.orElseGet(() -> new TokenV4Request(null, null));
        PersonAuthorization authorization;
        try {
            authorization = getAuthorizationFromConsumerRequestContext(accessToken, request);
        } catch (AuthorizationRestException e) {
            authorization = createLongLivedToken();
        }

        if (request.getDurationSeconds() != null) {
            authorization = createShortLived(authorization, request);
            servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(), authorization);

        } else if (!isLongLived(authorization)) {
            authorization = createTokenBasedOnShortLived(authorization, request);
            servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(), authorization);
        }

        return createTokenResponse(authorization);
    }

    @Override
    public TokenV4Response refreshToken(Optional<TokenV4Request> tokenV4Request, String accessToken)
        throws AuthorizationRestException, ReissueTokenRestException {
        TokenV4Request request = tokenV4Request.orElseGet(() -> new TokenV4Request(null, null));
        PersonAuthorization authorization;
        if (request.getDurationSeconds() == null) {
            authorization = handleLongLivedTokenRequest(accessToken, request);
        } else {
            authorization = handleShortLivedTokenRequest(accessToken, request);
        }
        servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(), authorization);
        return createTokenResponse(authorization);
    }

    @Override
    public TokenV4Response getTokenDetails(String requestedAccessToken) throws AuthorizationRestException {
        return createToken(requestedAccessToken);
    }

    @Override
    public TokenV4Response getTokenPost(String accessToken) throws AuthorizationRestException {
        return createToken(accessToken);
    }

    @Override
    public TokenV4Response getToken(String accessToken) {
        String accessTokenFromCookie = getFirstAccessTokenCookie();
        try {
            PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(accessToken)
                .build()
                .getAuthorization();
            if (!isLongLived(authorization)) {
                createLongLivedForSamePerson(authorization, accessToken, accessTokenFromCookie);
            }

            ensureConsumerAuthorization(authorization);
            return createTokenResponse(authorization);
        } catch (AuthorizationRestException e) {
            LOG.debug("Error authorizing token {}, error code {}", accessToken, e.getErrorCode());
            if (accessTokenFromCookie != null && !Objects.equals(accessToken, accessTokenFromCookie)) {
                LOG.debug("Trying to authorize with the value from cookie {}" + accessTokenFromCookie);
                try {
                    ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
                        .withAccessToken(accessTokenFromCookie)
                        .build();

                    ensureConsumerAuthorization(requestContext.getAuthorization());
                    return createTokenResponse(requestContext.getAuthorization());
                } catch (AuthorizationRestException ex) {
                    LOG.info("Cannot successfully authorize with token from cookie {}, error code: {}. "
                        + "Will create a new one", accessTokenFromCookie, ex.getErrorCode());
                }
            }

            LOG.debug("Creating implicit authorization");

            try {
                PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
                    .build()
                    .getAuthorization();
                LOG.debug("Implicit authorization successfully created {}", authorization);
                return createTokenResponse(authorization);
            } catch (AuthorizationRestException ex) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(ex)
                    .build();
            }

        }
    }

    private PersonAuthorization getAuthorizationFromConsumerRequestContext(String accessToken,
        TokenV4Request tokenV4Request)
        throws AuthorizationIdentifyRestException, ReissueTokenRestException, AuthorizationRestException {

        List<EventData> dataFromJwt = getDataFromJwt(tokenV4Request.getJwt());
        Optional<EventData> emailData = getEmailFromRequestData(tokenV4Request.getEmail());

        Optional<Email> verifiedRequestEmail = Optional.empty();
        Optional<Email> verifiedJwtEmail = Optional.empty();

        if (emailData.isPresent()) {
            String requestEmail = emailData.get().getValue().toString();
            verifiedRequestEmail = Optional.of(validateEmail(requestEmail));
        }

        Optional<EventData> jwtEmailData = dataFromJwt
            .stream().filter(data -> data.getName().equals("email")).findFirst();

        if (jwtEmailData.isPresent()) {
            String jwtEmail = jwtEmailData.get().getValue().toString();
            verifiedJwtEmail = Optional.of(validateEmail(jwtEmail));
        }

        if (verifiedRequestEmail.isPresent() && verifiedJwtEmail.isPresent()
            && !verifiedRequestEmail.get().getNormalizedAddress()
                .equals(verifiedJwtEmail.get().getNormalizedAddress())) {
            throw RestExceptionBuilder.newBuilder(AuthorizationIdentifyRestException.class)
                .withErrorCode(AuthorizationIdentifyRestException.EMAIL_MISMATCH)
                .addParameter("jwt_email", verifiedJwtEmail.get().getNormalizedAddress())
                .addParameter("email", verifiedRequestEmail.get().getNormalizedAddress())
                .build();
        }

        ConsumerRequestContext consumerRequestContext = getConsumerRequestContext(accessToken, emailData, dataFromJwt,
            verifiedRequestEmail.isPresent() || verifiedJwtEmail.isPresent());

        PersonAuthorization authorization = consumerRequestContext.getAuthorization();

        return getResultAuthorization(verifiedRequestEmail, verifiedJwtEmail, authorization);
    }

    private ConsumerRequestContext getConsumerRequestContext(String accessToken, Optional<EventData> emailData,
        List<EventData> dataFromJwt, boolean validEmailIsPresent) throws AuthorizationRestException {

        ConsumerRequestContextService.ConsumerRequestContextBuilder consumerRequestContextBuilder =
            consumerRequestContextService.createBuilder(servletRequest)
                .withEventProcessing(configurator -> {
                    for (EventData eventData : dataFromJwt) {
                        configurator.addData(eventData);
                    }

                    emailData.ifPresent(data -> configurator.addData(data));
                });

        if (accessToken != null && !validEmailIsPresent) {
            consumerRequestContextBuilder.withAccessToken(accessToken);
        }

        return consumerRequestContextBuilder.build();
    }

    private PersonAuthorization getResultAuthorization(Optional<Email> verifiedRequestEmail,
        Optional<Email> verifiedJwtEmail,
        PersonAuthorization authorization) {

        if (verifiedJwtEmail.isPresent()) {
            identifyPerson(authorization, verifiedJwtEmail.get());
            return authorization;
        }

        if (verifiedRequestEmail.isPresent()) {
            identifyPerson(authorization, verifiedRequestEmail.get());
            return authorization;
        }

        return authorization;
    }

    private void createLongLivedForSamePerson(PersonAuthorization authorization, String accessToken,
        String accessTokenFromCookie) throws AuthorizationRestException {
        LOG.debug("Successfully authorized short-lived token {}. "
            + "Ensuring we have a valid long-lived token in cookie referencing person {} "
            + "from the requested token", accessToken, authorization.getIdentityId());

        Authorization authorizationFromCookie = consumerRequestContextService.createBuilder(servletRequest)
            .withReplaceableAccessToken(accessTokenFromCookie)
            .build()
            .getAuthorization();

        if (!authorizationFromCookie.getIdentityId().equals(authorization.getIdentityId())) {
            Authorization longLivedAuthorizationForSamePerson;
            try {
                longLivedAuthorizationForSamePerson =
                    authorizationService.authorize(authorization, authorization.getIdentity());
            } catch (AuthorizationException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
            servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(),
                longLivedAuthorizationForSamePerson);
        }
    }

    @Override
    public TokenV4Response upgradeToken(String accessToken, UpgradeTokenV4Request request)
        throws AuthorizationRestException, UpgradeTokenV4RestException {
        return upgradeToken(accessToken, accessToken, request);
    }

    @Override
    public TokenV4Response upgradeToken(String accessToken, String requestedAccessToken,
        UpgradeTokenV4Request upgradeTokenV4Request) throws AuthorizationRestException, UpgradeTokenV4RestException {

        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(requestedAccessToken)
            .build();
        PersonAuthorization authorization = requestContext.getAuthorization();
        if (!authenticationEmailService.checkSecret(authorization, upgradeTokenV4Request.getExtoleSecret())) {
            throw RestExceptionBuilder.newBuilder(UpgradeTokenV4RestException.class)
                .withErrorCode(UpgradeTokenV4RestException.INVALID_SECRET)
                .build();
        }
        try {
            authorization = authorizationService.upgrade(
                backendAuthorizationProvider.getAuthorizationForBackend(authorization.getClientId()), authorization);
            return createTokenResponse(authorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public void deleteToken(String accessToken, String deleteAccessToken) throws AuthorizationRestException {
        deleteToken(deleteAccessToken);
    }

    @Override
    public void deleteToken(String accessToken) throws AuthorizationRestException {
        doDeleteToken(accessToken);
        for (String candidate : getAllAvailableCookieTokens()) {
            if (!candidate.equals(accessToken)) {
                doDeleteToken(candidate);
            }
        }
        servletRequest.removeAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
    }

    private PersonAuthorization createTokenBasedOnShortLived(PersonAuthorization shortLivedAuthorization,
        TokenV4Request request) throws ReissueTokenRestException {
        Set<Authorization.Scope> scopes = convertRestScopes(request.getScopes());
        try {
            return authorizationService.reissue(shortLivedAuthorization, scopes);
        } catch (AuthorizationScopesDeniedException e) {
            throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                .withErrorCode(ReissueTokenRestException.SCOPES_DENIED)
                .addParameter("denied_scopes", e.getDeniedScopes())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private PersonAuthorization createShortLived(PersonAuthorization authorization, TokenV4Request request)
        throws ReissueTokenRestException {
        Duration timeToLive = Duration.ofSeconds(request.getDurationSeconds());
        if (timeToLive.get(ChronoUnit.SECONDS) > TOKEN_MAXIMUM_TTL.get(ChronoUnit.SECONDS)) {
            throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                .withErrorCode(ReissueTokenRestException.DURATION_DENIED)
                .addParameter("maximum", TOKEN_MAXIMUM_TTL.get(ChronoUnit.SECONDS))
                .addParameter("duration", request.getDurationSeconds())
                .build();
        }

        Set<Authorization.Scope> scopes = convertRestScopes(request.getScopes());
        try {
            return authorizationService.reissue(authorization, scopes, timeToLive);
        } catch (AuthorizationScopesDeniedException e) {
            throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                .withErrorCode(ReissueTokenRestException.SCOPES_DENIED)
                .addParameter("denied_scopes", e.getDeniedScopes())
                .withCause(e)
                .build();
        } catch (AuthorizationException | InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void doDeleteToken(String accessToken) throws AuthorizationRestException {
        if (Strings.isNullOrEmpty(accessToken)) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_TOKEN_MISSING)
                .build();
        }

        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();

        authorizationService.invalidate(authorization);
    }

    private TokenV4Response createTokenResponse(PersonAuthorization authorization) {

        long expiresAt = authorization.getExpiresAt().getEpochSecond();
        long expiresInSeconds = expiresAt - Instant.now().getEpochSecond();
        return new TokenV4Response(authorization.getAccessToken(), expiresInSeconds,
            authorization.getScopes().stream().map(scope -> com.extole.consumer.rest.common.Scope.valueOf(scope.name()))
                .collect(Collectors.toSet()),
            Collections.singleton(UPDATE_PROFILE));
    }

    private boolean isConsumerAuthorization(Authorization authorization) {
        return authorization.getScopes().stream().allMatch(scope -> CONSUMER_SCOPES.contains(scope.name()));
    }

    private void ensureConsumerAuthorization(Authorization authorization) throws AuthorizationRestException {
        if (!isConsumerAuthorization(authorization)) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

    private Set<Authorization.Scope> convertRestScopes(Set<Scope> scopes) {
        return scopes != null
            ? scopes.stream().map(scope -> Authorization.Scope.valueOf(scope.name())).collect(Collectors.toSet())
            : Collections.emptySet();
    }

    private boolean isLongLived(PersonAuthorization authorization) {
        return Instant.now().plus(LONG_LIVED_TTL).isBefore(authorization.getExpiresAt());
    }

    @Nullable
    private String getFirstAccessTokenCookie() {
        Set<String> allAvailableTokens = getAllAvailableCookieTokens();
        return allAvailableTokens.isEmpty() ? null : allAvailableTokens.iterator().next();
    }

    private Set<String> getAllAvailableCookieTokens() {
        Set<String> accessTokens = Sets.newHashSet();
        Optional<String> cookieToken = getCookieValue(ExtoleCookieType.ACCESS_TOKEN.getCookieName());
        cookieToken.ifPresent(accessTokens::add);

        String fallbackEnabled = DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName();
        if (Boolean.TRUE.equals(servletRequest.getAttribute(fallbackEnabled))) {
            getCookieValue(ExtoleCookieType.DEPRECATED_ACCESS_TOKEN.getCookieName()).ifPresent(accessTokens::add);
        }

        return accessTokens;
    }

    private Optional<String> getCookieValue(String cookieName) {
        if (servletRequest.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(servletRequest.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst();
    }

    private PersonAuthorization createLongLivedToken() {
        PersonAuthorization authorization;
        try {
            authorization = consumerRequestContextService.createBuilder(servletRequest)
                .build()
                .getAuthorization();
        } catch (AuthorizationRestException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
        return authorization;
    }

    private TokenV4Response createToken(String requestedAccessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(requestedAccessToken)
            .build()
            .getAuthorization();
        ensureConsumerAuthorization(authorization);
        return createTokenResponse(authorization);
    }

    private Optional<EventData> getEmailFromRequestData(String email) {
        if (StringUtils.isEmpty(email)) {
            return Optional.empty();
        }
        return Optional.of(new EventData("email", email, REQUEST_BODY, false, true));
    }

    private List<EventData> getDataFromJwt(String jwt) throws ReissueTokenRestException {
        if (jwt == null) {
            return Collections.emptyList();
        }
        try {
            return validateJwtEventData(jwtDataExtractor.extract(getCurrentClientId(), jwt));
        } catch (JwtDataExtractor.JwtDataExtractionException e) {
            throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                .withErrorCode(ReissueTokenRestException.JWT_AUTHENTICATION_FAILED)
                .addParameter("reason", e.getReason())
                .addParameter("description", e.getDescription())
                .build();
        }
    }

    private List<EventData> validateJwtEventData(List<EventData> eventData)
        throws ReissueTokenRestException {
        Optional<EventData> email = eventData.stream().filter(data -> data.getName().equals("email")).findFirst();
        if (email.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                .withErrorCode(ReissueTokenRestException.JWT_AUTHENTICATION_FAILED)
                .addParameter("reason", JwtDataExtractor.Reason.ID_NOT_PROVIDED.name())
                .addParameter("description", JwtDataExtractor.Reason.ID_NOT_PROVIDED.getDescription())
                .build();
        } else {
            try {
                verifiedEmailService.verifyEmail(String.valueOf(email.get().getValue()));
            } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
                throw RestExceptionBuilder.newBuilder(ReissueTokenRestException.class)
                    .withErrorCode(ReissueTokenRestException.JWT_AUTHENTICATION_FAILED)
                    .addParameter("reason", JwtDataExtractor.Reason.ID_INVALID.name())
                    .addParameter("description", JwtDataExtractor.Reason.ID_INVALID.getDescription())
                    .build();
            }
        }

        return eventData;
    }

    private Authorization updateDuration(PersonAuthorization authorization, Duration timeToLive)
        throws AuthorizationDurationRestException {

        try {
            return authorizationService.updateExpiresAt(authorization, Instant.now().plus(timeToLive));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (InvalidExpiresAtException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationDurationRestException.class)
                .withErrorCode(AuthorizationDurationRestException.ACCESS_TOKEN_DURATION_INVALID)
                .withCause(e)
                .build();
        }
    }

    private Email validateEmail(String email) throws AuthorizationIdentifyRestException {
        try {
            return verifiedEmailService.verifyEmail(email).getEmail();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationIdentifyRestException.class)
                .withErrorCode(AuthorizationIdentifyRestException.EMAIL_INVALID)
                .addParameter("email", email)
                .withCause(e)
                .build();
        }
    }

    private PersonAuthorization handleLongLivedTokenRequest(@Nullable String accessToken, TokenV4Request tokenV4Request)
        throws AuthorizationRestException, ReissueTokenRestException {
        if (accessToken == null) {
            return createLongLivedToken();
        }
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken).build().getAuthorization();
        return isLongLived(authorization) ? authorization : createTokenBasedOnShortLived(authorization, tokenV4Request);
    }

    private PersonAuthorization handleShortLivedTokenRequest(@Nullable String accessToken,
        TokenV4Request tokenV4Request)
        throws ReissueTokenRestException {
        try {
            ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withReplaceableAccessToken(accessToken)
                .build();
            return createShortLived(requestContext.getAuthorization(), tokenV4Request);
        } catch (AuthorizationRestException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void identifyPerson(PersonAuthorization authorization, Email verifiedEmail) {
        try {
            personService.updatePerson(authorization, new LockDescription("person-jwt-identified"),
                (personBuilder, originalPersonProfile) -> {
                    try {
                        if (originalPersonProfile.getEmail() == null
                            && originalPersonProfile.getNormalizedEmail() == null) {
                            return personBuilder.withEmail(verifiedEmail).save();
                        } else {
                            return originalPersonProfile;
                        }
                    } catch (PersonEmailAlreadyDefinedException e) {
                        throw new LockClosureException(e);
                    }
                }, createConsumerEventSender());
        } catch (LockClosureException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private ConsumerEventSender createConsumerEventSender() {
        PublicProgram clientDomain = consumerRequestContextService.extractProgramDomain(servletRequest);
        if (clientDomain != null) {
            return consumerEventSenderService.createConsumerEventSender()
                .withClientDomainContext(
                    new ClientDomainContext(clientDomain.getProgramDomain().toString(), clientDomain.getId()));
        }
        return consumerEventSenderService.createConsumerEventSender();
    }

    private Id<ClientHandle> getCurrentClientId() {
        RequestContextAttributeName clientId = RequestContextAttributeName.CLIENT_ID;
        return Id.valueOf((String) servletRequest.getAttribute(clientId.getAttributeName()));
    }
}
