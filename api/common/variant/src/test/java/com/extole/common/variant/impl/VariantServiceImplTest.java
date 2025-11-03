package com.extole.common.variant.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.extole.common.variant.VariantService;

public class VariantServiceImplTest {

    private final VariantService variantService = new VariantServiceImpl();

    @Test
    public void testVariantEvaluatedToDefaultOnEmptyEnabledVariants() {
        String defaultVariant = "en";
        Assertions.assertThat(variantService.selectVariant(defaultVariant, new ArrayList<>(), Lists.newArrayList("fr")))
            .isEqualTo(defaultVariant);
        Assertions.assertThat(variantService.selectVariant(defaultVariant, new ArrayList<>()))
            .isEqualTo(defaultVariant);
    }

    @Test
    void testVariantEvaluatedToDefaultWhenPreferredNotInEnabledVariants() {
        String preferredVariant = "fr";
        String defaultVariant = "en";
        List<String> enabledVariants = Lists.newArrayList(defaultVariant, "de");
        Assertions.assertThat(variantService.selectVariant(defaultVariant, enabledVariants,
            Lists.newArrayList(preferredVariant)))
            .isEqualTo(defaultVariant);
    }

    @Test
    void testVariantEvaluatedToPreferredWhenInEnabledVariants() {
        String preferredVariant = "fr";
        List<String> enabledVariants = Lists.newArrayList("en", "ja", "fr", "de");
        Assertions.assertThat(variantService.selectVariant("en", enabledVariants, Lists.newArrayList(preferredVariant)))
            .isEqualTo(preferredVariant);
    }

    @Test
    void testPreferredVariantIsNotParsed() {
        String preferredVariant = "ro-RO";
        String defaultVariant = "en";
        List<String> enabledVariants = Lists.newArrayList(defaultVariant, "ja", "fr", "ro_RO");
        Assertions.assertThat(
            variantService.selectVariant(defaultVariant, enabledVariants, Lists.newArrayList(preferredVariant)))
            .isEqualTo(defaultVariant);
    }

    @Test
    void testVariantEvaluatedToMostPreferredWhenInEnabledVariants() {
        List<String> enabledVariants = Lists.newArrayList("ja", "fr", "de");
        Assertions.assertThat(variantService.selectVariant("ja", enabledVariants, Lists.newArrayList("de", "fr")))
            .isEqualTo("de");
    }

    @Test
    void testVariantEvaluatedToEmptyWhenDefaultAndEnabledVariantsAreMissing() {
        String preferredVariant = "fr";
        Assertions.assertThat(variantService.selectVariant(null, new ArrayList<>(),
            Lists.newArrayList(preferredVariant)))
            .isNull();
    }

    @Test
    void testVariantEvaluatedToEmptyWhenPreferredAndDefaultAndEnabledVariantsAreMissing() {
        Assertions.assertThat(variantService.selectVariant(null, new ArrayList<>())).isNull();
        Assertions.assertThat(variantService.selectVariant("", new ArrayList<>())).isEmpty();
    }
}
