package com.extole.consumer.rest.impl.request.context;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization.Scope;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.AuthorizationExpiredException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.client.identity.IdentityKey;
import com.extole.common.jwt.decode.JwtParseException;
import com.extole.common.lang.JsonMap;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RequestBodySizeTooLargeRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.person.PersonAuthorizationProvider;
import com.extole.consumer.event.service.processor.EventProcessorBuilder;
import com.extole.consumer.event.service.processor.EventProcessorConfigurator;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.EventProcessorResult;
import com.extole.consumer.event.service.processor.EventProcessorService;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.ConsumerContextAttributeName;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.event.consumer.ConsumerEventDeviceId;
import com.extole.event.consumer.raw.RawEventBuildResult;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.person.service.identity.IdentityKeyValueNormalizer;
import com.extole.person.service.identity.InvalidIdentityKeyValueException;
import com.extole.person.service.identity.NormalizedIdentityKeyValue;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonService;
import com.extole.security.backend.BackendAuthorizationProvider;

@Component
class ConsumerRequestContextServiceImpl implements ConsumerRequestContextService {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerRequestContextServiceImpl.class);

    private static final String BROWSER_ID_PREFIX = "browser:";
    private static final String WEB_DEFAULT_APP_TYPE = "Web";
    private static final String CLAIM_SCOPE = "scope";

    private final PersonService personService;
    private final PersonAuthorizationService authorizationService;
    private final EventProcessorService eventProcessorService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final PersonAuthorizationProvider authorizationProvider;
    private final ConsumerRequestRawEventProducer consumerRequestRawEventProducer;
    private final ConsumerDisplacePrehandler consumerDisplacePrehandler;
    private final ClientCache clientCache;
    private final ConsumerRequestJwtAuthorizationExtractor consumerRequestJwtAuthorizationExtractor;
    private final IdentityKeyValueNormalizer identityKeyValueNormalizer;

    @Autowired
    ConsumerRequestContextServiceImpl(PersonService personService, PersonAuthorizationService authorizationService,
        EventProcessorService eventProcessorService,
        ConsumerRequestRawEventProducer consumerRequestRawEventProducer,
        BackendAuthorizationProvider backendAuthorizationProvider,
        PersonAuthorizationProvider authorizationProvider,
        ConsumerDisplacePrehandler consumerDisplacePrehandler,
        ClientCache clientCache,
        ConsumerRequestJwtAuthorizationExtractor consumerRequestJwtAuthorizationExtractor,
        IdentityKeyValueNormalizer identityKeyValueNormalizer) {
        this.personService = personService;
        this.authorizationService = authorizationService;
        this.eventProcessorService = eventProcessorService;
        this.consumerRequestRawEventProducer = consumerRequestRawEventProducer;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.authorizationProvider = authorizationProvider;
        this.consumerDisplacePrehandler = consumerDisplacePrehandler;
        this.clientCache = clientCache;
        this.consumerRequestJwtAuthorizationExtractor = consumerRequestJwtAuthorizationExtractor;
        this.identityKeyValueNormalizer = identityKeyValueNormalizer;
    }

    @Override
    public PublicProgram extractProgramDomain(HttpServletRequest servletRequest) {
        return (PublicProgram) servletRequest.getAttribute(ConsumerContextAttributeName.PROGRAM.getAttributeName());
    }

    @Override
    public ConsumerRequestContextBuilder createBuilder(HttpServletRequest httpServletRequest) {
        return new ConsumerRequestContextBuilderImpl(httpServletRequest);
    }

    private final class ConsumerRequestContextBuilderImpl implements ConsumerRequestContextBuilder {
        private final HttpServletRequest servletRequest;
        private final PublicProgram requestClientDomain;
        private final Id<ClientHandle> clientId;
        private ConsumerRequestType requestType = ConsumerRequestType.API;
        private HttpRequestBodyCapturingType requestBodyCapturingType = HttpRequestBodyCapturingType.FULL;
        private boolean authorizationReplaceable = true;
        private boolean expiredAccessTokenExceptionEnabled = false;
        private String eventName;
        private Optional<String> accessToken = Optional.empty();
        private Optional<HttpHeaders> httpHeaders = Optional.empty();
        private Optional<UriInfo> uriInfo = Optional.empty();
        private Optional<Consumer<EventProcessorConfigurator>> processorConfigurator = Optional.empty();

        // TODO ENG-10439 HttpServletRequest should not be passed directly
        ConsumerRequestContextBuilderImpl(HttpServletRequest servletRequest) {
            this.servletRequest = servletRequest;
            this.requestClientDomain = extractProgramDomain(servletRequest);
            this.clientId = this.requestClientDomain.getClientId();
        }

        @Override
        public ConsumerRequestContextBuilder withConsumerRequestType(ConsumerRequestType requestType) {
            if (requestType != null) {
                this.requestType = requestType;
            }
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder withReplaceableAccessTokenBasedOnCoreSettings(String accessToken) {
            try {
                if (Strings.isNullOrEmpty(accessToken)
                    || clientCache.getById(clientId).getCoreSettings().isAccessTokenIncludedInResponseEnabled()) {
                    return withReplaceableAccessToken(accessToken);
                } else {
                    expiredAccessTokenExceptionEnabled = true;
                    return withAccessToken(accessToken);
                }
            } catch (ClientNotFoundException e) {
                // should not happen
                return withReplaceableAccessToken(accessToken);
            }
        }

        @Override
        public ConsumerRequestContextBuilder withReplaceableAccessToken(String accessToken) {
            this.accessToken = Optional.ofNullable(Strings.emptyToNull(accessToken));
            this.authorizationReplaceable = true;
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder withAccessToken(@Nullable String accessToken) {
            this.accessToken = Optional.ofNullable(Strings.emptyToNull(accessToken));
            this.authorizationReplaceable = false;
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder withHttpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = Optional.ofNullable(httpHeaders);
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder withUriInfo(UriInfo uriInfo) {
            this.uriInfo = Optional.ofNullable(uriInfo);
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder
            withEventProcessing(Consumer<EventProcessorConfigurator> processorConfigurator) {
            this.processorConfigurator = Optional.of(processorConfigurator);
            return this;
        }

        @Override
        public ConsumerRequestContextBuilder
            withHttpRequestBodyCapturing(HttpRequestBodyCapturingType requestBodyCapturingType) {
            Preconditions.checkNotNull(requestBodyCapturingType, "Body capturing cannot be null");
            this.requestBodyCapturingType = requestBodyCapturingType;
            return this;
        }

        @Override
        public ConsumerRequestContext build() throws AuthorizationRestException {
            Stopwatch stopwatch = Stopwatch.createStarted();
            PersonAuthorization requestAuthorization = getAuthorizationOrCreateNew(clientId, accessToken.orElse(null));

            boolean isWebRequestType = requestType == ConsumerRequestType.WEB;
            boolean isTrimNeeded = isWebRequestType && requestBodyCapturingType == HttpRequestBodyCapturingType.FULL;
            requestBodyCapturingType = isTrimNeeded ? HttpRequestBodyCapturingType.LIMITED : requestBodyCapturingType;

            RawEventBuildResult rawEventBuildResult;
            try {
                rawEventBuildResult = consumerRequestRawEventProducer.buildRawEvent(servletRequest, uriInfo,
                    httpHeaders, requestClientDomain, eventName, requestBodyCapturingType);
            } catch (RequestBodySizeTooLargeException e) {
                throw RestExceptionBuilder.newBuilder(RequestBodySizeTooLargeRestRuntimeException.class)
                    .withErrorCode(RequestBodySizeTooLargeRestRuntimeException.REQUEST_BODY_TOO_LARGE)
                    .addParameter("body_size", e.getBodySize())
                    .addParameter("max_allowed_body_size", e.getMaxAllowedBodySize())
                    .withCause(e)
                    .build();
            }

            EventProcessorBuilder processorBuilder =
                eventProcessorService.create(rawEventBuildResult.getRawEvent()).withCandidateProvider(
                    (processedRawEvent, logger) -> Optional.of(requestAuthorization.getIdentity()));
            processorBuilder.addLogMessages(rawEventBuildResult.getLogMessages());

            if (isWebRequestType) {
                processorBuilder.withDefaultAppType(WEB_DEFAULT_APP_TYPE);
                processorBuilder.addPrehandler(consumerDisplacePrehandler);
            }

            processorConfigurator.ifPresent(configurator -> configurator.accept(processorBuilder));

            try {
                EventProcessorResult processorResult = processorBuilder.build().process();

                ProcessedRawEvent processedRawEvent = processorResult.getProcessedRawEvent();
                PersonAuthorization authorization = invalidateAuthorizationIfRequiredAndCreateNew(requestAuthorization,
                    processorResult.getPerson().get());
                authorization = upgradeAuthorizationIfRequired(authorization, processedRawEvent);

                servletRequest.setAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName(),
                    authorization);
                servletRequest.setAttribute(ConsumerContextAttributeName.BROWSER_ID.getAttributeName(),
                    extractBrowserId(processedRawEvent.getDeviceId()));

                stopwatch.stop();
                List<String> performanceLogMessages = List.of(
                    "Raw processing duration (ms): " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

                return new ConsumerRequestContextImpl(authorization, processedRawEvent, performanceLogMessages);
            } catch (AuthorizationException e) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .withCause(e).build();
            } catch (EventProcessorException e) {
                FatalRestRuntimeException exception = RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e).build();
                LOG.error("Unexpected error: {} during raw event: {} processing", exception.getUniqueId(),
                    rawEventBuildResult.getRawEvent().getId());
                throw exception;
            }
        }

        private PersonAuthorization invalidateAuthorizationIfRequiredAndCreateNew(
            PersonAuthorization requestAuthorization, Person person) throws AuthorizationException {
            if (requestAuthorization.getIdentityId().equals(person.getId())) {
                return requestAuthorization;
            }
            if (personService.getPerson(requestAuthorization).getId().equals(person.getIdentityId())) {
                return requestAuthorization;
            }
            PersonAuthorization authorization = authorizationService.authorize(requestAuthorization, person);
            authorizationService.invalidate(requestAuthorization);
            return authorization;
        }

        private PersonAuthorization upgradeAuthorizationIfRequired(PersonAuthorization authorization,
            ProcessedRawEvent processedRawEvent) {
            if (authorization.getScopes().contains(Scope.VERIFIED_CONSUMER)
                || !isEligibleVerifiedConsumer(authorization, processedRawEvent)) {
                return authorization;
            }
            try {
                return authorizationService.authorizeVerifiedConsumer(
                    backendAuthorizationProvider.getAuthorizationForBackend(authorization.getClientId()),
                    authorization.getIdentity());
            } catch (AuthorizationException e) {
                LOG.error("Error while issuing new verified consumer authorization to person: {} for client: {}",
                    authorization.getIdentityId(), authorization.getClientId(), e);
                return authorization;
            }
        }

        private boolean isEligibleVerifiedConsumer(PersonAuthorization authorization, ProcessedRawEvent event) {
            JsonMap verifiedData = JsonMap.valueOf(event.getVerifiedData());
            Person person = authorization.getIdentity();
            String identityKeyValue = verifiedData.getValueAsString(person.getIdentityKey().getName()).orElse(null);

            if (Strings.isNullOrEmpty(identityKeyValue)) {
                return false;
            }

            if (Scope.VERIFIED_CONSUMER.name().equals(verifiedData.get(CLAIM_SCOPE))) {
                return true;
            }

            LOG.info("Is eligible consumer check using deprecated way for identity_key_value {}", identityKeyValue);
            return isEligibleVerifiedConsumerLegacyWay(person, identityKeyValue);
        }

        private boolean isEligibleVerifiedConsumerLegacyWay(Person person, String identityKeyValue) {
            try {
                NormalizedIdentityKeyValue normalizedIdentityKeyValue =
                    normalize(person.getIdentityKey(), identityKeyValue);
                // TODO ENG-12845 here we don't know yet if the person's identity key value will be updated successfully
                return person.getIdentityKeyValue().stream()
                    .allMatch(normalizedIdentityKeyValue.getNormalizedValue()::equals);
            } catch (InvalidIdentityKeyValueException e) {
                LOG.debug("Invalid identity key value: {} while checking if authorization is eligible for upgrade",
                    identityKeyValue, e);
                return false;
            }
        }

        private PersonAuthorization getAuthorizationOrCreateNew(Id<ClientHandle> clientId, @Nullable String accessToken)
            throws AuthorizationRestException {
            try {
                return getAuthorization(accessToken);
            } catch (AuthorizationRestException restException) {
                if (authorizationReplaceable) {
                    try {
                        return authorizationService.authorize(clientId);
                    } catch (AuthorizationException e) {
                        throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                            .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                            .withCause(e).build();
                    }
                }
                throw restException;
            }
        }

        private PersonAuthorization getAuthorization(@Nullable String accessToken) throws AuthorizationRestException {
            try {
                if (Strings.isNullOrEmpty(accessToken)) {
                    throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                        .withErrorCode(AuthorizationRestException.ACCESS_TOKEN_MISSING)
                        .build();
                }
                return authorizationProvider.getPersonAuthorization(accessToken);
            } catch (UserAuthorizationRestException e) {
                if (expiredAccessTokenExceptionEnabled && e.getCause() instanceof AuthorizationExpiredException) {
                    throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                        .withErrorCode(AuthorizationRestException.ACCESS_TOKEN_EXPIRED)
                        .withCause(e)
                        .build();
                }
                return generateAuthorizationFromJwt(accessToken);
            }
        }

        private PersonAuthorization generateAuthorizationFromJwt(String accessToken)
            throws AuthorizationRestException {
            try {
                return consumerRequestJwtAuthorizationExtractor.generateAuthorizationFromJwt(clientId, accessToken);
            } catch (JwtParseException e) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_TOKEN_INVALID)
                    .withCause(e)
                    .build();
            } catch (JwtAuthorizationExtractionException e) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.JWT_AUTHENTICATION_FAILED)
                    .addParameter("reason", e.getReason())
                    .addParameter("description", e.getDescription())
                    .withCause(e)
                    .build();
            }
        }
    }

    private NormalizedIdentityKeyValue normalize(IdentityKey identityKey, String identityKeyValue)
        throws InvalidIdentityKeyValueException {
        return identityKeyValueNormalizer.normalize(identityKey, identityKeyValue);
    }

    private static Long extractBrowserId(ConsumerEventDeviceId deviceId) {
        return Long.valueOf(deviceId.getId().replace(BROWSER_ID_PREFIX, ""));
    }

}
