package com.extole.common.jwt.decode;

import java.util.Map;
import java.util.Optional;

import com.extole.common.jwt.Algorithm;

public interface Header {

    String JWT_TYPE = "JWT";

    String ALGORITHM = "alg";
    String ENCRYPTION_ALGORITHM = "enc";
    String COMPRESSION_ALGORITHM = "zip";
    String JWK_SET_URL = "jku";
    String JWK = "jwk";
    String KEY_ID = "kid";
    String X_509_CERT_URL = "x5u";
    String X_509_CERT_CHAIN = "x5c";
    String X_509_CERT_SHA_1_THUMBPRINT = "x5t";
    String X_509_CERT_SHA_256_THUMBPRINT = "x5t#S256";
    String TYPE = "typ";
    String CONTENT_TYPE = "cty";
    String CRITICAL = "crit";
    String EPHEMERAL_PUBLIC_KEY = "epk";
    String AGREEMENT_PARTY_U_INFO = "apu";
    String AGREEMENT_PARTY_V_INFO = "apv";
    String INITIALIZATION_VECTOR = "iv";
    String AUTHENTICATION_TAG = "tag";
    String PBES2_SALT_INPUT = "p2s";
    String PBES2_COUNT = "p2c";
    String SENDER_KEY_ID = "skid";
    String BASE64_URL_ENCODE_PAYLOAD = "b64";

    Algorithm getAlgorithm();

    String getType();

    Optional<Object> getHeader(String name);

    Map<String, Object> getHeaders();
}
