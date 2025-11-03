package com.extole.id;

import java.security.SecureRandom;
import java.time.Instant;

public final class InstantPlusRandom<T> implements DeprecatedIdGenerator<Id<T>> {

    private static final long LOW_BIT_MASK = 0xffffffffL;
    private static final int HIGH_BITS_OFFSET = 32;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Id<T> generateId() {
        Long randomlyGeneratedId = Long.valueOf(((Instant.now().getEpochSecond() << HIGH_BITS_OFFSET) +
            (RANDOM.nextInt() & LOW_BIT_MASK)));

        return Id.valueOf(randomlyGeneratedId.toString());
    }
}
