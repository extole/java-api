package com.extole.api.impl.service;

import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.A128KW;
import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.A192KW;
import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.A256KW;
import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.RSA_OAEP_256_PUBLIC;
import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.RSA_OAEP_384_PUBLIC;
import static com.extole.model.entity.client.security.key.ClientKey.Algorithm.RSA_OAEP_512_PUBLIC;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import com.extole.api.service.JwtBuilder;
import com.extole.api.service.JwtBuilderException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptionMethod;
import com.extole.common.jwt.decode.Header;
import com.extole.common.jwt.decode.Payload;
import com.extole.common.jwt.encode.SecuredJwtEncoder;
import com.extole.common.jwt.encode.SecuredJwtEncoderProvider;
import com.extole.id.Id;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.jwt.JwtAlgorithm;
import com.extole.model.shared.client.security.key.jwt.JwtClientKeyCache;

public class JwtBuilderImpl implements JwtBuilder {

    private static final Set<ClientKey.Algorithm> JWE_SYMMETRIC_ALGORITHMS = Set.of(A128KW, A192KW, A256KW);
    private static final Set<ClientKey.Algorithm> JWE_ASYMMETRIC_ALGORITHMS =
        Set.of(RSA_OAEP_256_PUBLIC, RSA_OAEP_384_PUBLIC, RSA_OAEP_512_PUBLIC);
    private static final Set<ClientKey.Algorithm> JWE_SUPPORTED_ALGORITHMS = ImmutableSet.<ClientKey.Algorithm>builder()
        .addAll(JWE_SYMMETRIC_ALGORITHMS)
        .addAll(JWE_ASYMMETRIC_ALGORITHMS)
        .build();

    private static final EncryptionMethod DEFAULT_ENCRYPTION_METHOD = EncryptionMethod.A128CBC_HS256;
    private static final Map<String, EncryptionMethod> ENCRYPTION_METHODS =
        EncryptionMethod.SUPPORTED_ENCRYPTION_METHODS.stream()
            .collect(Collectors.toMap(method -> method.getName().toUpperCase(), Function.identity()));

    private ClientKey key;
    private EncryptionMethod encryptionMethod = DEFAULT_ENCRYPTION_METHOD;
    private final Map<String, Object> headers = new HashMap<>();
    private final Map<String, Object> claims = new HashMap<>();

    private final JwtClientKeyCache jwtClientKeyCache;
    private final KeyProviderService keyProviderService;
    private final Id<ClientHandle> clientId;

    public JwtBuilderImpl(Id<ClientHandle> clientId, JwtClientKeyCache jwtClientKeyCache,
        KeyProviderService keyProviderService) {
        this.jwtClientKeyCache = jwtClientKeyCache;
        this.keyProviderService = keyProviderService;
        this.clientId = clientId;
    }

