package com.extole.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public enum HashAlgorithm {
    NONE(Optional.empty()), SHA1(Optional.of(Hashing.sha1())), SHA256(Optional.of(Hashing.sha256()));

    private final Optional<HashFunction> hashFunction;
    private final String hashMarker;

    HashAlgorithm(Optional<HashFunction> hashFunction) {
        this.hashFunction = hashFunction;
        this.hashMarker = "HASHED_" + name();
    }

    public String getHashMarker() {
        return hashMarker;
    }

    private Optional<HashFunction> getHashFunction() {
        return hashFunction;
    }

    public String hashString(String plainString) {
        if (Strings.isNullOrEmpty(plainString) || !this.getHashFunction().isPresent()) {
            return plainString;
        }
        String hash = this.getHashFunction().get()
            .hashString(plainString, StandardCharsets.UTF_8)
            .toString();
        return hashMarker + "(" + hash + ")";
    }

}
