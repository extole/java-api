package com.extole.common.jwt.decode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Payload {

    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String AUDIENCE = "aud";
    public static final String EXPIRATION_TIME = "exp";
    public static final String NOT_BEFORE = "nbf";
    public static final String ISSUED_AT = "iat";
    public static final String JWT_ID = "jti";

    private final Map<String, Object> payload;

    public Payload(Map<String, Object> payload) {
        this.payload = Map.copyOf(filterNulls(payload));
    }

    public Map<String, Object> getPayloadMap() {
        return payload;
    }

    public Optional<Object> get(String key) {
        return getValueAsOptional(key);
    }

    public Optional<String> getJwtId() {
        return getStringValueAsOptional(JWT_ID);
    }

    public Optional<String> getIssuer() {
        return getStringValueAsOptional(ISSUER);
    }

    public Optional<String> getSubject() {
        return getStringValueAsOptional(SUBJECT);
    }

    public List<String> getAudience() {
        Optional<Object> audience = getValueAsOptional(AUDIENCE);
        if (audience.isEmpty()) {
            return List.of();
        }
        return PayloadClaimsParser.parseList(audience.get());
    }

    private Optional<Object> getValueAsOptional(String key) {
        return Optional.ofNullable(payload.get(key));
    }

    private Optional<String> getStringValueAsOptional(String key) {
        return getValueAsOptional(key)
            .map(value -> value.toString());
    }

    private static Map<String, Object> filterNulls(Map<String, Object> headers) {
        return headers.entrySet().stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null)
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }
}
