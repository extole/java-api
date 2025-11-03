package com.extole.common.blacklist;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class ReservedWordsBlacklistStrategyImpl implements BlacklistStrategy {
    private static final String RESOURCE_FILE = "/reserved_words.txt";
    private static final List<String> LOWERCASE_RESERVED_WORDS = ResourceUtils.readLinesOfResourceFile(RESOURCE_FILE)
        .stream()
        .map(value -> value.toLowerCase())
        .collect(Collectors.toUnmodifiableList());

    private final boolean onlyWholeWords;

    ReservedWordsBlacklistStrategyImpl() {
        this(false);
    }

    ReservedWordsBlacklistStrategyImpl(boolean onlyWholeWords) {
        this.onlyWholeWords = onlyWholeWords;
    }

    @Override
    public BlacklistedResult verify(String value) {
        Objects.requireNonNull(value);
        String lowercaseValue = value.toLowerCase();

        List<String> matched = new LinkedList<>();
        if (this.onlyWholeWords) {
            LOWERCASE_RESERVED_WORDS.forEach(reservedValue -> {
                if (lowercaseValue.equals(reservedValue)) {
                    matched.add(reservedValue);
                }
            });
        } else {
            LOWERCASE_RESERVED_WORDS.forEach(reservedValue -> {
                if (lowercaseValue.startsWith(reservedValue)) {
                    matched.add(reservedValue);
                }
            });
        }
        return new BlacklistedResult(matched, BlacklistType.RESERVED_WORDS);
    }

}
