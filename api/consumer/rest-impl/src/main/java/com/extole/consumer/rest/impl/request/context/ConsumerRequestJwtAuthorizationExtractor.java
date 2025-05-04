package com.extole.consumer.rest.impl.request.context;

import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.ALGORITHM_MISSING;
import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.KEY_ID_MISMATCH;
import static com.extole.model.service.jwt.JwtKeySupplyFailureReason.KEY_ID_MISSING;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.JwtDecoder;
import com.extole.common.jwt.decode.JwtParseException;
import com.extole.id.Id;
import com.extole.key.provider.service.KeyProviderException;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.client.security.key.ClientKeyNotFoundException;
import com.extole.model.service.jwt.JwtAlgorithm;
import com.extole.model.service.jwt.JwtClientKeyConversionRuntimeException;
import com.extole.model.service.jwt.JwtKeySupplyException;
import com.extole.model.service.jwt.JwtService;
import com.extole.model.service.jwt.JwtVerificationException;
import com.extole.model.service.jwt.VerifiedJwt;
import com.extole.model.shared.client.security.key.jwt.JwtClientKeyCache;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.security.backend.BackendAuthorization;
import com.extole.security.backend.BackendAuthorizationProvider;

@Component
final class ConsumerRequestJwtAuthorizationExtractor {

    private static final String JWT_SCOPES_CLAIM = "scopes";
    private static final String JWT_PERSON_ID_CLAIM = "person_id";

    private final JwtService jwtService;
    private final JwtClientKeyCache jwtClientKeyCache;
    private final KeyProviderService keyProviderService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final PersonService personService;
    private final PersonAuthorizationService authorizationService;

    @Autowired
    ConsumerRequestJwtAuthorizationExtractor(JwtService jwtService,
        JwtClientKeyCache jwtClientKeyCache,
        KeyProviderService keyProviderService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        PersonService personService,
        PersonAuthorizationService authorizationService) {
        this.jwtService = jwtService;
        this.jwtClientKeyCache = jwtClientKeyCache;
        this.keyProviderService = keyProviderService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.personService = personService;
        this.authorizationService = authorizationService;
    }

    PersonAuthorization generateAuthorizationFromJwt(Id<ClientHandle> clientId, String jwt)
        throws JwtParseException, JwtAuthorizationExtractionException {
        validateIsJwt(jwt);

        VerifiedJwt verifiedJwt = verifyJwt(jwt, clientId);
        Map<String, Object> jwtClaims = verifiedJwt.getBody();

        Object personId = jwtClaims.get(JWT_PERSON_ID_CLAIM);
        if (personId == null) {
            throw new JwtAuthorizationExtractionException(Reason.PERSON_ID_MISSING.name(),
                Reason.PERSON_ID_MISSING.getDescription());
        }

        boolean verifiedConsumerScopeRequested = jwtClaims.get(JWT_SCOPES_CLAIM) != null
            && jwtClaims.get(JWT_SCOPES_CLAIM).toString().toLowerCase()
                .contains(Authorization.Scope.VERIFIED_CONSUMER.name().toLowerCase());
        return createPersonAuthorization(clientId, personId.toString(), verifiedConsumerScopeRequested);
    }

    private void validateIsJwt(String jwt) throws JwtParseException, JwtAuthorizationExtractionException {
        try {
            JwtDecoder.newDecoder(jwt).decodeUnchecked();
        } catch (UnsupportedAlgorithmException e) {
            throw new JwtAuthorizationExtractionException(Reason.UNSUPPORTED_JWT_ENCODING.name(),
                Reason.UNSUPPORTED_JWT_ENCODING.getDescription(), e);
        }
    }

    private VerifiedJwt verifyJwt(String jwt, Id<ClientHandle> clientId)
        throws JwtAuthorizationExtractionException {
        try {
            return jwtService.verifyJwt(jwt, createKeySupplier(clientId));
        } catch (JwtVerificationException e) {
            throw new JwtAuthorizationExtractionException(e.getReason().name(), e.getReason().getDescription(), e);
        } catch (JwtKeySupplyException e) {
            throw new JwtAuthorizationExtractionException(e.getReason().name(), e.getReason().getDescription(), e);
        }
    }

    private PersonAuthorization createPersonAuthorization(Id<ClientHandle> clientId, String personId,
        boolean verifiedConsumerScopeRequested) throws JwtAuthorizationExtractionException {
        try {
            BackendAuthorization backendAuthorization = getBackendAuthorization(clientId);
            Person person = personService.getPerson(backendAuthorization, Id.valueOf(personId));

            PersonAuthorization personAuthorization =
                authorizationService.authorize(backendAuthorization, person);
            if (verifiedConsumerScopeRequested) {
                personAuthorization = authorizationService.upgrade(backendAuthorization, personAuthorization);
            }
            return personAuthorization;
        } catch (AuthorizationException e) {
            throw new ConsumerRequestJwtAuthorizationExtractorRuntimeException("Coould not create person authorization",
                e);
        } catch (PersonNotFoundException e) {
            throw new JwtAuthorizationExtractionException(Reason.PERSON_NOT_FOUND.name(),
                Reason.PERSON_NOT_FOUND.getDescription(), e);
        }
    }

    private JwtService.KeySupplier createKeySupplier(Id<ClientHandle> clientId) {
        return (partnerKeyId, algorithm) -> {
            if (partnerKeyId == null) {
                throw new JwtKeySupplyException(KEY_ID_MISSING);
            }
            BackendAuthorization backendAuthorization = getBackendAuthorization(clientId);

            try {
                ClientKey jwtClientKey =
                    jwtClientKeyCache.getJwtClientKeyByPartnerKeyId(backendAuthorization.getClientId(), partnerKeyId);

                if (algorithm == null) {
                    throw new JwtKeySupplyException(ALGORITHM_MISSING);
                }
                JwtService.KeySupplier.validateKeyAlgorithm(jwtClientKey.getAlgorithm().name(), algorithm);

                return JwtAlgorithm.valueOf(jwtClientKey.getAlgorithm().name())
                    .convert(keyProviderService.getKey(jwtClientKey).getBytes(StandardCharsets.ISO_8859_1));
            } catch (ClientKeyNotFoundException e) {
                throw new JwtKeySupplyException(KEY_ID_MISMATCH, e);
            } catch (InvalidKeyException | KeyProviderException e) {
                throw new JwtClientKeyConversionRuntimeException(e);
            }
        };
    }

    private BackendAuthorization getBackendAuthorization(Id<ClientHandle> clientId) {
        try {
            return backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            throw new ConsumerRequestJwtAuthorizationExtractorRuntimeException(
                "Could not create backend authorization for client: " + clientId, e);
        }
    }

    private enum Reason {
        PERSON_ID_MISSING("Jwt claim person_id is missing"),
        PERSON_NOT_FOUND("Could not find person by jwt claim person_id"),
        UNSUPPORTED_JWT_ENCODING("Jwt is encoded with an algorithm we do not support");

        private final String description;

        Reason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final class ConsumerRequestJwtAuthorizationExtractorRuntimeException extends RuntimeException {
        private ConsumerRequestJwtAuthorizationExtractorRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
