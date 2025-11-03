package com.extole.common.jwt.decode;

import java.security.Key;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.encrypt.EncryptedUncheckedJwtFactory;
import com.extole.common.jwt.decode.sign.SignedUncheckedJwtFactory;

public final class JwtDecoder {

    private static final Map<Set<Algorithm>, UncheckedJwtFactory> UNCHECKED_JWT_FACTORIES = Map.of(
        SignedJwtAlgorithm.SUPPORTED_ALGORITHMS, new SignedUncheckedJwtFactory(),
        EncryptedJwtAlgorithm.SUPPORTED_ALGORITHMS, new EncryptedUncheckedJwtFactory(),
        Set.of(Algorithm.NONE), new PlainUncheckedJwtFactory());

    private JwtDecoder() {
    }

    public static SecuredJwtDecoderBuilder newDecoder(String encodedJwt)
        throws JwtParseException, UnsupportedAlgorithmException {
        return new SecuredJwtDecoderBuilderImpl(parseJwtString(encodedJwt));
    }

    public static PlainJwtDecoderBuilder newPlainDecoder(String encodedJwt) throws JwtParseException {
        return new PLainJwtDecoderBuilderImpl(parsePlainJwtString(encodedJwt));
    }

    private static UncheckedJwt parseJwtString(String encodedJwtString)
        throws JwtParseException, UnsupportedAlgorithmException {
        try {
            JWT jwt = JWTParser.parse(encodedJwtString);
            Algorithm algorithm = Algorithm.parseBySpecName(jwt.getHeader().getAlgorithm().getName());
            return UNCHECKED_JWT_FACTORIES.entrySet().stream()
                .filter(entry -> entry.getKey().contains(algorithm))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElseThrow(() -> new UnsupportedAlgorithmException(algorithm.getSpecName(),
                    UNCHECKED_JWT_FACTORIES.keySet().stream().flatMap(set -> set.stream()).collect(Collectors.toSet())))
                .createUncheckedJwt(encodedJwtString);
        } catch (ParseException e) {
            throw new JwtParseException("Could not parse JWT", e);
        }
    }

    private static PlainUncheckedJwt parsePlainJwtString(String encodedJwtString) throws JwtParseException {
        try {
            UncheckedJwt parsedJwt = parseJwtString(encodedJwtString);
            Algorithm algorithm = parsedJwt.getHeader().getAlgorithm();
            if (!Algorithm.NONE.equals(algorithm)) {
                throw new JwtParseException(
                    "Cannot parse jwt encoded with algorithm: " + algorithm.getSpecName() + " as plain jwt");
            }
            return (PlainUncheckedJwt) parsedJwt;
        } catch (UnsupportedAlgorithmException e) {
            throw new JwtParseException(
                "Cannot parse jwt encoded with algorithm: " + e.getAlgorithm() + " as plain jwt");
        }
    }

    private static final class PLainJwtDecoderBuilderImpl implements PlainJwtDecoderBuilder {

        private final PlainUncheckedJwt uncheckedJwt;

        private PLainJwtDecoderBuilderImpl(PlainUncheckedJwt uncheckedJwt) {
            this.uncheckedJwt = uncheckedJwt;
        }

        @Override
        public Jwt decode() {
            try {
                return createPlainJwt(uncheckedJwt.getEncodedJwtString());
            } catch (JwtParseException e) {
                throw new JwtRuntimeException("Could not parse encoded JWT", e);
            }
        }
    }

    private static final class SecuredJwtDecoderBuilderImpl implements SecuredJwtDecoderBuilder {

        private final UncheckedJwt uncheckedJwt;
        private final Algorithm algorithm;
        private InternalSecuredJwtDecoderImpl internalSecuredJwtDecoder;

        private SecuredJwtDecoderBuilderImpl(UncheckedJwt uncheckedJwt) {
            this.uncheckedJwt = uncheckedJwt;
            this.algorithm = uncheckedJwt.getHeader().getAlgorithm();
        }

        @Override
        public SecuredJwtDecoderBuilder withKey(Key decodingKey)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            if (decodingKey == null || Algorithm.NONE.equals(algorithm)) {
                return this;
            }
            try {
                this.internalSecuredJwtDecoder = createInternalSecuredJwtDecoder(algorithm, decodingKey);
            } catch (UnsupportedAlgorithmException e) {
                throw new JwtRuntimeException(
                    "Could not create internal jwt decoder for algorithm: " + algorithm.getSpecName(), e);
            }
            return this;
        }

        @Override
        public Jwt decode() throws MissingDecodingKeyException, InvalidSecuredJwtDecoderException {
            if (Algorithm.NONE.equals(algorithm)) {
                try {
                    return createPlainJwt(uncheckedJwt.getEncodedJwtString());
                } catch (JwtParseException e) {
                    throw new JwtRuntimeException("Could not parse encoded JWT", e);
                }
            }

            if (internalSecuredJwtDecoder == null) {
                throw new MissingDecodingKeyException();
            }
            return internalSecuredJwtDecoder.toDecodedObject(uncheckedJwt);
        }

        @Override
        public UncheckedJwt decodeUnchecked() {
            return uncheckedJwt;
        }

        private static InternalSecuredJwtDecoderImpl createInternalSecuredJwtDecoder(Algorithm algorithm,
            Key decodingKey) throws UnsupportedAlgorithmException, InvalidDecodingKeyTypeException,
            InvalidDecodingKeyAlgorithmException {
            try {
                return InternalSecuredJwtDecoderImpl.builder(algorithm).withKey(decodingKey).build();
            } catch (JwtDecoderBuildException e) {
                throw new JwtRuntimeException("Failed to create internal secured jwt decoder", e);
            }
        }
    }

    private static Jwt createPlainJwt(String encodedJwtString) throws JwtParseException {
        try {
            PlainJWT jwt = PlainJWT.parse(encodedJwtString);
            return new PlainJwt(new PlainJwtHeader(jwt.getHeader().toJSONObject()),
                jwt.getPayload().toJSONObject());
        } catch (ParseException | UnsupportedAlgorithmException e) {
            throw new JwtParseException("Could not parse encoded JWT as plain jwt", e);
        }
    }
}
