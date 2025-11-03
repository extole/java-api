package com.extole.id;

public class TypedIdGenerator {

    private static final DeprecatedIdGenerator<Long> EPOCH_PLUS_RANDOM_GENERATOR = new EpochPlusRandom();

    public <T> Id<T> generateId() {
        return Id.valueOf(String.valueOf(EPOCH_PLUS_RANDOM_GENERATOR.generateId()));
    }
}
