package com.extole.common.blacklist;

import static com.extole.common.blacklist.BlacklistType.RESERVED_WORDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ReservedBlacklistTest {
    private static final String RESERVED_WORD_1 = "login";
    private static final String RESERVED_WORD_2 = "terms";

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @ValueSource(strings = {RESERVED_WORD_1, RESERVED_WORD_2,
    })
    public void testReservedBlacklistedWholeWord(String value) {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(RESERVED_WORDS, true);
        BlacklistedResult result = blacklist.verify(value);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(value);
    }

    @Test
    public void testReservedBlacklistedWholeWordCaseInsensitive() {
        String lowerCaseWord = RESERVED_WORD_1.toLowerCase();
        String upperCaseWord = RESERVED_WORD_1.toUpperCase();

        BlacklistStrategy blacklist = BlacklistFactory.valueOf(RESERVED_WORDS, true);

        BlacklistedResult result = blacklist.verify(lowerCaseWord);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(RESERVED_WORD_1);

        result = blacklist.verify(upperCaseWord);
        assertThat(result.isBlacklisted()).isTrue();
        assertThat(result.getBlacklistedElements()).containsExactly(RESERVED_WORD_1);
    }

    @Test
    public void testBlacklistedWordsInsideRegularString() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(RESERVED_WORDS, false);
        assertThat(blacklist.verify("asdfasdfasdf" + RESERVED_WORD_1 + "asdfasfdasf").isBlacklisted()).isFalse();
        assertThat(blacklist.verify(RESERVED_WORD_1 + "asdfasfdasf").isBlacklisted()).isTrue();
        assertThat(blacklist.verify("asdfasfdasf" + RESERVED_WORD_1).isBlacklisted()).isFalse();
    }

    @Test
    public void testShouldAllowNonBlacklistedPartialWords() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(RESERVED_WORDS, false);
        assertThat(blacklist.verify("asdfasdfsaveasdfasdfads").isBlacklisted()).isFalse();
    }

    @Test
    public void testStringThatContainsMultipleBlacklistedWords() {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(RESERVED_WORDS, false);
        BlacklistedResult result = blacklist.verify(RESERVED_WORD_1 + "abc" + RESERVED_WORD_2 + "abc");
        assertThat(result.isBlacklisted())
            .isTrue();
        assertThat(result.getBlacklistedElements()).containsExactlyInAnyOrder(RESERVED_WORD_1);
    }

    @EnumSource(BlacklistType.class)
    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    public void testBlacklistNullIsNotAllowed(BlacklistType type) {
        BlacklistStrategy blacklist = BlacklistFactory.valueOf(type, false);
        assertThatThrownBy(() -> blacklist.verify(null))
            .isInstanceOf(NullPointerException.class);
    }
}
