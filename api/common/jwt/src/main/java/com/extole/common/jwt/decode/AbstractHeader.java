package com.extole.common.jwt.decode;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;

public abstract class AbstractHeader implements Header {

    private static final String DEFAULT_JWT_TYPE = "JWT";

    private final Algorithm algorithm;
    private final String type;
    private final Map<String, Object> headers;

    protected AbstractHeader(Map<String, Object> headers, Set<Algorithm> supportedAlgorithms)
        throws UnsupportedAlgorithmException {
        this.headers = Map.copyOf(filterNulls(headers));

        Object alg = headers.get(Header.ALGORITHM);
        if (alg instanceof String) {
            this.algorithm = Algorithm.parseBySpecName((String) alg);
        } else {
            this.algorithm = Algorithm.NONE;
        }
        if (!supportedAlgorithms.contains(algorithm)) {
            throw new UnsupportedAlgorithmException(algorithm.getSpecName(), supportedAlgorithms);
        }

        type = getStringHeader(Header.TYPE).orElse(DEFAULT_JWT_TYPE);
    }

    @Override
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Optional<Object> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    @Override
    public Map<String, Object> getHeaders() {
        return Map.copyOf(headers);
    }

    protected Optional<String> getStringHeader(String name) {
        return Optional.ofNullable(headers.get(name))
            .filter(header -> header instanceof String)
            .map(headers -> (String) headers);
    }

    private static Map<String, Object> filterNulls(Map<String, Object> headers) {
        return headers.entrySet().stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null)
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }
}
