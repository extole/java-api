package com.extole.consumer.rest.impl.authorization;

import static com.extole.consumer.event.service.processor.EventData.Source.REQUEST_BODY;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.InvalidExpiresAtException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.client.identity.IdentityKey;
import com.extole.common.email.Email;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.JwtDataExtractor;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.rest.authorization.AuthorizationDurationRestException;
import com.extole.consumer.rest.authorization.AuthorizationEndpoints;
import com.extole.consumer.rest.authorization.AuthorizationVerificationJwtRestException;
import com.extole.consumer.rest.authorization.CreateTokenRequest;
import com.extole.consumer.rest.authorization.TokenResponse;
import com.extole.consumer.rest.common.AuthorizationIdentifyRestException;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.Scope;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.shared.client.ClientCache;
import com.extole.person.service.identity.InvalidIdentityKeyValueException;
import com.extole.person.service.profile.IdentityKeyValueAlreadyTakenException;
import com.extole.person.service.profile.IdentityKeyValueUnauthorizedUpdateException;
import com.extole.person.service.profile.PersonService;

@Provider
public class AuthorizationEndpointsImpl implements AuthorizationEndpoints {
    private static final Set<String> CONSUMER_SCOPES = Arrays.stream(Scope.values())
        .map(Enum::name).collect(Collectors.toSet());

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonAuthorizationService authorizationService;
    private final VerifiedEmailService verifiedEmailService;
    private final JwtDataExtractor jwtDataExtractor;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final ClientCache clientCache;

