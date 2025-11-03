package com.extole.common.blacklist;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public final class BlacklistedResult {
    private final List<String> blacklistedElements;
    private final BlacklistType blacklistType;

    BlacklistedResult(List<String> blacklistedElements,
        BlacklistType blacklistType) {
        this.blacklistedElements = ImmutableList.copyOf(Objects.requireNonNull(blacklistedElements));
        this.blacklistType = blacklistType;
    }

    public List<String> getBlacklistedElements() {
        return blacklistedElements;
    }

    public BlacklistType getBlacklistType() {
        return blacklistType;
    }

    public boolean isBlacklisted() {
        return !blacklistedElements.isEmpty();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
