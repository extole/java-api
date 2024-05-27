package com.extole.api.service;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A builder class for constructing JSON Web Tokens (JWT)
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7519">JSON Web Token (JWT)</a>
 *
 */
@Schema
public interface JwtBuilder {

    JwtBuilder withHeaderParameter(String name, String value);

    JwtBuilder withCustomClaim(String name, Object value);

    JwtBuilder withIssuer(String issuer);

    JwtBuilder withAudiences(List<String> audienceList);

    JwtBuilder withAudience(String audience);

    JwtBuilder withExpirationDate(Long secondsSince1970);

    JwtBuilder withSubject(String subject);

    JwtBuilder withNotBefore(Long secondsSince1970);

    JwtBuilder withIssuedAt(Long secondsSince1970);

    JwtBuilder withJwtId(String jwtId);

    @Deprecated // TODO migrate to withClientKeyId and remove ENG-22470
    JwtBuilder signWithKeyByClientKeyId(String extoleClientKeyId) throws JwtBuilderException;

    @Deprecated // TODO migrate to withClientKeyId and remove ENG-22470
    JwtBuilder signWithKeyByPartnerKeyId(String partnerKeyId) throws JwtBuilderException;

    JwtBuilder withClientKeyId(String clientKeyId) throws JwtBuilderException;

    String build() throws JwtBuilderException;

}
