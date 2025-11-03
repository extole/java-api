package com.extole.common.blacklist;

import static com.extole.common.blacklist.BlacklistType.NAUGHTY_WORDS;
import static com.extole.common.blacklist.BlacklistType.RESERVED_WORDS;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class Blacklist {
    private static final Logger LOG = LoggerFactory.getLogger(Blacklist.class);

    private final List<BlacklistStrategy> strategies;

    @Autowired
    public Blacklist(@Value("${blacklist.onlyWholeWords:true}") boolean onlyWholeWords) {
        this.strategies = ImmutableList.of(
            BlacklistFactory.valueOf(NAUGHTY_WORDS, onlyWholeWords),
            BlacklistFactory.valueOf(RESERVED_WORDS, onlyWholeWords));
    }

    public boolean isBlacklisted(String word) {
        Optional<BlacklistedResult> blacklistResult = this.strategies.stream()
            .map(strategy -> strategy.verify(word))
            .filter(result -> result.isBlacklisted())
            .findFirst();

        blacklistResult.ifPresent(value -> LOG.debug("Blacklist result: {} for the word: {}", value, word));
        return blacklistResult.isPresent();
    }

    public BlacklistedResult getBlacklistResult(String word) {
        return this.strategies.stream()
            .map(strategy -> strategy.verify(word))
            .filter(value -> value.isBlacklisted())
            .findFirst()
            .orElseThrow();
    }

    public List<BlacklistedResult> getAllBlacklistResults(String word) {
        return this.strategies.stream()
            .map(strategy -> strategy.verify(word))
            .filter(value -> value.isBlacklisted())
            .collect(Collectors.toUnmodifiableList());
    }
}
