package com.extole.common.variant;

import java.util.List;

public interface VariantService {

    String selectVariant(
        String defaultVariant,
        List<String> enabledVariants);

    String selectVariant(String defaultVariant,
        List<String> enabledVariants,
        List<String> preferredVariants);
}
