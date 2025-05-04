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

    /**
     * Sets the encryption method for the JSON Web Token (JWT).
     * The encryption method is ignored if non encrypted token is created.
     *
     * @param encryptionMethod the encryption method for the JWT. Supported values are:
     *            <ul>
     *            <li>A128CBC_HS256</li>
     *            <li>A192CBC_HS384</li>
     *            <li>A256CBC_HS512</li>
     *            <li>A128GCM</li>
     *            <li>A192GCM</li>
     *            <li>A256GCM</li>
     *            <li>XC20P</li>
     *            </ul>
     * @return the JwtBuilder instance
     * @throws JwtBuilderException if the provided string is not a supported encryption method
     */
    JwtBuilder withEncryptionMethod(String encryptionMethod) throws JwtBuilderException;

    String build() throws JwtBuilderException;

    enum EncryptionMethodType {
        A128CBC_HS256,
        A192CBC_HS384,
        A256CBC_HS512,
        A128GCM,
        A192GCM,
        A256GCM,
        XC20P,
    }
}
