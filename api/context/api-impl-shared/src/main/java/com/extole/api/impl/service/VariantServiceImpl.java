package com.extole.api.impl.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.extole.api.service.VariantService;
import com.extole.common.log.execution.ExecutionLogger;

public class VariantServiceImpl implements VariantService {
    private static final String DEFAULT_VARIANT = "en";
    private final com.extole.common.variant.VariantService variantService;
    private final ExecutionLogger executionLogger;

    public VariantServiceImpl(com.extole.common.variant.VariantService variantService,
        ExecutionLogger executionLogger) {
        this.variantService = variantService;
        this.executionLogger = executionLogger;
    }

    @Override
    public String selectVariant(String[] enabledVariants, @Nullable String[] preferredVariants) {
        List<String> variants = Arrays.asList(enabledVariants);
        String defaultVariant = variants.isEmpty()
            ? DEFAULT_VARIANT
            : variants.get(0);
        String selectedVariant =
            preferredVariants == null
                ? variantService.selectVariant(defaultVariant, variants)
                : variantService.selectVariant(defaultVariant, variants, Arrays.asList(preferredVariants));

        executionLogger.log(
            "Selected variant: " + selectedVariant + " based on preferred variants: " +
                Arrays.toString(preferredVariants) + ", default variant: " + defaultVariant + ", and enabled variants: "
                + Arrays.toString(enabledVariants));

        return selectedVariant;
    }
}
