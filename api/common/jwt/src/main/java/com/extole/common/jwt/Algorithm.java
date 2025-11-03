package com.extole.common.jwt;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.extole.common.lang.ToString;

public class Algorithm {

    public static final Algorithm NONE = new Algorithm("NONE", "NONE", 0, false);

    private final String name;
    private final String specName;
    private final String jcaName;
    private final int size;
    private final boolean isSymmetric;

    public Algorithm(String specName, String jcaName, int size, boolean isSymmetric) {
        this.name = specName;
        this.specName = specName;
        this.jcaName = jcaName;
        this.size = size;
        this.isSymmetric = isSymmetric;
    }

    public Algorithm(String name, String specName, String jcaName, int size, boolean isSymmetric) {
        this.name = name;
        this.specName = specName;
        this.jcaName = jcaName;
        this.size = size;
        this.isSymmetric = isSymmetric;
    }

    public String getName() {
        return name;
    }

    public String getSpecName() {
        return specName;
    }

    public String getJcaName() {
        return jcaName;
    }

    public int getSize() {
        return size;
    }

    public boolean isSymmetric() {
        return isSymmetric;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Algorithm algorithm = (Algorithm) object;
        return Objects.equals(name, algorithm.name) && Objects.equals(specName, algorithm.specName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specName);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Algorithm parse(String algorithmName) throws UnsupportedAlgorithmException {
        Optional<Algorithm> algorithm = Optional.ofNullable(algorithmName)
            .filter(name -> name.equalsIgnoreCase(NONE.getName()))
            .map(name -> NONE);
        if (algorithm.isPresent()) {
            return algorithm.get();
        }

        try {
            return SignedJwtAlgorithm.parse(algorithmName);
        } catch (UnsupportedAlgorithmException e) {
            try {
                return EncryptedJwtAlgorithm.parse(algorithmName);
            } catch (UnsupportedAlgorithmException ex) {
                Set<Algorithm> supportedAlgorithms = new HashSet<>();
                supportedAlgorithms.addAll(SignedJwtAlgorithm.SUPPORTED_ALGORITHMS);
                supportedAlgorithms.addAll(EncryptedJwtAlgorithm.SUPPORTED_ALGORITHMS);
                throw new UnsupportedAlgorithmException(algorithmName, Set.copyOf(supportedAlgorithms));
            }
        }
    }

    public static Algorithm parseBySpecName(String algorithmSpecName) throws UnsupportedAlgorithmException {
        Optional<Algorithm> algorithm = Optional.ofNullable(algorithmSpecName)
            .filter(name -> name.equalsIgnoreCase(NONE.getSpecName()))
            .map(name -> NONE);
        if (algorithm.isPresent()) {
            return algorithm.get();
        }

        try {
            return SignedJwtAlgorithm.parseBySpecName(algorithmSpecName);
        } catch (UnsupportedAlgorithmException e) {
            try {
                return EncryptedJwtAlgorithm.parseBySpecName(algorithmSpecName);
            } catch (UnsupportedAlgorithmException ex) {
                Set<Algorithm> supportedAlgorithms = new HashSet<>();
                supportedAlgorithms.addAll(SignedJwtAlgorithm.SPEC_SUPPORTED_ALGORITHMS);
                supportedAlgorithms.addAll(EncryptedJwtAlgorithm.SPEC_SUPPORTED_ALGORITHMS);
                throw new UnsupportedAlgorithmException(algorithmSpecName, Set.copyOf(supportedAlgorithms));
            }
        }
    }

    static Algorithm parse(String nameToParse, Function<Algorithm, String> nameProvider,
        Set<Algorithm> algorithmsToConsider) throws UnsupportedAlgorithmException {
        Map<String, Algorithm> supportedAlgorithmsMappedByName = algorithmsToConsider.stream()
            .collect(Collectors.toUnmodifiableMap(alg -> nameProvider.apply(alg).toUpperCase(), Function.identity()));
        return Optional.ofNullable(nameToParse)
            .filter(name -> StringUtils.isNotBlank(name))
            .map(name -> supportedAlgorithmsMappedByName.get(name.toUpperCase()))
            .orElseThrow(() -> new UnsupportedAlgorithmException(nameToParse, algorithmsToConsider));
    }
}
