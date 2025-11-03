package com.extole.common.variant.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.extole.common.variant.InvalidLocaleException;
import com.extole.common.variant.LocaleParseService;

public class LocaleParseServiceImplTest {

    private final LocaleParseService localeParseService = new LocaleParseServiceImpl();

    @Test
    public void testSimpleLocaleRemainsUnchanged() throws Exception {
        String locale = "en";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly(locale);
    }

    @Test
    public void testLocaleWithUnderscoreIsAccepted() throws Exception {
        String locale = "en_US";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly(locale);
    }

    @Test
    public void testLocaleWithDashIsAccepted() throws Exception {
        String locale = "en-US";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly("en_US");
    }

    @Test
    public void testWeightedLocalesAreAcceptedAndSortedByPriority() throws Exception {
        String locales = "en;q=0.1,fr;q=0.5";
        Assertions.assertThat(localeParseService.parse(locales))
            .containsExactly("fr", "en");
    }

    @Test
    public void testLocaleCanGenerateMultipleRanges() throws Exception {
        String locales = "fr-FR";
        Assertions.assertThat(localeParseService.parse(locales))
            .containsExactly("fr_FR", "fr_FX");
    }

    @Test
    public void testEmptyInput() throws Exception {
        Assertions.assertThat(localeParseService.parse("")).isEmpty();
        Assertions.assertThat(localeParseService.parse(null)).isEmpty();
    }

    @Test
    public void testInvalidLocaleCannotBeParsed() {
        assertThrows(InvalidLocaleException.class, () -> localeParseService.parse("blah!"));
    }
}