    @Override
    public JwtBuilder withHeaderParameter(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public JwtBuilder withCustomClaim(String name, Object value) {
        claims.put(name, value);
        return this;
    }

    @Override
    public JwtBuilder withIssuer(String issuer) {
        claims.put(Payload.ISSUER, issuer);
        return this;
    }

    @Override
    public JwtBuilder withAudiences(List<String> audienceList) {
        claims.put(Payload.AUDIENCE, audienceList);
        return this;
    }

    @Override
    public JwtBuilder withAudience(String audience) {
        claims.put(Payload.AUDIENCE, audience);
        return this;
    }

    @Override
    public JwtBuilder withExpirationDate(Long secondsSince1970) {
        claims.put(Payload.EXPIRATION_TIME, secondsSince1970);
        return this;
    }

    @Override
    public JwtBuilder withSubject(String subject) {
        claims.put(Payload.SUBJECT, subject);
        return this;
    }

    @Override
    public JwtBuilder withNotBefore(Long secondsSince1970) {
        claims.put(Payload.NOT_BEFORE, secondsSince1970);
        return this;
    }

    @Override
    public JwtBuilder withIssuedAt(Long secondsSince1970) {
        claims.put(Payload.ISSUED_AT, secondsSince1970);
        return this;
    }

    @Override
    public JwtBuilder withJwtId(String jwtId) {
        claims.put(Payload.JWT_ID, jwtId);
        return this;
    }

    @Override
    public JwtBuilder signWithKeyByClientKeyId(String clientKeyId) throws JwtBuilderException {
        return withClientKeyId(clientKeyId);
    }

    @Override
    public JwtBuilder signWithKeyByPartnerKeyId(String partnerKeyId) throws JwtBuilderException {
        try {
            this.key = jwtClientKeyCache.getJwtClientKeyByPartnerKeyId(clientId, partnerKeyId);
        } catch (Exception e) {
            throw new JwtBuilderException(
                String.format("Unable to find client key for clientId=%s with [partnerKeyId=%s]", clientId,
                    partnerKeyId),
                e);
        }
        return this;
    }

    @Override
    public JwtBuilder withClientKeyId(String clientKeyId) throws JwtBuilderException {
        this.key = jwtClientKeyCache.list(clientId)
            .stream()
            .filter(candidate -> candidate.getId().getValue().equals(clientKeyId))
            .findFirst()
            .orElseThrow(() -> new JwtBuilderException(
                String.format("Unable to find client key for clientId=%s with [clientKeyId=%s]", clientId,
                    clientKeyId)));
        return this;
    }

    @Override
    public JwtBuilder withEncryptionMethod(String encryptionMethodString) throws JwtBuilderException {
        this.encryptionMethod = Optional.ofNullable(encryptionMethodString)
            .filter(method -> StringUtils.isNotBlank(method))
            .map(method -> ENCRYPTION_METHODS.get(method))
            .orElseThrow(() -> new JwtBuilderException(
                "Encryption method: \"" + encryptionMethodString + "\" is not supported. Supported encryption methods: "
                    + EncryptionMethod.SUPPORTED_ENCRYPTION_METHODS + ". ClientId: " + clientId));
        return this;
    }

    @Override
    public String build() throws JwtBuilderException {
        if (key == null) {
            return createUnsecuredJWT();
        }

        if (JWE_SUPPORTED_ALGORITHMS.contains(key.getAlgorithm())) {
            return createEncryptedJWT();
        }

        return createSignedJWT();
    }

    private String createSignedJWT() throws JwtBuilderException {
        try {
            Key signingKey = JwtAlgorithm.valueOf(key.getAlgorithm().name())
                .convert(keyProviderService.getKey(key).getBytes(StandardCharsets.ISO_8859_1));
            Algorithm algorithm = Algorithm.parse(key.getAlgorithm().name());
            return com.extole.common.jwt.encode.JwtBuilder.newEncoder()
                .withClaims(claims)
                .withHeaders(headers)
                .withSecuredEncoder(
                    SecuredJwtEncoderProvider.createSigningEncoder(algorithm).withKey(signingKey).build())
                .encode();
        } catch (Exception e) {
            throw new JwtBuilderException(
                String.format("Unable to build signed jwt token for clientId=%s with [signingKey=%s]",
                    clientId, key.getId()),
                e);
        }
    }

    private String createEncryptedJWT() throws JwtBuilderException {
        try {
            Algorithm algorithm = Algorithm.parse(key.getAlgorithm().name());
            Key encryptingKey = JwtAlgorithm.valueOf(key.getAlgorithm().name())
                .convert(keyProviderService.getKey(key).getBytes(StandardCharsets.ISO_8859_1));
            SecuredJwtEncoder securedEncoder = SecuredJwtEncoderProvider.createEncryptingEncoder(algorithm)
                .withEncryptionMethod(encryptionMethod)
                .withKey(encryptingKey)
                .build();
            return com.extole.common.jwt.encode.JwtBuilder.newEncoder()
                .withKeyId(key.getPartnerKeyId())
                .withType(Header.JWT_TYPE)
                .withClaims(claims)
                .withHeaders(headers)
                .withSecuredEncoder(securedEncoder)
                .encode();
        } catch (Exception e) {
            throw new JwtBuilderException(
                String.format("Unable to build encrypted jwt token for clientId=%s with [encryptionKey=%s]",
                    clientId, key.getId()),
                e);
        }
    }

    private String createUnsecuredJWT() {
        return com.extole.common.jwt.encode.JwtBuilder.newEncoder()
            .withHeaders(headers)
            .withClaims(claims)
            .encode();
    }
}
