package com.extole.api.impl.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.extole.api.service.VariantService;
import com.extole.common.log.execution.ExecutionLoggerFactory;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestVariantConfig.class})
public class VariantServiceImplTest {
    @Autowired
    private com.extole.common.variant.VariantService internalVariantService;

    @Test
    public void testVariantEvaluatedToDefaultOnEmptyEnabledVariants() {
        VariantService variantService =
            new VariantServiceImpl(internalVariantService, ExecutionLoggerFactory.newInstance());
        String preferredVariant = "fr";
        String defaultVariant = "en";
        String[] enabledVariants = new String[0];
        Assertions.assertThat(variantService.selectVariant(enabledVariants, new String[] {preferredVariant}))
            .isEqualTo(defaultVariant);
    }

    @Test
    void testVariantEvaluatedToDefaultWhenPreferredNotInEnabledVariants() {
        VariantService variantService =
            new VariantServiceImpl(internalVariantService, ExecutionLoggerFactory.newInstance());
        String preferredVariant = "fr";
        String defaultVariant = "en";
        String[] enabledVariants = new String[] {defaultVariant, "ja", "de"};
        Assertions.assertThat(variantService.selectVariant(enabledVariants, new String[] {preferredVariant}))
            .isEqualTo(defaultVariant);
    }

    @Test
    void testVariantEvaluatedToPreferredWhenInEnabledVariants() {
        VariantService variantService =
            new VariantServiceImpl(internalVariantService, ExecutionLoggerFactory.newInstance());
        String preferredVariant = "fr";
        String[] enabledVariants = new String[] {"en", "ja", "fr", "de"};
        Assertions.assertThat(variantService.selectVariant(enabledVariants, new String[] {preferredVariant}))
            .isEqualTo(preferredVariant);
    }

    @Test
    void testVariantEvaluatedToMostPreferredWhenInEnabledVariants() {
        VariantService variantService =
            new VariantServiceImpl(internalVariantService, ExecutionLoggerFactory.newInstance());
        String[] preferredVariants = new String[] {"de", "fr"};
        String[] enabledVariants = new String[] {"ja", "fr", "de"};
        Assertions.assertThat(variantService.selectVariant(enabledVariants, preferredVariants))
            .isEqualTo("de");
    }
}
