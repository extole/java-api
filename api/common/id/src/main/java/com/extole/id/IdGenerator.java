package com.extole.id;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;

public final class IdGenerator implements Serializable {
    private static final int APPROXIMATE_BITS_PER_MAX_RADIX = 5;
    private static final int STRING_LENGTH = 14;
    private final SecureRandom random = new SecureRandom();

    public <T> Id<T> generateId() {

        return Id.valueOf(Long.toString(Instant.now().getEpochSecond(), Character.MAX_RADIX)
            + new BigInteger(APPROXIMATE_BITS_PER_MAX_RADIX * STRING_LENGTH, random).toString(Character.MAX_RADIX));
    }
}
