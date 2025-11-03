package com.extole.common.blacklist;

import static com.extole.common.blacklist.BlacklistType.NAUGHTY_WORDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NaughtyBlacklistTest {
    private static final String NAUGHTY_WORD_1 = "damn";
    private static final String NAUGHTY_WORD_2 = "whop";

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {
        NAUGHTY_WORD_1,
        NAUGHTY_WORD_2,
    })
    public void testNaughtyBlacklistedWholeWord(String value) {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(NAUGHTY_WORDS, true);
        BlacklistedResult result = blacklist.verify(value);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(value);
    }

    @Test
    public void testNaughtyBlacklistedWholeWordDifferentCase() {
        String lowerCaseWord = NAUGHTY_WORD_1.toLowerCase();
        String upperCaseWord = NAUGHTY_WORD_1.toUpperCase();

        BlacklistStrategy blacklist = BlacklistFactory.valueOf(NAUGHTY_WORDS, true);

        BlacklistedResult result = blacklist.verify(lowerCaseWord);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(NAUGHTY_WORD_1);

        result = blacklist.verify(upperCaseWord);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(NAUGHTY_WORD_1);
    }

    @Test
    public void testShouldBlacklistDisallowedPartialWords() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(NAUGHTY_WORDS, false);
        assertThat(blacklist.verify("asdfasdfasdf" + NAUGHTY_WORD_1 + "asdfasfdasf").isBlacklisted()).isTrue();
    }

    @Test
    public void testShouldAllowNonBlacklistedPartialWords() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(NAUGHTY_WORDS, false);
        assertThat(blacklist.verify("asdfasdfsaveasdfasdfads").isBlacklisted()).isFalse();
    }

    @Test
    public void testStringThatContainsMultipleBlacklistedWords() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(NAUGHTY_WORDS, false);
        BlacklistedResult result = blacklist.verify("abc" + NAUGHTY_WORD_1 + "abc" + NAUGHTY_WORD_2 + "abc");
        assertThat(result.isBlacklisted())
            .isTrue();
        assertThat(result.getBlacklistedElements())
            .containsExactly(NAUGHTY_WORD_1, NAUGHTY_WORD_2);
    }
}
