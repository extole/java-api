package com.extole.common.blacklist;

public final class BlacklistFactory {
    private BlacklistFactory() {
        throw new AssertionError();
    }

    public static BlacklistStrategy valueOf(BlacklistType blacklistType) {
        return valueOf(blacklistType, true);
    }

    public static BlacklistStrategy valueOf(BlacklistType blacklistType, boolean onlyWholeWords) {
        if (BlacklistType.NAUGHTY_WORDS == blacklistType) {
            return new NaughtyWordsBlacklistStrategyImpl(onlyWholeWords);
        }
        if (BlacklistType.RESERVED_WORDS == blacklistType) {
            return new ReservedWordsBlacklistStrategyImpl(onlyWholeWords);
        }
        throw new IllegalArgumentException("Implementation for " + blacklistType + " is not found");
    }

}