    @Inject
    public AuthorizationEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        JwtDataExtractor jwtDataExtractor,
        PersonAuthorizationService authorizationService,
        ConsumerEventSenderService consumerEventSenderService,
        PersonService personService,
        VerifiedEmailService verifiedEmailService,
        ClientCache clientCache) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.jwtDataExtractor = jwtDataExtractor;
        this.authorizationService = authorizationService;
        this.verifiedEmailService = verifiedEmailService;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.clientCache = clientCache;
    }

    @Override
    public TokenResponse getTokenDetails(String accessToken) throws AuthorizationRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build();
        ensureConsumerAuthorization(requestContext.getAuthorization());
        return toTokenResponse(requestContext.getAuthorization());
    }

    @Override
    public TokenResponse createToken(Optional<CreateTokenRequest> tokenRequest)
        throws AuthorizationVerificationJwtRestException, AuthorizationIdentifyRestException,
        AuthorizationDurationRestException {

        CreateTokenRequest request = tokenRequest.orElseGet(() -> new CreateTokenRequest(null, null, null));
        try {
            PersonAuthorization authorization;
            IdentityKey currentIdentityKey = getCurrentIdentityKey();
            if (IdentityKey.EMAIL_IDENTITY_KEY.equals(currentIdentityKey)) {
                authorization = getAuthorizationFromConsumerRequestContext(request);
            } else {
                if (StringUtils.isNotEmpty(request.getEmail())) {
                    throw RestExceptionBuilder.newBuilder(AuthorizationIdentifyRestException.class)
                        .withErrorCode(AuthorizationIdentifyRestException.EMAIL_NOT_APPLICABLE)
                        .addParameter("identity_key", currentIdentityKey.getName())
                        .build();
                }
                authorization = createJwtBasedAuthorization(currentIdentityKey, request.getJwt());
            }

            if (request.getDurationSeconds() != null) {
                Duration timeToLive = Duration.ofSeconds(request.getDurationSeconds().longValue());
                authorization = updateDuration(authorization, timeToLive);
            }
            return toTokenResponse(authorization);
        } catch (AuthorizationVerificationJwtRestException | AuthorizationIdentifyRestException
            | AuthorizationDurationRestException | RuntimeException e) {
            servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(), null);
            throw e;
        }
    }

    private PersonAuthorization getAuthorizationFromConsumerRequestContext(CreateTokenRequest tokenRequest)
        throws AuthorizationVerificationJwtRestException, AuthorizationIdentifyRestException {
        try {
            String jwt = tokenRequest.getJwt();
            List<EventData> dataFromJwt = getDataFromJwt(jwt);
            Optional<EventData> jwtEmailData = dataFromJwt.stream()
                .filter(data -> data.getName().equals("email"))
                .findFirst();
            Optional<EventData> emailData = getEmailFromRequestData(tokenRequest.getEmail());

            if (emailData.isPresent() && jwtEmailData.isPresent()) {
                String validatedEmail = validateEmail(emailData.get().getValue().toString()).getNormalizedAddress();
                String validatedJwtEmail =
                    validateJwtEmail(jwtEmailData.get().getValue().toString()).getNormalizedAddress();
                if (!validatedEmail.equals(validatedJwtEmail)) {
                    throw RestExceptionBuilder.newBuilder(AuthorizationIdentifyRestException.class)
                        .withErrorCode(AuthorizationIdentifyRestException.EMAIL_MISMATCH)
                        .addParameter("jwt_email", jwtEmailData.get().getValue())
                        .addParameter("email", emailData.get().getValue())
                        .build();
                }
            }

            ConsumerRequestContext consumerRequestContext = consumerRequestContextService
                .createBuilder(servletRequest)
                .withEventProcessing(configurator -> {
                    configurator.addJwt(jwt, REQUEST_BODY);
                    emailData.ifPresent(data -> configurator.addData(data));
                })
                .build();

            PersonAuthorization authorization = consumerRequestContext.getAuthorization();

            Optional<Email> validatedEmail = Optional.empty();
            if (consumerRequestContext.getProcessedRawEvent().getAllData().containsKey("email")) {
                List<EventData> allEmailData = consumerRequestContext.getProcessedRawEvent()
                    .getAllData()
                    .get("email");
                validatedEmail = processEmails(allEmailData);
            }

            if (StringUtils.isNotEmpty(jwt) && validatedEmail.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(AuthorizationVerificationJwtRestException.class)
                    .withErrorCode(AuthorizationVerificationJwtRestException.JWT_AUTHENTICATION_VERIFICATION_FAILED)
                    .addParameter("reason", JwtDataExtractor.Reason.ID_NOT_PROVIDED.name())
                    .addParameter("description", JwtDataExtractor.Reason.ID_NOT_PROVIDED.getDescription())
                    .build();
            }

            if (validatedEmail.isPresent()) {
                PublicProgram clientDomain = consumerRequestContext.getProcessedRawEvent().getClientDomain();
                identifyPerson(authorization, validatedEmail.get().getNormalizedAddress(), clientDomain);
            }
            return authorization;
        } catch (AuthorizationRestException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private PersonAuthorization createJwtBasedAuthorization(IdentityKey currentIdentityKey, String jwt)
        throws AuthorizationVerificationJwtRestException, AuthorizationIdentifyRestException {
        try {
            validateJwt(jwt);
            ConsumerRequestContext consumerRequestContext = consumerRequestContextService
                .createBuilder(servletRequest)
                .withEventProcessing(configurator -> configurator.addJwt(jwt, REQUEST_BODY))
                .build();

            PersonAuthorization authorization = consumerRequestContext.getAuthorization();

            ProcessedRawEvent processedRawEvent = consumerRequestContext.getProcessedRawEvent();
            Object identityKeyValue = KeyCaseInsensitiveMap.create(processedRawEvent.getData())
                .get(currentIdentityKey.getName());

            if (identityKeyValue == null && StringUtils.isNotEmpty(jwt)) {
                throw RestExceptionBuilder.newBuilder(AuthorizationVerificationJwtRestException.class)
                    .withErrorCode(AuthorizationVerificationJwtRestException.JWT_AUTHENTICATION_VERIFICATION_FAILED)
                    .addParameter("reason", JwtDataExtractor.Reason.ID_NOT_PROVIDED.name())
                    .addParameter("description", JwtDataExtractor.Reason.ID_NOT_PROVIDED.getDescription())
                    .build();
            }

            if (identityKeyValue != null) {
                PublicProgram clientDomain = processedRawEvent.getClientDomain();
                identifyPerson(authorization, identityKeyValue, clientDomain);
            }
            return authorization;
        } catch (AuthorizationRestException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void validateJwt(String jwt) throws AuthorizationVerificationJwtRestException {
        if (StringUtils.isEmpty(jwt)) {
            return;
        }
        List<EventData> ignored = getDataFromJwt(jwt);
    }

    private static Optional<EventData> getEmailFromRequestData(String email) {
        if (StringUtils.isEmpty(email)) {
            return Optional.empty();
        }
        return Optional.of(new EventData("email", email, REQUEST_BODY, false, true));
    }

    private Id<ClientHandle> getCurrentClientId() {
        RequestContextAttributeName clientId = RequestContextAttributeName.CLIENT_ID;
        return Id.valueOf((String) servletRequest.getAttribute(clientId.getAttributeName()));
    }

    private List<EventData> getDataFromJwt(String jwt) throws AuthorizationVerificationJwtRestException {
        if (StringUtils.isEmpty(jwt)) {
            return Collections.emptyList();
        }
        try {
            return jwtDataExtractor.extract(getCurrentClientId(), jwt);
        } catch (JwtDataExtractor.JwtDataExpiredException e) {
            return e.getExtractedData();
        } catch (JwtDataExtractor.JwtDataExtractionException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationVerificationJwtRestException.class)
                .withErrorCode(AuthorizationVerificationJwtRestException.JWT_AUTHENTICATION_VERIFICATION_FAILED)
                .addParameter("reason", e.getReason())
                .addParameter("description", e.getDescription())
                .withCause(e)
                .build();
        }
    }

    private Email validateJwtEmail(String email) throws AuthorizationVerificationJwtRestException {
        try {
            return verifiedEmailService.verifyEmail(email).getEmail();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationVerificationJwtRestException.class)
                .withErrorCode(AuthorizationVerificationJwtRestException.JWT_AUTHENTICATION_VERIFICATION_FAILED)
                .addParameter("reason", JwtDataExtractor.Reason.ID_INVALID.name())
                .addParameter("description", JwtDataExtractor.Reason.ID_INVALID.getDescription())
                .withCause(e)
                .build();
        }
    }

    private PersonAuthorization updateDuration(PersonAuthorization authorization, Duration timeToLive)
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

    @Override
    public SuccessResponse deleteToken(String accessToken) throws AuthorizationRestException {
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
        return SuccessResponse.SUCCESS;
    }

    private static TokenResponse toTokenResponse(PersonAuthorization authorization) {
        long expiresInSeconds = authorization.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
        return new TokenResponse(authorization.getAccessToken(), expiresInSeconds,
            authorization.getScopes().stream().map(scope -> Scope.valueOf(scope.name()))
                .collect(Collectors.toSet()));
    }

    private static boolean isConsumerAuthorization(Authorization authorization) {
        return authorization.getScopes().stream().allMatch(scope -> CONSUMER_SCOPES.contains(scope.name()));
    }

    private static void ensureConsumerAuthorization(Authorization authorization) throws AuthorizationRestException {
        if (!isConsumerAuthorization(authorization)) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
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

    private Optional<Email> processEmails(List<EventData> emails)
        throws AuthorizationVerificationJwtRestException, AuthorizationIdentifyRestException {
        List<EventData> sortedEmails = emails.stream()
            .sorted(Comparator.comparingInt(value -> value.getSource().getOrder()))
            .collect(Collectors.toList());
        for (EventData email : sortedEmails) {
            return Optional.of(
                email.getSource() == EventData.Source.JWT ? validateJwtEmail(email.getValue().toString())
                    : validateEmail(email.getValue().toString()));
        }
        return Optional.empty();
    }

    private void identifyPerson(PersonAuthorization authorization, Object identityKeyValue,
        PublicProgram clientDomain) throws AuthorizationVerificationJwtRestException {
        try {
            personService.updatePerson(authorization, new LockDescription("person-jwt-v5-identified"),
                (personBuilder, beforeUpdate) -> {
                    try {
                        if (beforeUpdate.getIdentityKeyValue().isEmpty()) {
                            personBuilder.withIdentityKeyValue(identityKeyValue);
                            return personBuilder.save();
                        }
                        return beforeUpdate;
                    } catch (InvalidIdentityKeyValueException | IdentityKeyValueUnauthorizedUpdateException
                        | IdentityKeyValueAlreadyTakenException e) {
                        throw new LockClosureException("This is not supposed to ever happen", e);
                    }
                }, consumerEventSenderService.createConsumerEventSender().withClientDomainContext(
                    new ClientDomainContext(clientDomain.getProgramDomain().toString(), clientDomain.getId())));
        } catch (LockClosureException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationVerificationJwtRestException.class)
                .withErrorCode(AuthorizationVerificationJwtRestException.JWT_AUTHENTICATION_VERIFICATION_FAILED)
                .addParameter("reason", JwtDataExtractor.Reason.ID_INVALID.name())
                .addParameter("description", JwtDataExtractor.Reason.ID_INVALID.getDescription())
                .withCause(e)
                .build();
        }
    }

    private IdentityKey getCurrentIdentityKey() {
        try {
            return clientCache.getById(getCurrentClientId()).getIdentityKey();
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
