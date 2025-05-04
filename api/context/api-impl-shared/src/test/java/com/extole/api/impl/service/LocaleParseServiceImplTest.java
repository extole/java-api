package com.extole.api.impl.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.api.service.LocaleParseService;
import com.extole.common.log.execution.NoOpExecutionLoggerFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestVariantConfig.class})

class LocaleParseServiceImplTest {

    @Autowired
    private com.extole.common.variant.LocaleParseService internalLocaleParseService;

    @Test
    public void testSimpleLocaleRemainsUnchanged() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        String locale = "en";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly(locale);
    }

    @Test
    public void testLocaleWithUnderscoreIsAccepted() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        String locale = "en_US";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly(locale);
    }

    @Test
    public void testLocaleWithDashIsAccepted() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        String locale = "en-US";
        Assertions.assertThat(localeParseService.parse(locale))
            .containsExactly("en_US");
    }

    @Test
    public void testWeightedLocalesAreAcceptedAndSortedByPriority() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        String locales = "en;q=0.1,fr;q=0.5";
        Assertions.assertThat(localeParseService.parse(locales))
            .containsExactly("fr", "en");
    }

    @Test
    public void testLocaleCanGenerateMultipleRanges() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        String locales = "fr-FR";
        Assertions.assertThat(localeParseService.parse(locales))
            .containsExactly("fr_FR", "fr_FX");
    }

    @Test
    public void testEmptyInput() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        Assertions.assertThat(localeParseService.parse("")).isEmpty();
        Assertions.assertThat(localeParseService.parse(null)).isNull();
    }

    @Test
    public void testInvalidLocaleIsParsedAsNull() {
        LocaleParseService localeParseService =
            new LocaleParseServiceImpl(internalLocaleParseService, NoOpExecutionLoggerFactory.newInstance());
        Assertions.assertThat(localeParseService.parse("blah!")).isNull();
    }

}
