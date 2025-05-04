package com.extole.client.rest.security.key;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ClientKeyAlgorithm {

    PASSWORD(true),
    HTTP_BASIC(true),
    OAUTH(true),
    OAUTH_GENERIC(true),
    OAUTH_SALESFORCE(true),
    OAUTH_LISTRAK(true),
    OAUTH_OPTIMOVE(true),
    OAUTH_LEAD_PERFECTION(true),
    OAUTH_SFDC(true),
    OAUTH_SFDC_PASSWORD(true),
    SSL_PKCS_12(true),
    A128KW(true),
    A192KW(true),
    A256KW(true),
    HS256(true),
    HS384(true),
    HS512(true),

    GENERIC(false),
    RSA(false),

    RS256_PUBLIC(false),
    RS384_PUBLIC(false),
    RS512_PUBLIC(false),
    RS256_PRIVATE(false),
    RS384_PRIVATE(false),
    RS512_PRIVATE(false),

    PS256_PUBLIC(false),
    PS384_PUBLIC(false),
    PS512_PUBLIC(false),
    PS256_PRIVATE(false),
    PS384_PRIVATE(false),
    PS512_PRIVATE(false),

    RSA_OAEP_256_PUBLIC(false),
    RSA_OAEP_384_PUBLIC(false),
    RSA_OAEP_512_PUBLIC(false),
    RSA_OAEP_256_PRIVATE(false),
    RSA_OAEP_384_PRIVATE(false),
    RSA_OAEP_512_PRIVATE(false),

    ES256_PUBLIC(false),
    ES384_PUBLIC(false),
    ES512_PUBLIC(false),
    ES256_PRIVATE(false),
    ES384_PRIVATE(false),
    ES512_PRIVATE(false);

    private final boolean isSymmetric;

    ClientKeyAlgorithm(final boolean isSymmetric) {
        this.isSymmetric = isSymmetric;
    }

    public boolean isSymmetric() {
        return isSymmetric;
    }
}
