package com.extole.common.jwt.encode;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.apache.commons.collections4.MapUtils;

import com.extole.common.jwt.decode.Header;
import com.extole.common.jwt.decode.Payload;

public class JwtBuilder {

    public static JwtBuilder newEncoder() {
        return new JwtBuilder();
    }

    private final Map<String, Object> headers = new HashMap<>();
    private final Map<String, Object> claims = new HashMap<>();
    private Optional<SecuredJwtEncoder> encoder = Optional.empty();

    public JwtBuilder withKeyId(String keyId) {
        putHeader(Header.KEY_ID, keyId);
        return this;
    }

    public JwtBuilder withType(String type) {
        putHeader(Header.TYPE, type);
        return this;
    }

    public JwtBuilder withContentType(String contentType) {
        putHeader(Header.CONTENT_TYPE, contentType);
        return this;
    }

    public JwtBuilder withCriticalParameters(Set<String> criticalParameters) {
        putHeader(Header.CRITICAL, criticalParameters);
        return this;
    }

    public JwtBuilder withHeader(String key, Object value) {
        putHeader(key, value);
        return this;
    }

    public JwtBuilder withHeaders(Map<String, Object> headers) {
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(this::putHeader);
        }
        return this;
    }

    public JwtBuilder withIssuer(String issuer) {
        putClaim(Payload.ISSUER, issuer);
        return this;
    }

    public JwtBuilder withAudience(String audience) {
        putClaim(Payload.AUDIENCE, List.of(audience));
        return this;
    }

    public JwtBuilder withAudience(List<String> audiences) {
        putClaim(Payload.AUDIENCE, List.copyOf(audiences));
        return this;
    }

    public JwtBuilder withSubject(String subject) {
        putClaim(Payload.SUBJECT, subject);
        return this;
    }

    public JwtBuilder withExpirationTime(Instant expirationTime) {
        putClaim(Payload.EXPIRATION_TIME, Date.from(expirationTime));
        return this;
    }

    public JwtBuilder withNotBeforeTime(Instant notBeforeTime) {
        putClaim(Payload.NOT_BEFORE, Date.from(notBeforeTime));
        return this;
    }

    public JwtBuilder withIssuedAt(Instant issuedAt) {
        putClaim(Payload.ISSUED_AT, Date.from(issuedAt));
        return this;
    }

    public JwtBuilder withJwtId(String jwtId) {
        putClaim(Payload.JWT_ID, jwtId);
        return this;
    }

    public JwtBuilder withClaim(String key, Object value) {
        putClaim(key, value);
        return this;
    }

    public JwtBuilder withClaims(Map<String, Object> claims) {
        if (MapUtils.isNotEmpty(claims)) {
            claims.forEach(this::putClaim);
        }
        return this;
    }

    public JwtBuilder withSecuredEncoder(SecuredJwtEncoder encoder) {
        this.encoder = Optional.of(encoder);
        return this;
    }

    public String encode() {
        if (encoder.isPresent()) {
            return encoder.get().toEncodedString(Map.copyOf(headers), Map.copyOf(claims));
        } else {
            PlainHeader header = new PlainHeader.Builder().customParams(Map.copyOf(headers)).build();
            JWTClaimsSet.Builder claimsSetJwtEncoder = new JWTClaimsSet.Builder();
            claims.forEach(claimsSetJwtEncoder::claim);
            return new PlainJWT(header, claimsSetJwtEncoder.build()).serialize();
        }
    }

    private void putHeader(String key, Object value) {
        if (key != null && value != null) {
            headers.put(key, value);
        }
    }

    private void putClaim(String key, Object value) {
        if (key != null && value != null) {
            claims.put(key, value);
        }
    }
}
